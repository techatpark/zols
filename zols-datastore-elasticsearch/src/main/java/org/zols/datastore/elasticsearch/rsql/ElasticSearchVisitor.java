package org.zols.datastore.elasticsearch.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.NoArgRSQLVisitorAdapter;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Visit RSQl to produce Elastic Search Query.
 */
public class ElasticSearchVisitor
        extends NoArgRSQLVisitorAdapter<QueryBuilder> {
    /**
     * comparisonNodeInterpreter.
     */
    private final ComparisonNodeInterpreter<QueryBuilder>
            comparisonNodeInterpreter;
    /**
     * Instantiates a new ElasticSearchVisitor.
     *
     * @param anComparisonNodeInterpreter  an ComparisonNodeInterpreter
     */
    public ElasticSearchVisitor(
            final ComparisonNodeInterpreter<QueryBuilder>
                    anComparisonNodeInterpreter) {
        this.comparisonNodeInterpreter = anComparisonNodeInterpreter;
    }

    /**
     * visit the Node.
     * @param andNode the and node
     * @return andNode
     */
    @Override
    public QueryBuilder visit(final AndNode andNode) {
        return visit((LogicalNode) andNode);
    }
    /**
     * visit the Node.
     * @param orNode or node
     * @return orNode
     */
    @Override
    public QueryBuilder visit(final OrNode orNode) {
        return visit((LogicalNode) orNode);
    }
    /**
     * visit the Node.
     * @param node the node
     * @return node
     */
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
    /**
     * visit the Node.
     * @param node the node
     * @return Node
     */
    private QueryBuilder visitUnknownNode(final Node node) {
        if (node instanceof LogicalNode) {
            return visit((LogicalNode) node);
        } else {
            // if ComparisonNode
            return visit((ComparisonNode) node);
        }
    }

}
