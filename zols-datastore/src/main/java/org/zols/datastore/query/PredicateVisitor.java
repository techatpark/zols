/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import com.github.rutledgepaulv.qbuilders.nodes.AndNode;
import com.github.rutledgepaulv.qbuilders.nodes.ComparisonNode;
import com.github.rutledgepaulv.qbuilders.nodes.OrNode;
import com.github.rutledgepaulv.qbuilders.operators.ComparisonOperator;
import com.github.rutledgepaulv.qbuilders.visitors.AbstractVoidContextNodeVisitor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static org.zols.datastore.util.MapUtil.getFieldValue;

@SuppressWarnings("WeakerAccess")
public class PredicateVisitor<T>
        extends AbstractVoidContextNodeVisitor<Predicate<T>> {

    /**
     * @param node
     * @return
     */
    @Override
    protected Predicate<T> visit(final AndNode node) {
        return (t) -> node.getChildren().stream().map(this::visitAny)
                .allMatch(p -> p.test(t));
    }

    /**
     * @param node the or node
     * @return node
     */
    @Override
    protected Predicate<T> visit(final OrNode node) {
        return (t) -> node.getChildren().stream().map(this::visitAny)
                .anyMatch(p -> p.test(t));
    }

    /**
     * @param node the node
     * @return node
     */
    @Override
    protected Predicate<T> visit(final ComparisonNode node) {

        ComparisonOperator operator = node.getOperator();

        if (ComparisonOperator.EQ.equals(operator)) {
            return single(node, this::equality);
        } else if (ComparisonOperator.NE.equals(operator)) {
            return single(node, this::inequality);
        } else if (ComparisonOperator.EX.equals(operator)) {
            return ((Boolean) node.getValues().iterator().next())
                    ? exists(node) : doesNotExist(node);
        } else if (ComparisonOperator.GT.equals(operator)) {
            return single(node, this::greaterThan);
        } else if (ComparisonOperator.LT.equals(operator)) {
            return single(node, this::lessThan);
        } else if (ComparisonOperator.GTE.equals(operator)) {
            return single(node, this::greaterThanOrEqualTo);
        } else if (ComparisonOperator.LTE.equals(operator)) {
            return single(node, this::lessThanOrEqualTo);
        } else if (ComparisonOperator.IN.equals(operator)) {
            return multi(node, this::in);
        } else if (ComparisonOperator.NIN.equals(operator)) {
            return multi(node, this::nin);
        } else if (ComparisonOperator.RE.equals(operator)) {
            return single(node, this::regex);
        } else if (ComparisonOperator.SUB_CONDITION_ANY.equals(operator)) {
            Predicate test = condition(node);
            // subquery condition is ignored because a predicate has already
            // been built.
            return single(node,
                    (fieldValue, subQueryCondition) -> this.subquery(
                            fieldValue, test));
        }

        throw new UnsupportedOperationException(
                "This visitor does not support the operator " + operator
                        + ".");
    }

    /**
     * @param actual the actual
     * @param func the func
     * @return boolean
     */
    protected boolean subquery(final Object actual,
                               final Predicate<Object> func) {
        if (actual != null && actual.getClass().isArray()) {
            Object[] values = (Object[]) actual;
            return stream(values).anyMatch(func);
        } else if (actual != null
                && Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<?> values = (Collection<?>) actual;
            return values.stream().anyMatch(func);
        } else {
            throw new IllegalArgumentException(
                    "You cannot do a subquery against a single element.");
        }
    }

    protected boolean regex(final Object actual, final Object query) {
        Predicate<String> test;

        if (query instanceof String) {
            String queryRegex = (String) query;
            test = Pattern.compile(queryRegex).asPredicate();
        } else {
            return false;
        }

        if (actual.getClass().isArray()) {
            String[] values = (String[]) actual;
            return Arrays.stream(values).anyMatch(test);
        } else if (Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<String> values = (Collection<String>) actual;
            return values.stream().anyMatch(test);
        } else if (actual instanceof String) {
            return test.test((String) actual);
        }

        return false;
    }

    /**
     * @param actual the actual
     * @param query the query
     * @return query equals or not
     */
    protected boolean equality(final Object actual, final Object query) {
        if (actual != null && actual.getClass().isArray()) {
            Object[] values = (Object[]) actual;
            return stream(values).anyMatch(query::equals);
        } else if (actual != null
                && Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<?> values = (Collection<?>) actual;
            return values.stream().anyMatch(query::equals);
        } else {
            return query.equals(actual);
        }
    }

    /**
     * @param actual the actual
     * @param query the query
     * @return query of actual
     */
    protected boolean inequality(final Object actual, final Object query) {
        if (actual != null && actual.getClass().isArray()) {
            Object[] values = (Object[]) actual;
            return stream(values).noneMatch(query::equals);
        } else if (actual != null
                && Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<?> values = (Collection<?>) actual;
            return values.stream().noneMatch(query::equals);
        } else {
            return !query.equals(actual);
        }
    }

    /**
     * @param actual  the actual
     * @param queries the queries
     * @return queries
     */
    protected boolean nin(final Object actual, final Collection<?> queries) {
        if (actual != null && actual.getClass().isArray()) {
            Object[] values = (Object[]) actual;
            return stream(values).noneMatch(queries::contains);
        } else if (actual != null
                && Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<?> values = (Collection<?>) actual;
            return values.stream().noneMatch(queries::contains);
        } else {
            return !queries.contains(actual);
        }
    }

    /**
     * @param actual the actual
     * @param queries the queries
     * @return queries contains actual
     */
    protected boolean in(final Object actual, final Collection<?> queries) {
        if (actual != null && actual.getClass().isArray()) {
            Object[] values = (Object[]) actual;
            return stream(values).anyMatch(queries::contains);
        } else if (actual != null
                && Collection.class.isAssignableFrom(actual.getClass())) {
            Collection<?> values = (Collection<?>) actual;
            return values.stream().anyMatch(queries::contains);
        } else {
            return queries.contains(actual);
        }
    }
    /**
     * @param actual the actual
     * @param query the query
     * @return boolean
     */
    protected boolean greaterThan(final Object actual, final Object query) {
        if (query instanceof Number && actual instanceof Number) {
            return ((Number) actual).doubleValue()
                    > ((Number) query).doubleValue();
        } else if (query instanceof String && actual instanceof String) {
            return ((String) actual).compareTo((String) query) > 0;
        } else {
            throw new UnsupportedOperationException(
                    "Incompatible types provided.");
        }
    }

    /**
     * @param actual the actual
     * @param query the query
     * @return boolean
     */
    protected boolean greaterThanOrEqualTo(final Object actual,
                                           final Object query) {
        if (query instanceof Number && actual instanceof Number) {
            return ((Number) actual).doubleValue()
                    >= ((Number) query).doubleValue();
        } else if (query instanceof String && actual instanceof String) {
            return ((String) actual).compareTo((String) query) >= 0;
        } else {
            throw new UnsupportedOperationException(
                    "Incompatible types provided.");
        }
    }

    /**
     * @param actual the actual
     * @param query the query
     * @return boolean
     */
    protected boolean lessThan(final Object actual, final Object query) {
        if (query instanceof Number && actual instanceof Number) {
            return ((Number) actual).doubleValue()
                    < ((Number) query).doubleValue();
        } else if (query instanceof String && actual instanceof String) {
            return ((String) actual).compareTo((String) query) < 0;
        } else {
            throw new UnsupportedOperationException(
                    "Incompatible types provided.");
        }
    }
    /**
     * @param actual the actual
     * @param query the query
     * @return boolean
     */
    protected boolean lessThanOrEqualTo(final Object actual,
                                        final Object query) {
        if (query instanceof Number && actual instanceof Number) {
            return ((Number) actual).doubleValue()
                    <= ((Number) query).doubleValue();
        } else if (query instanceof String && actual instanceof String) {
            return ((String) actual).compareTo((String) query) <= 0;
        } else {
            throw new UnsupportedOperationException(
                    "Incompatible types provided.");
        }
    }
    /**
     * @param node the node
     * @return node
     */
    private Predicate<T> doesNotExist(final ComparisonNode node) {
        return t -> resolveSingleField(t, node.getField().asKey(), node,
                (one, two) -> Objects.isNull(one));
    }
    /**
     * @param node the node
     * @return node
     */
    private Predicate<T> exists(final ComparisonNode node) {
        return t -> resolveSingleField(t, node.getField().asKey(), node,
                (one, two) -> Objects.nonNull(one));
    }
    /**
     * @param node the node
     * @param func the func
     * @return resolveMultiField
     */
    private Predicate<T> single(final ComparisonNode node,
                                final BiPredicate<Object, Object> func) {
        return t -> resolveSingleField(t, node.getField().asKey(), node, func);
    }

    /**
     * @param node the node
     * @param func the func
     * @return resolveMultiField
     */
    private Predicate<T> multi(final ComparisonNode node,
                               final BiPredicate<Object, Collection<?>> func) {
        return t -> resolveMultiField(t, node.getField().asKey(), node, func);
    }
    /**
     * @param root the root
     * @param field the field
     * @param node the node
     * @param func the func
     * @return func test
     */
    private boolean resolveSingleField(final Object root, final String field,
                                       final ComparisonNode node,
                                       final BiPredicate<Object, Object> func) {
        if (root == null || node.getField() == null) {
            return func.test(null, node.getValues().iterator().next());
        } else {
            String[] splitField = field.split("\\.", 2);
            Object currentField = getFieldValueFromString(root, splitField[0]);
            if (splitField.length == 1) {
                return func.test(currentField,
                        node.getValues().iterator().next());
            } else {
                return recurseSingle(currentField, splitField[1], node, func);
            }
        }
    }
    /**
     * @param root the root
     * @param field the field
     * @param node the node
     * @param func the func
     * @return resolveSingleField
     */
    private boolean recurseSingle(final Object root, final String field,
                                  final ComparisonNode node,
                                  final BiPredicate<Object, Object> func) {

        if (root.getClass().isArray()) {
            return Arrays.stream((Object[]) root)
                    .anyMatch(t -> resolveSingleField(t, field, node, func));
        }

        if (root instanceof Collection) {
            return ((Collection<Object>) root).stream()
                    .anyMatch(t -> resolveSingleField(t, field, node, func));
        }

        return resolveSingleField(root, field, node, func);
    }

    /**
     * @param root the root
     * @param field the field
     * @param node the node
     * @param func the func
     * @return recurseMulti
     */
    private boolean resolveMultiField(final Object root, final String field,
                              final ComparisonNode node,
                              final BiPredicate<Object, Collection<?>> func) {
        if (root == null || node.getField() == null) {
            return func.test(null, node.getValues());
        } else {
            String[] splitField = field.split("\\.", 2);
            Object currentField = getFieldValueFromString(root, splitField[0]);
            if (splitField.length == 1) {
                return func.test(currentField, node.getValues());
            } else {
                return recurseMulti(currentField, splitField[1], node, func);
            }
        }
    }

    /**
     * @param root the root
     * @param field the field
     * @param node the node
     * @param func the func
     * @return resolveMultiField
     */
    private boolean recurseMulti(final Object root, final String field,
                               final ComparisonNode node,
                               final BiPredicate<Object, Collection<?>> func) {

        if (root.getClass().isArray()) {
            return Arrays.stream((Object[]) root)
                    .anyMatch(t -> resolveMultiField(t, field, node, func));
        }

        if (root instanceof Collection) {
            return ((Collection<Object>) root).stream()
                    .anyMatch(t -> resolveMultiField(t, field, node, func));
        }

        return resolveMultiField(root, field, node, func);
    }

    /**
     * @param o the Object
     * @param s the String
     * @return read fields
     */
    private Object getFieldValueFromString(final Object o, final String s) {

        if (o instanceof Map) {
            return getFieldValue((Map<String, Object>) o, s);
        }

        if (o == null) {
            return null;
        }
        try {
            return FieldUtils.readField(o, s, true);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
