/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ComparisonOperatorProxy Enum
 * <p>
 * Convert RSQLOperators to an Enumeration type.
 *
 * @author AntonioRabelo
 * @since 2015-02-10
 */
public enum ComparisonOperatorProxy {
    /**
     * Compare with =.
     */
    EQUAL(RSQLOperators.EQUAL),
    /**
     * Compare with !=.
     */
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    /**
     * Compare with >.
     */
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    /**
     * Compare with >=.
     */
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    /**
     * Compare with <.
     */
    LESS_THAN(RSQLOperators.LESS_THAN),
    /**
     * Compare with <=.
     */
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    /**
     * Compare with IN.
     */
    IN(RSQLOperators.IN),
    /**
     * Compare with NOT_IN.
     */
    NOT_IN(RSQLOperators.NOT_IN);

    /**
     * Cache.
     */
    private static final Map<ComparisonOperator, ComparisonOperatorProxy> CACHE
            = Collections.synchronizedMap(
            new HashMap<ComparisonOperator, ComparisonOperatorProxy>());

    static {
        for (ComparisonOperatorProxy proxy : values()) {
            CACHE.put(proxy.getOperator(), proxy);
        }
    }

    /**
     * declares variable operator.
     */
    private final ComparisonOperator operator;

    /**
     * Instantiates a new ComparisonOperatorProxy.
     *
     * @param anOperator an Operator
     */
    ComparisonOperatorProxy(final ComparisonOperator anOperator) {
        this.operator = anOperator;
    }

    /**
     * asEnum.
     * @param operator the operator
     * @return  cache
     */
    public static ComparisonOperatorProxy asEnum(final
                                                 ComparisonOperator operator) {
        return CACHE.get(operator);
    }
    /**
     * gets the operator.
     *
     * @return operator
     */
    public ComparisonOperator getOperator() {
        return this.operator;
    }
}
