/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.Query;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.slf4j.LoggerFactory.getLogger;

import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.IS_NULL;

@Service
public class ElasticSearchDataStore extends DataStore {

    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchDataStore.class);

    private final Node node;
    private final String indexName = "zols";

    public ElasticSearchDataStore() {
        node = nodeBuilder().local(true).node();
        createIndexIfNotExists();
    }

    private void patchDelayInRefresh() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ElasticSearchDataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createIndexIfNotExists() {
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
        if (!node.client().admin().indices().exists(indicesExistsRequest).actionGet().isExists()) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            node.client().admin().indices().create(createIndexRequest).actionGet();
        }
        // node.client().admin().indices().refresh(null);
    }

    @Override
    protected Map<String, Object> createData(String jsonSchema, Map<String, Object> validatedDataObject) {
        String idValue = validatedDataObject.get(getIdField(jsonSchema)).toString();
        IndexResponse response = node.client().prepareIndex(indexName, getId(jsonSchema), idValue)
                .setSource(validatedDataObject)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        node.client().admin().indices().refresh(new RefreshRequest(indexName));
        patchDelayInRefresh();
        return (response.isCreated() ? readData(jsonSchema, idValue) : null);
    }

    @Override
    protected Map<String, Object> readData(String jsonSchema, String idValue) {
        GetResponse getResponse = node.client()
                .prepareGet(indexName, getId(jsonSchema), idValue)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        patchDelayInRefresh();
        return getResponse.getSource();
    }

    @Override
    protected boolean deleteData(String jsonSchema, String idValue) {
        DeleteResponse response = node.client()
                .prepareDelete(indexName, getId(jsonSchema), idValue)
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        node.client().admin().indices().refresh(new RefreshRequest(indexName));
        patchDelayInRefresh();
        return response.isFound();
    }

    @Override
    protected boolean deleteData(String jsonSchema, Query query) {
        DeleteByQueryResponse actionGet = node.client().prepareDeleteByQuery(indexName)
                .setListenerThreaded(false)
                .setTypes(getId(jsonSchema))
                .setQuery(getQueryBuilder(query))
                .execute().actionGet();
        node.client().admin().indices().refresh(new RefreshRequest(indexName));
        patchDelayInRefresh();
        return true;
    }

    @Override
    protected boolean updateData(String jsonSchema, Map<String, Object> validatedDataObject) {
        String idValue = validatedDataObject.get(getIdField(jsonSchema)).toString();
        IndexResponse response = node.client().prepareIndex(indexName, getId(jsonSchema), idValue)
                .setOperationThreaded(false)
                .setSource(validatedDataObject)
                .execute()
                .actionGet();
        node.client().admin().indices().refresh(new RefreshRequest(indexName));
        patchDelayInRefresh();
        return response.isCreated();
    }

    @Override
    protected List<Map<String, Object>> listData(String jsonSchema) {
        return listData(jsonSchema, null);
    }

    @Override
    protected List<Map<String, Object>> listData(String jsonSchema, Query query) {
        List<Map<String, Object>> list = null;
        SearchResponse response = node.client()
                .prepareSearch()
                .setIndices(indexName)
                .setTypes(getId(jsonSchema))
                .setQuery(getQueryBuilder(query))
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

    private QueryBuilder getQueryBuilder(Query query) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (query != null) {
            List<Filter> queries = query.getFilters();
            if (queries != null) {
                int size = queries.size();
                QueryBuilder[] queryBuilders = new QueryBuilder[size];
                Filter filter;
                for (int index = 0; index < size; index++) {
                    filter = queries.get(index);
                    switch (filter.getOperator()) {
                        case EQUALS:
                            queryBuilder.must(QueryBuilders.matchQuery(filter.getName(), filter.getValue()));
                            break;
                        case IS_NULL:
                            queryBuilder.mustNot(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.existsFilter(filter.getName())));
                            break;
                    }
                }
            }
        }
        return queryBuilder;
    }

}
