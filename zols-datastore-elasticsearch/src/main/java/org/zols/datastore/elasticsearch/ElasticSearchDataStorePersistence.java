/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import cz.jirutka.rsql.parser.ast.Node;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.ElasticsearchStatusException;
import static org.elasticsearch.action.DocWriteResponse.Result.CREATED;
import static org.elasticsearch.action.DocWriteResponse.Result.DELETED;
import static org.elasticsearch.action.DocWriteResponse.Result.UPDATED;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datastore.DataStore;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.Page;
import org.zols.datatore.exception.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import static org.zols.jsonschema.util.JsonSchemaUtil.jsonSchemaForSchema;
import static org.zols.jsonschema.util.JsonUtil.asMap;
import org.zols.rsql.ElasticComparisonNodeInterpreter;

import org.zols.rsql.ElasticSearchVisitor;

/**
 *
 * Elastic Search Implementation of DataStore
 *
 */
public class ElasticSearchDataStorePersistence implements BrowsableDataStorePersistence {

    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchDataStorePersistence.class);

    private final RestHighLevelClient client;

    private final String indexName;

    private DataStore dataStore;

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
                indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
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
    public Map<String, Object> read(JsonSchema jsonSchema, SimpleEntry<String, Object>... idValues) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);

        LOGGER.debug("Getting Data for {} with id {}", typeName, ids);

        GetRequest getRequest = new GetRequest(
                getIndexName(typeName), typeName, getEncodedId(ids));

        GetResponse getResponse;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            return getResponse.getSource();
        } catch (IOException ex) {
            throw new DataStoreException("Unable to get data for " + typeName + "with id " + ids, ex);
        }
    }

    @Override
    public boolean update(JsonSchema jsonSchema,
            Map<String, Object> jsonData, SimpleEntry<String, Object>... idValues) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Updating Data for {} with id {}", typeName, ids);

        if (ids != null) {
            IndexRequest indexRequest = new IndexRequest(getIndexName(typeName), typeName, getEncodedId(ids))
                    .source(jsonData);
            IndexResponse indexResponse;
            try {
                indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
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
            Map<String, Object> validatedData, SimpleEntry<String, Object>... idValues)
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
            SimpleEntry<String, Object>... idValues) throws DataStoreException {
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
    public boolean delete(JsonSchema jsonSchema, Node queryNode) throws DataStoreException {

        String typeName = getTypeName(jsonSchema);
        String indexName2 = getIndexName(typeName);
        QueryBuilder builder = getQueryBuilder(jsonSchema, queryNode);
        if (builder != null) {

            try {
                // Patch for Solinving delete by query conflicts
                client.getLowLevelClient().performRequest(new Request("POST", "/" + indexName2 + "/_refresh"));
                HttpEntity entity = new NStringEntity("{" + "\"query\":" + builder.toString() + "}", ContentType.APPLICATION_JSON);
                Request req = new Request("POST", "/" + indexName2 + "/" + typeName + "/_delete_by_query?scroll_size=1");
                req.setEntity(entity);
                client.getLowLevelClient()
                        .performRequest(req);
                client.getLowLevelClient().performRequest(new Request("POST", "/" + indexName2 + "/_refresh"));
                refreshIndex(indexName2);
                return true;
            } catch (IOException ex) {
                throw new DataStoreException("Unable to delete " + indexName2, ex);
            }
        }

        return false;
    }

    @Override
    public List<Map<String, Object>> list(JsonSchema jsonSchema, Node queryNode) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);

        LOGGER.debug("Listing Data for {} ", typeName);
        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

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
    public Page<Map<String, Object>> list(JsonSchema jsonSchema, Node queryNode, Integer pageNumber, Integer pageSize) throws DataStoreException {
        String typeName = getTypeName(jsonSchema);

        LOGGER.debug("Listing Data for {} page {} size {}", typeName, pageNumber, pageSize);
        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
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

    private QueryBuilder getQueryBuilder(JsonSchema jsonSchema, Node queryNode) throws DataStoreException {

        BoolQueryBuilder bool_builder = QueryBuilders.boolQuery();
        if (queryNode != null) {
            bool_builder.must(queryNode.accept(new ElasticSearchVisitor(new ElasticComparisonNodeInterpreter())));
        }
        if (jsonSchemaForSchema() != jsonSchema) {

            List<String> implementations = dataStore.getImplementationsOf(jsonSchema);
            if (implementations == null || implementations.isEmpty()) {
                bool_builder.must(termQuery("$type", jsonSchema.getId()));

            } else {
                implementations.add(jsonSchema.getId());
                bool_builder.must(termsQuery("$type", implementations));
            }
            /*
            if (queryNode == null) {
                condition = new MapQuery().string("$type").in(implementations);
            } else {
                condition = condition.and().string("$type").in(implementations);
            }*/
        }

        //QueryBuilder builder = condition.query(new ElasticsearchVisitor(), new ElasticsearchVisitor.Context());
        return bool_builder;
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
    public AggregatedResults browse(JsonSchema schema, String keyword, Node queryNode, Integer pageNumber, Integer pageSize) throws DataStoreException {
        AggregatedResults aggregatedResults = null;
        if (schema != null) {
//            if (keyword != null) {
//                query.addFilter(new Filter(Filter.Operator.FULL_TEXT_SEARCH, keyword + "*"));
//            }
            Map<String, Object> searchResponse = searchResponse(schema, queryNode, pageNumber, pageSize);
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
                        Object title = ((Map<String, Object>) ((Map<String, Object>) aggregations.get(entrySet.getKey())).get("meta")).get("title");
                        if (aggregationName.startsWith("min_")) {
                            bucket.put("name", aggregationName.replaceAll("min_", ""));
                            bucket.put("type", "minmax");
                            bucketItem = new HashMap<>();
                            bucketItem.put("min", ((Map<String, Object>) aggregations.get(entrySet.getKey())).get("value"));
                            bucketItem.put("max", ((Map<String, Object>) aggregations.get(entrySet.getKey().replaceAll("min_", "max_").replaceAll("min#", "max#"))).get("value"));
                            bucket.put("title", title == null ? bucket.get("name") : title);
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
                            bucket.put("title", title == null ? bucket.get("name") : title);
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
        searchRequestBuilder.aggregation(AggregationBuilders.terms("types").setMetaData(map).field("$type.keyword"));
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
                                .aggregation(AggregationBuilders.terms(entry.getKey()).setMetaData(entry.getValue()).field(entry.getKey() + ".keyword"));

                        break;
                }
            }

        });

    }

    public Map<String, Object> searchResponse(JsonSchema jsonSchema,
            Node queryNode, Integer pageNumber, Integer pageSize) throws DataStoreException {

        String typeName = getTypeName(jsonSchema);

        SearchRequest searchRequest = new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
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

    @Override
    public void onNewSchema(JsonSchema jsonSchema) throws DataStoreException {
        if (jsonSchema.isRoot()) {
            String typeName = jsonSchema.getId();
            String typeIndexName = getIndexName(typeName);
            String originalTypeIndexName = getIndexName(typeName) + "_base";

            try {

                GetIndexRequest request = new GetIndexRequest();
                request.indices(originalTypeIndexName);

                if (!client.indices().exists(request, RequestOptions.DEFAULT)) {
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(originalTypeIndexName);
                    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

                    if (createIndexResponse.isAcknowledged()) {
                        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
                        AliasActions aliasAction
                                = new AliasActions(AliasActions.Type.ADD)
                                        .index(originalTypeIndexName)
                                        .alias(typeIndexName);
                        aliasesRequest.addAliasAction(aliasAction);
                        AcknowledgedResponse indicesAliasesResponse
                                = client.indices().updateAliases(aliasesRequest, RequestOptions.DEFAULT);
                    }
                }

            } catch (IOException ex) {
                throw new DataStoreException("Unable to create index for " + typeName, ex);
            }

        }
    }

    @Override
    public void onUpdateSchema(JsonSchema oldSchema, JsonSchema newSchema) throws DataStoreException {
        Map<String, Map<String, Object>> oldPropertiesMap = oldSchema.getProperties();
        Map<String, Map<String, Object>> newPropertiesMap = newSchema.getProperties();
        if (oldPropertiesMap != null) {
            List<String> removedFields = oldPropertiesMap.keySet().stream()
                    .filter(key -> {
                        return !newPropertiesMap.containsKey(key);
                    }).map(key -> "\"" + key + "\"").collect(toList());
            if (!removedFields.isEmpty()) {
                String typeName = getTypeName(oldSchema);
                try {

                    String typeIndexAliasName = getIndexName(typeName);
                    String typeIndexName = findIndexName(typeIndexAliasName);
                    String newTypeIndexName = typeIndexAliasName + System.currentTimeMillis();

                    Map<String, String> params = Collections.singletonMap("pretty", "true");
                    String reindexRequest = "{\n"
                            + "  \"source\": {\n"
                            + "    \"index\": "
                            + "\"" + typeIndexAliasName + "\""
                            + ",\n"
                            + "    \"_source\": {\n"
                            + "        \"excludes\": "
                            + removedFields.toString()
                            + "    }\n"
                            + "  },\n"
                            + "  \"dest\": {\n"
                            + "    \"index\": \""
                            + newTypeIndexName
                            + "\"\n"
                            + "  }\n"
                            + "}";
                    HttpEntity entity = new NStringEntity(reindexRequest, ContentType.APPLICATION_JSON);

                    Request req = new Request("POST", "/_reindex");

                    req.setEntity(entity);

                    client.getLowLevelClient()
                            .performRequest(req);

                    // Switch
                    String switchReq = "{\n"
                            + "    \"actions\" : [\n"
                            + "        { \"remove\" : { \"indices\" : \""
                            + typeIndexName
                            + "\", \"alias\" : \""
                            + typeIndexAliasName
                            + "\" } },\n"
                            + "        { \"add\" : { \"indices\" : \""
                            + newTypeIndexName
                            + "\", \"alias\" : \""
                            + typeIndexAliasName
                            + "\" } }\n"
                            + "    ]\n"
                            + "}";
                    entity = new NStringEntity(switchReq, ContentType.APPLICATION_JSON);

                    req = new Request("POST", "/_aliases");

                    req.setEntity(entity);

                    client.getLowLevelClient()
                            .performRequest(req);

                    client.getLowLevelClient().performRequest(new Request("DELETE", "/" + typeIndexName));

                } catch (IOException ex) {
                    throw new DataStoreException("Unable to switch alias index for " + typeName, ex);
                }
            }
        }
    }

    private String findIndexName(String aliasName) throws IOException {
        final StringBuilder index = new StringBuilder();
        GetAliasesRequest requestWithAlias = new GetAliasesRequest(aliasName);
        GetAliasesResponse response = client.indices().getAlias(requestWithAlias, RequestOptions.DEFAULT);
        Map<String, Set<AliasMetaData>> aliasMap = response.getAliases();

        aliasMap.forEach((indexName2, md) -> {
            if (indexName2.startsWith(aliasName)) {
                index.append(indexName2);
            }
        });

        return index.toString().trim().length() == 0 ? null : index.toString();
    }

    @Override
    public void onDeleteSchema(JsonSchema jsonSchema) throws DataStoreException {
        // If root delete the index
        if (jsonSchema.isRoot()) {
            String typeName = getTypeName(jsonSchema);
            String indexAliasName = getIndexName(typeName);

            try {
                String typeIndexName = findIndexName(indexAliasName);
                DeleteIndexRequest request = new DeleteIndexRequest(typeIndexName);
                AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            } catch (IOException ex) {
                throw new DataStoreException("Unable to delete index for " + typeName, ex);
            }
        }
    }

    @Override
    public void onIntialize(DataStore dataStore) {
        this.dataStore = dataStore;
    }

}
