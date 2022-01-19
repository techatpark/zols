package org.zols.datastore.elasticsearch.rsql;

import cz.jirutka.rsql.parser.ast.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Visit RSQl to produce Elastic Search Query.
 */
public class ElasticSearchVisitor
        extends NoArgRSQLVisitorAdapter<QueryBuilder> {

    private final ComparisonNodeInterpreter<QueryBuilder>
            comparisonNodeInterpreter;

    public ElasticSearchVisitor(
            final ComparisonNodeInterpreter<QueryBuilder>
                    anComparisonNodeInterpreter) {
        this.comparisonNodeInterpreter = anComparisonNodeInterpreter;
    }

    @Override
    public QueryBuilder visit(final AndNode andNode) {
        return visit((LogicalNode) andNode);
    }

    @Override
    public QueryBuilder visit(final OrNode orNode) {
        return visit((LogicalNode) orNode);
    }

    @Override
    public QueryBuilder visit(final ComparisonNode node) {
        return comparisonNodeInterpreter.interpret(node);
    }

    private QueryBuilder visit(final LogicalNode logicalNode) {
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        for (final Node childNode : logicalNode.getChildren()) {
            final QueryBuilder childNodeQueryBuilder =
                    visitUnknownNode(childNode);

            if (logicalNode.getOperator() == LogicalOperator.AND) {
                boolQueryBuilder.must(childNodeQueryBuilder);
            } else {
                // if LogicalOperator.OR
                boolQueryBuilder.should(childNodeQueryBuilder);
            }
        }

        return boolQueryBuilder;
    }

    private QueryBuilder visitUnknownNode(final Node node) {
        if (node instanceof LogicalNode) {
            return visit((LogicalNode) node);
        } else {
            // if ComparisonNode
            return visit((ComparisonNode) node);
        }
    }

}
