/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.ElasticsearchStatusException;
import static org.elasticsearch.action.DocWriteResponse.Result.CREATED;
import static org.elasticsearch.action.DocWriteResponse.Result.DELETED;
import static org.elasticsearch.action.DocWriteResponse.Result.UPDATED;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import static org.zols.datastore.query.Filter.Operator.FULL_TEXT_SEARCH;
import static org.zols.datastore.query.Filter.Operator.IN_BETWEEN;
import static org.zols.datastore.query.Filter.Operator.IS_NOTNULL;
import static org.zols.datastore.query.Filter.Operator.NOT_EQUALS;
import static org.zols.datastore.query.Filter.Operator.NOT_EXISTS_IN;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import static org.zols.jsonschema.util.JsonUtil.asMap;

/**
 *
 * Elastic Search Implementation of DataStore
 *
 */
public class ElasticSearchDataStorePersistence implements BrowsableDataStorePersistence {
    
    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchDataStorePersistence.class);
    
    private final RestHighLevelClient client;
    
    private final String indexName;
    
    public ElasticSearchDataStorePersistence() {
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        this.indexName = "zols";
    }
    
    public ElasticSearchDataStorePersistence(String indexName, RestHighLevelClient client) {
        this.indexName = indexName;
        
        this.client = client;
        
    }
    
    @Override
    public Map<String, Object> create(JsonSchema jsonSchema, Map<String, Object> jsonData) throws DataStoreException {
        
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(jsonData);
        LOGGER.debug("Creating Data for {} with id {}", typeName, ids);
        
        if (ids != null) {
            IndexRequest indexRequest = new IndexRequest(getIndexName(typeName), typeName, getEncodedId(ids))
                    .source(jsonData);
            
            IndexResponse indexResponse;
            try {
                indexResponse = client.index(indexRequest);
                if (indexResponse.getResult() == CREATED) {
                    LOGGER.debug("Created Data for {} with id {}", typeName, ids);
                    refreshIndex(getIndexName(typeName));
                    return read(jsonSchema, jsonSchema.getIdKeys(jsonData));
                }
            } catch (IOException ex) {
                throw new DataStoreException("Unable to create data for " + typeName, ex);
            }
            
        }
        
        return null;
    }
    
    @Override
    public Map<String, Object> read(JsonSchema jsonSchema, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        
        LOGGER.debug("Getting Data for {} with id {}", typeName, ids);
        
        GetRequest getRequest = new GetRequest(
                getIndexName(typeName), typeName, getEncodedId(ids));
        
        GetResponse getResponse;
        try {
            getResponse = client.get(getRequest);
            return getResponse.getSource();
        } catch (IOException ex) {
            throw new DataStoreException("Unable to get data for " + typeName + "with id " + ids, ex);
        }
    }
    
    @Override
    public boolean update(JsonSchema jsonSchema,
            Map<String, Object> jsonData, AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Updating Data for {} with id {}", typeName, ids);
        
        if (ids != null) {
            IndexRequest indexRequest = new IndexRequest(getIndexName(typeName), typeName, getEncodedId(ids))
                    .source(jsonData);
            IndexResponse indexResponse;
            try {
                indexResponse = client.index(indexRequest);
                if (indexResponse.getResult() == UPDATED) {
                    refreshIndex(getIndexName(typeName));
                    return true;
                }
            } catch (IOException ex) {
                throw new DataStoreException("Unable to create data for " + typeName, ex);
            }
            
        }
        return false;
    }
    
    @Override
    public boolean updatePartially(JsonSchema jsonSchema,
            Map<String, Object> validatedData, AbstractMap.SimpleEntry<String, Object>... idValues)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Updating Data for {} with id {}", typeName, ids);
        
        if (ids != null) {
            UpdateRequest updateRequest = new UpdateRequest(getIndexName(typeName), typeName, getEncodedId(ids));
            updateRequest.doc(validatedData);
            UpdateResponse updateResponse;
            try {
                updateResponse = client.update(updateRequest);
                if (updateResponse.getResult() == UPDATED) {
                    refreshIndex(getIndexName(typeName));
                    return true;
                }
            } catch (IOException ex) {
                throw new DataStoreException("Unable to create data for " + typeName, ex);
            }
            
        }
        return false;
    }
    
    @Override
    public boolean delete(JsonSchema jsonSchema,
            AbstractMap.SimpleEntry<String, Object>... idValues) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Deleting Data for {} with id {}", typeName, ids);
        
        DeleteRequest deleteRequest = new DeleteRequest(
                getIndexName(typeName), typeName, getEncodedId(ids));
        
        DeleteResponse deleteResponse;
        try {
            deleteResponse = client.delete(deleteRequest);
            return (deleteResponse.getResult() == DELETED);
        } catch (IOException ex) {
            throw new DataStoreException("Unable to delete data for " + typeName + "with id " + ids, ex);
        }
        
    }
    
    private String getTypeName(JsonSchema jsonSchema) {
        return jsonSchema.getJSONPropertyName(jsonSchema.getRoot().getId());
    }
    
    private Object getIdValue(JsonSchema jsonSchema, Map<String, Object> jsonData) {
        return jsonSchema == null ? null : jsonSchema.getIdValues(jsonData)[0];
    }
    
    @Override
    public boolean delete(JsonSchema jsonSchema, Query query) throws DataStoreException {
        
        String typeName = getTypeName(jsonSchema);
        QueryBuilder builder = getQueryBuilder(query);
        if (builder != null) {
            
            try {
                // Patch for Solinving delete by query conflicts
                client.getLowLevelClient().performRequest("POST", "/" + getIndexName(typeName) + "/_refresh");
                Map<String, String> params = Collections.singletonMap("pretty", "true");
                HttpEntity entity = new NStringEntity("{" + "\"query\":" + builder.toString() + "}", ContentType.APPLICATION_JSON);
                client.getLowLevelClient()
                        .performRequest("POST", "/" + getIndexName(typeName) + "/" + typeName + "/_delete_by_query?scroll_size=1", params, entity);
                client.getLowLevelClient().performRequest("POST", "/" + getIndexName(typeName) + "/_refresh");
                return true;
            } catch (IOException ex) {
                throw new DataStoreException("Unable to delete " + getIndexName(typeName), ex);
            }
        }
        
        return false;
    }
    
    @Override
    public List<Map<String, Object>> list(JsonSchema jsonSchema, Query query) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        
        LOGGER.debug("Listing Data for {} ", typeName);
        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(getQueryBuilder(query));
        searchRequest.source(sourceBuilder);
        
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            if (totalHits != 0) {
                List<Map<String, Object>> list = new ArrayList<>((int) totalHits);
                for (SearchHit hit : hits.getHits()) {
                    list.add(hit.getSourceAsMap());
                }
                return list;
            }
            
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() == RestStatus.NOT_FOUND) {
                return null;
            } else {
                throw new DataStoreException("Unable to list data for " + typeName, ex);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Unable to list data for " + typeName, ex);
        }
        return null;
    }
    
    @Override
    public Page<Map<String, Object>> list(JsonSchema jsonSchema, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        
        LOGGER.debug("Listing Data for {} page {} size {}", typeName, pageNumber, pageSize);
        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(query));
        searchRequest.source(sourceBuilder);
        
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            if (totalHits != 0) {
                List<Map<String, Object>> list = new ArrayList<>(pageSize);
                for (SearchHit hit : hits.getHits()) {
                    list.add(hit.getSourceAsMap());
                }
                return new Page(pageNumber, pageSize, totalHits, list);
            }
            
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() == RestStatus.NOT_FOUND) {
                return null;
            } else {
                throw new DataStoreException("Unable to list data for " + typeName, ex);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Unable to list data for " + typeName, ex);
        }
        return null;
    }
    
    public static QueryBuilder getQueryBuilder(Query query) {
        BoolQueryBuilder queryBuilder = null;
        if (query != null) {
            queryBuilder = QueryBuilders.boolQuery();
            List<Filter> queries = query.getFilters();
            if (queries != null) {
                int size = queries.size();
                Filter filter;
                Collection collection;
                BoolQueryBuilder boolQuery;
                for (int index = 0; index < size; index++) {
                    filter = queries.get(index);
                    switch (filter.getOperator()) {
                        case FULL_TEXT_SEARCH:
                            queryBuilder.must(QueryBuilders.queryStringQuery(filter.getValue().toString()));
                            break;
                        case EQUALS:
                            queryBuilder.must(QueryBuilders.matchQuery(filter.getName(), filter.getValue()));
                            break;
                        case NOT_EQUALS:
                            queryBuilder.mustNot(QueryBuilders.matchQuery(filter.getName(), filter.getValue()));
                            break;
                        case IS_NULL:
                            queryBuilder.mustNot(QueryBuilders.existsQuery(filter.getName()));
                            break;
                        case IS_NOTNULL:
                            queryBuilder.must(QueryBuilders.existsQuery(filter.getName()));
                            break;
                        case EXISTS_IN:
                            boolQuery = QueryBuilders.boolQuery();
                            collection = (Collection) filter.getValue();
                            for (Object object : collection) {
                                boolQuery.should(QueryBuilders.matchQuery(filter.getName(), object));
                            }
                            queryBuilder.must(boolQuery);
                            break;
                        case NOT_EXISTS_IN:
                            boolQuery = QueryBuilders.boolQuery();
                            collection = (Collection) filter.getValue();
                            for (Object object : collection) {
                                boolQuery.should(QueryBuilders.matchQuery(filter.getName(), object));
                            }
                            queryBuilder.mustNot(boolQuery);
                            break;
                        case IN_BETWEEN:
                            Object[] rangeValues = (Object[]) filter.getValue();
                            queryBuilder.must(QueryBuilders.rangeQuery(filter.getName())
                                    .from(rangeValues[0])
                                    .to(rangeValues[1]));
                            break;
                    }
                }
            }
            
        }
        
        return queryBuilder;
    }
    
    private String getEncodedId(Object ids) {
        try {
            return ids == null ? null : URLEncoder.encode(ids.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ElasticSearchDataStorePersistence.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private String getIndexName(String typeName) {
        return indexName + "_" + typeName;
    }
    
    @Override
    public AggregatedResults browse(JsonSchema schema, String keyword, Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        AggregatedResults aggregatedResults = null;
        if (schema != null) {
            if (keyword != null) {
                query.addFilter(new Filter(Filter.Operator.FULL_TEXT_SEARCH, keyword + "*"));
            }
            Map<String, Object> searchResponse = searchResponse(schema, query, pageNumber, pageSize);
            Page<Map<String, Object>> resultsOf = pageOf(searchResponse, pageNumber, pageSize);
            if (resultsOf != null) {
                aggregatedResults = new AggregatedResults();
                aggregatedResults.setPage(resultsOf);
                aggregatedResults.setBuckets(bucketsOf(searchResponse));
            }
        }
        return aggregatedResults;
    }
    
    private List<Map<String, Object>> bucketsOf(Map<String, Object> searchResponse) {
        List<Map<String, Object>> buckets = null;
        if (searchResponse != null) {
            Map<String, Object> aggregations = (Map<String, Object>) searchResponse.get("aggregations");
            if (aggregations != null) {
                Map<String, Object> bucket;
                Map<String, Object> bucketItem;
                List<Map<String, Object>> bucketItems;
                List<Map<String, Object>> bucketsMaps;
                
                buckets = new ArrayList<>();
                
                String aggregationName;
                for (Map.Entry<String, Object> entrySet : aggregations.entrySet()) {
                    
                    aggregationName = entrySet.getKey().substring(entrySet.getKey().indexOf("#") + 1);
                    if (!aggregationName.startsWith("max_")) {
                        bucket = new HashMap<>();
                        if (aggregationName.startsWith("min_")) {
                            bucket.put("name", aggregationName.replaceAll("min_", ""));
                            bucket.put("type", "minmax");
                            bucketItem = new HashMap<>();
                            bucketItem.put("min", ((Map<String, Object>) aggregations.get(entrySet.getKey())).get("value"));
                            bucketItem.put("max", ((Map<String, Object>) aggregations.get(entrySet.getKey().replaceAll("min_", "max_").replaceAll("min#", "max#"))).get("value"));
                            bucket.put("title", ((Map<String, Object>) ((Map<String, Object>) aggregations.get(entrySet.getKey())).get("meta")).get("title"));
                            bucket.put("item", bucketItem);
                        } else if (!aggregationName.startsWith("max_")) {
                            bucket.put("name", aggregationName);
                            bucket.put("type", "term");
                            bucketsMaps = (List<Map<String, Object>>) ((Map<String, Object>) entrySet.getValue()).get("buckets");
                            bucketItems = new ArrayList<>();
                            for (Map<String, Object> bucketsMap : bucketsMaps) {
                                bucketItem = new HashMap<>();
                                bucketItem.put("name", bucketsMap.get("key").toString());
                                bucketItem.put("label", bucketsMap.get("key").toString());
                                bucketItem.put("count", (Integer) bucketsMap.get("doc_count"));
                                bucketItems.add(bucketItem);
                            }
                            bucket.put("title", ((Map<String, Object>) ((Map<String, Object>) aggregations.get(entrySet.getKey())).get("meta")).get("title"));
                            
                            bucket.put("items", bucketItems);
                        }
                        buckets.add(bucket);
                    }
                    
                }
                
            }
        }
        return buckets;
    }
    
    private Page<Map<String, Object>> pageOf(Map<String, Object> searchResponse, Integer pageNumber,
            Integer pageSize) {
        Page<Map<String, Object>> page = null;
        List<Map<String, Object>> list = resultsOf(searchResponse);
        if (list != null) {
            Long noOfRecords = new Long(((Map<String, Object>) searchResponse.get("hits")).get("total").toString());
            page = new Page(pageNumber, pageSize, noOfRecords, list);
        }
        return page;
    }
    
    private List<Map<String, Object>> resultsOf(Map<String, Object> searchResponse) {
        List<Map<String, Object>> list = null;
        if (searchResponse != null) {
            Integer noOfRecords = (Integer) ((Map<String, Object>) searchResponse.get("hits")).get("total");
            if (0 != noOfRecords) {
                List<Map<String, Object>> recordsMapList = (List<Map<String, Object>>) ((Map<String, Object>) searchResponse.get("hits")).get("hits");
                list = new ArrayList<>(recordsMapList.size());
                for (Map<String, Object> recordsMapList1 : recordsMapList) {
                    list.add((Map<String, Object>) recordsMapList1.get("_source"));
                }
            }
        }
        return list;
    }
    
    private void addAggregations(JsonSchema jsonSchema, SearchSourceBuilder searchRequestBuilder) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "Types");
        searchRequestBuilder.aggregation(AggregationBuilders.terms("types").setMetaData(map).field("$type"));
        jsonSchema.getProperties().entrySet().parallelStream().forEach(entry -> {
            String filter = (String) entry.getValue().get("filter");
            if (filter != null) {
                String title = entry.getKey();
                if (entry.getValue().get("title") != null) {
                    title = entry.getValue().get("title").toString();
                }
                switch (filter) {
                    case "minmax":
                        searchRequestBuilder
                                .aggregation(AggregationBuilders.min("min_" + entry.getKey()).setMetaData(entry.getValue()).field(entry.getKey()))
                                .aggregation(AggregationBuilders.max("max_" + entry.getKey()).setMetaData(entry.getValue()).field(entry.getKey()));
                        break;
                    case "term":
                        searchRequestBuilder
                                .aggregation(AggregationBuilders.terms(entry.getKey()).setMetaData(entry.getValue()).field(entry.getKey()));
                        
                        break;
                }
            }
            
        });
        
    }
    
    public Map<String, Object> searchResponse(JsonSchema jsonSchema,
            Query query, Integer pageNumber, Integer pageSize) throws DataStoreException {
        
        String typeName = getTypeName(jsonSchema);
        
        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(query));
        addAggregations(jsonSchema, sourceBuilder);
        
        searchRequest.source(sourceBuilder);
        
        LOGGER.debug("Executing elastic search query {}", searchRequest.toString());
        
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            return asMap(searchResponse.toString());
        } catch (IOException ex) {
            throw new DataStoreException("Unable to browse data for " + typeName, ex);
        }
        
    }
    
    private void refreshIndex(String indexName) throws IOException {
        RefreshRequest request = new RefreshRequest(indexName);
        client.indices().refresh(request, RequestOptions.DEFAULT);
    }
}
