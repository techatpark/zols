/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

@FunctionalInterface
public interface ComparisonNodeInterpreter<T> {

    /**
     * intercept Node.
     * @param comparisonNode
     * @return
     */
    T interpret(ComparisonNode comparisonNode);
}
