/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

@Service
public class ElasticSearchDataStore extends DataStore {

    private final Node node;
    private final String indexName = "zols";

    public ElasticSearchDataStore() {
        node = nodeBuilder().local(true).node();
        createIndexIfNotExists();
    }

    private void createIndexIfNotExists() {
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
        if (!node.client().admin().indices().exists(indicesExistsRequest).actionGet().isExists()) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            node.client().admin().indices().create(createIndexRequest).actionGet();
        }
    }

    @Override
    protected Map<String, Object> create(JsonSchema jsonSchema, Map<String, Object> validatedDataObject) {
        String idValue = validatedDataObject.get(getIdField(jsonSchema)).toString();
        IndexResponse response = node.client().prepareIndex(indexName, jsonSchema.getId(), idValue)
                
                .setSource(validatedDataObject)
                .execute()
                .actionGet();
        return ( response.isCreated() ? read(jsonSchema, idValue) : null );
    }

    @Override
    protected Map<String, Object> read(JsonSchema jsonSchema, String idValue) {
        GetResponse getResponse = node.client()
                .prepareGet(indexName, jsonSchema.getId(), idValue)
                
                .execute()
                .actionGet();
        return getResponse.getSource();
    }

    @Override
    protected boolean delete(JsonSchema jsonSchema, String idValue) {
        DeleteResponse response = node.client()
                .prepareDelete(indexName, jsonSchema.getId(), idValue)
                .execute()
                .actionGet();
        return response.isFound();
    }

    @Override
    protected boolean delete(JsonSchema jsonSchema, Query query) {
		DeleteByQueryResponse actionGet = node.client().prepareDeleteByQuery(indexName)
    			.setQuery(termQuery("_type", jsonSchema.getId()))
                .execute().actionGet();
    	return true;
    }

    @Override
    protected boolean update(JsonSchema jsonSchema, Map<String, Object> validatedDataObject) {
        String idValue = validatedDataObject.get(getIdField(jsonSchema)).toString();
        IndexResponse response = node.client().prepareIndex(indexName, jsonSchema.getId(), idValue)
                
                .setSource(validatedDataObject)
                .execute()
                .actionGet();
        return response.isCreated();
    }

    @Override
    protected List<Map<String, Object>> list(JsonSchema jsonSchema) {
        return list(jsonSchema, null);
    }

    @Override
    protected List<Map<String, Object>> list(JsonSchema jsonSchema, Query query) {
        List<Map<String, Object>> list = null;
        SearchResponse response = node.client()
                .prepareSearch()
                .setIndices(indexName)
                .setTypes(jsonSchema.getId())
                .setPostFilter(getFilterBuilder(query))
                .execute().actionGet();
        SearchHits hits = response.getHits();
        if (hits != null) {
            SearchHit[] searchHits = hits.getHits();
            if (searchHits != null) {
                list = new ArrayList<>(searchHits.length);
                for (SearchHit searchHit : searchHits) {
                    list.add(searchHit.getSource());
                }
            }
        }
        return list;
    }
    
    

    private FilterBuilder getFilterBuilder(Query query) {
        FilterBuilder filterBuilder = null;
        if (query != null) {
            List<Filter> filters = query.getFilters();
            if (filters != null) {
                int size = filters.size();
                FilterBuilder[] filterBuilders = new FilterBuilder[size];
                Filter filter;
                for (int index = 0; index < size; index++) {
                    filter = filters.get(index);
                    switch (filter.getOperator()) {
                        case EQUALS:
                            filterBuilders[index] = FilterBuilders.termFilter(filter.getName(), filter.getValue());
                            break;
                        case IS_NULL:
                            filterBuilders[index] = FilterBuilders.notFilter(FilterBuilders.existsFilter(filter.getName()));
                            break;
                    }
                }
                filterBuilder = FilterBuilders.andFilter(filterBuilders);
            }
        }
        return filterBuilder;
    }

}
