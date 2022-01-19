/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import cz.jirutka.rsql.parser.ast.Node;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.ElasticsearchStatusException;
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
import org.elasticsearch.client.*;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.zols.datastore.DataStore;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.elasticsearch.rsql.ElasticComparisonNodeInterpreter;
import org.zols.datastore.elasticsearch.rsql.ElasticSearchVisitor;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.Page;
import org.zols.jsonschema.JsonSchema;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.action.DocWriteResponse.Result.*;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.slf4j.LoggerFactory.getLogger;
import static org.zols.jsonschema.util.JsonSchemaUtil.jsonSchemaForSchema;
import static org.zols.jsonschema.util.JsonUtil.asMap;

/**
 * Elastic Search Implementation of DataStore.
 */
public class ElasticSearchDataStorePersistence
        implements BrowsableDataStorePersistence {

    private static final org.slf4j.Logger LOGGER =
            getLogger(ElasticSearchDataStorePersistence.class);
    /**
     * tells the client.
     */
    private final RestHighLevelClient client;
    /**
     * tells the indexName.
     */
    private final String indexName;
    /**
     * tells the dataStore.
     */
    private DataStore dataStore;

    public ElasticSearchDataStorePersistence() {
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        this.indexName = "zols";
    }

    /**
     * this is the constructor.
     *
     * @param anIndexName indexName
     * @param anClient    an client
     */
    public ElasticSearchDataStorePersistence(final String anIndexName,
                                  final RestHighLevelClient anClient) {
        this.indexName = anIndexName;
        this.client = anClient;
    }

    /**
     * creates the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param jsonData   the jsonData
     * @return data
     */
    @Override
    public Map<String, Object> create(final JsonSchema jsonSchema,
                                      final Map<String, Object> jsonData)
            throws DataStoreException {

        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(jsonData);
        LOGGER.debug("Creating Data for {} with id {}", typeName, ids);

        if (ids != null) {
            IndexRequest indexRequest =
                    new IndexRequest(getIndexName(typeName), typeName,
                            getEncodedId(ids))
                            .source(jsonData);

            IndexResponse indexResponse;
            try {
                indexResponse =
                        client.index(indexRequest, RequestOptions.DEFAULT);
                if (indexResponse.getResult() == CREATED) {
                    LOGGER.debug("Created Data for {} with id {}", typeName,
                            ids);
                    refreshIndex(getIndexName(typeName));
                    return read(jsonSchema, jsonSchema.getIdKeys(jsonData));
                }
            } catch (IOException ex) {
                throw new DataStoreException(
                        "Unable to create data for " + typeName, ex);
            }

        }

        return null;
    }

    /**
     * reads the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param idValues   the idValues
     * @return schema
     */
    @Override
    public Map<String, Object> read(final JsonSchema jsonSchema,
                        final SimpleEntry<String, Object>... idValues)
            throws DataStoreException {
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
            throw new DataStoreException(
                    "Unable to get data for " + typeName + "with id " + ids,
                    ex);
        }
    }

    /**
     * updates the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param jsonData   the jsonData
     * @param idValues   the idValues
     * @return data
     */
    @Override
    public boolean update(final JsonSchema jsonSchema,
                          final Map<String, Object> jsonData,
                          final SimpleEntry<String, Object>... idValues)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Updating Data for {} with id {}", typeName, ids);

        if (ids != null) {
            IndexRequest indexRequest =
                    new IndexRequest(getIndexName(typeName), typeName,
                            getEncodedId(ids))
                            .source(jsonData);
            IndexResponse indexResponse;
            try {
                indexResponse =
                        client.index(indexRequest, RequestOptions.DEFAULT);
                if (indexResponse.getResult() == UPDATED) {
                    refreshIndex(getIndexName(typeName));
                    return true;
                }
            } catch (IOException ex) {
                throw new DataStoreException(
                        "Unable to create data for " + typeName, ex);
            }

        }
        return false;
    }

    /**
     * updates the schema.
     *
     * @param jsonSchema    the jsonSchema
     * @param validatedData the validatedData
     * @param idValues      the idValues
     * @return data
     */
    @Override
    public boolean updatePartially(final JsonSchema jsonSchema,
                                  final Map<String, Object> validatedData,
                                  final SimpleEntry<String, Object>... idValues)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Updating Data for {} with id {}", typeName, ids);

        if (ids != null) {
            UpdateRequest updateRequest =
                    new UpdateRequest(getIndexName(typeName),
                            getEncodedId(ids));
            updateRequest.doc(validatedData);
            UpdateResponse updateResponse;
            try {
                updateResponse =
                        client.update(updateRequest, RequestOptions.DEFAULT);
                if (updateResponse.getResult() == UPDATED) {
                    refreshIndex(getIndexName(typeName));
                    return true;
                }
            } catch (IOException ex) {
                throw new DataStoreException(
                        "Unable to create data for " + typeName, ex);
            }

        }
        return false;
    }

    /**
     * deletes the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param idValues   the idValues
     * @return data
     */
    @Override
    public boolean delete(final JsonSchema jsonSchema,
                          final SimpleEntry<String, Object>... idValues)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);
        String ids = jsonSchema.getIdValuesAsString(idValues);
        LOGGER.debug("Deleting Data for {} with id {}", typeName, ids);

        DeleteRequest deleteRequest = new DeleteRequest(
                getIndexName(typeName), getEncodedId(ids));

        DeleteResponse deleteResponse;
        try {
            deleteResponse =
                    client.delete(deleteRequest, RequestOptions.DEFAULT);
            return (deleteResponse.getResult() == DELETED);
        } catch (IOException ex) {
            throw new DataStoreException(
                    "Unable to delete data for " + typeName + "with id " + ids,
                    ex);
        }

    }

    /**
     * get type of the schema name.
     *
     * @param jsonSchema the jsonSchema
     * @return schema
     */
    private String getTypeName(final JsonSchema jsonSchema) {
        return jsonSchema.getJSONPropertyName(jsonSchema.getRoot().getId());
    }

    private Object getIdValue(final JsonSchema jsonSchema,
                              final Map<String, Object> jsonData) {
        return jsonSchema == null ? null : jsonSchema.getIdValues(jsonData)[0];
    }

    /**
     * deletes the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param queryNode  the queryNode
     * @return data
     */
    @Override
    public boolean delete(final JsonSchema jsonSchema, final Node queryNode)
            throws DataStoreException {

        String typeName = getTypeName(jsonSchema);
        String indexName2 = getIndexName(typeName);
        QueryBuilder builder = getQueryBuilder(jsonSchema, queryNode);
        if (builder != null) {

            try {
                // Patch for Solving delete by query conflicts
                client.getLowLevelClient().performRequest(
                        new Request("POST", "/" + indexName2 + "/_refresh"));
                HttpEntity entity = new NStringEntity(
                        "{" + "\"query\":" + builder + "}",
                        ContentType.APPLICATION_JSON);
                Request req = new Request("POST",
                        "/" + indexName2 + "/" + typeName
                                + "/_delete_by_query?scroll_size=1");
                req.setEntity(entity);
                client.getLowLevelClient()
                        .performRequest(req);
                client.getLowLevelClient().performRequest(
                        new Request("POST", "/" + indexName2 + "/_refresh"));
                refreshIndex(indexName2);
                return true;
            } catch (IOException ex) {
                throw new DataStoreException("Unable to delete " + indexName2,
                        ex);
            }
        }

        return false;
    }

    /**
     * list the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param queryNode  the queryNode
     * @return data
     */
    @Override
    public List<Map<String, Object>> list(final JsonSchema jsonSchema,
                                          final Node queryNode)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);

        LOGGER.debug("Listing Data for {} ", typeName);
        SearchRequest searchRequest =
                new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse =
                    client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
            if (totalHits != 0) {
                List<Map<String, Object>> list =
                        new ArrayList<>((int) totalHits);
                for (SearchHit hit : hits.getHits()) {
                    list.add(hit.getSourceAsMap());
                }
                return list;
            }

        } catch (ElasticsearchStatusException ex) {
            if (ex.status() == RestStatus.NOT_FOUND) {
                return null;
            } else {
                throw new DataStoreException(
                        "Unable to list data for " + typeName, ex);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Unable to list data for " + typeName,
                    ex);
        }
        return null;
    }

    /**
     * list the page of schema.
     *
     * @param jsonSchema the jsonSchema
     * @param queryNode  the queryNode
     * @param pageSize   the pageSize
     * @param pageNumber the pageNumber
     * @return data
     */
    @Override
    public Page<Map<String, Object>> list(final JsonSchema jsonSchema,
                                          final Node queryNode,
                                          final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        String typeName = getTypeName(jsonSchema);

        LOGGER.debug("Listing Data for {} page {} size {}", typeName,
                pageNumber, pageSize);
        SearchRequest searchRequest =
                new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse =
                    client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
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
                throw new DataStoreException(
                        "Unable to list data for " + typeName, ex);
            }
        } catch (IOException ex) {
            throw new DataStoreException("Unable to list data for " + typeName,
                    ex);
        }
        return null;
    }

    /**
     * querying the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param queryNode  the queryNode
     * @return data
     */
    private QueryBuilder getQueryBuilder(final JsonSchema jsonSchema,
                                         final Node queryNode)
            throws DataStoreException {

        BoolQueryBuilder bool_builder = QueryBuilders.boolQuery();
        if (queryNode != null) {
            bool_builder.must(queryNode.accept(new ElasticSearchVisitor(
                    new ElasticComparisonNodeInterpreter())));
        }
        if (jsonSchemaForSchema() != jsonSchema) {
            List<String> implementations =
                    dataStore.getImplementationsOf(jsonSchema);
            if (implementations == null || implementations.isEmpty()) {
                bool_builder.must(termQuery("$type", jsonSchema.getId()));
            } else {
                implementations.add(jsonSchema.getId());
                bool_builder.must(termsQuery("$type", implementations));
            }
        }

        //QueryBuilder builder = condition.query(new ElasticsearchVisitor(),
        //                                  new ElasticsearchVisitor.Context());
        return bool_builder;
    }

    /**
     * gets the encoded id.
     *
     * @param ids the id
     * @return id
     */
    private String getEncodedId(final Object ids) {
        return ids == null ? null
                : URLEncoder.encode(ids.toString(), StandardCharsets.UTF_8);
    }

    /**
     * gets the index name of the schema.
     *
     * @param typeName the typeName
     * @return data
     */
    private String getIndexName(final String typeName) {
        return indexName + "_" + typeName;
    }

    /**
     * browse the schema.
     *
     * @param schema     the schema
     * @param keyword    the keyword
     * @param queryNode  the queryNode
     * @param pageSize   the pageSize
     * @param pageNumber the pageNumber
     * @return data
     */
    @Override
    public AggregatedResults browse(final JsonSchema schema,
                                    final String keyword,
                                    final Node queryNode,
                                    final Integer pageNumber,
                                    final Integer pageSize)
            throws DataStoreException {
        AggregatedResults aggregatedResults = null;
        if (schema != null) {
//            if (keyword != null) {
//                query.addFilter(new Filter(Filter.Operator.FULL_TEXT_SEARCH,
//                                                              keyword + "*"));
//            }
            Map<String, Object> searchResponse =
                    searchResponse(schema, queryNode, pageNumber, pageSize);
            Page<Map<String, Object>> resultsOf =
                    pageOf(searchResponse, pageNumber, pageSize);
            if (resultsOf != null) {
                aggregatedResults = new AggregatedResults();
                aggregatedResults.setPage(resultsOf);
                aggregatedResults.setBuckets(bucketsOf(searchResponse));
            }
        }
        return aggregatedResults;
    }

    /**
     * list the buckets.
     *
     * @param searchResponse the searchResponse
     * @return data
     */
    private List<Map<String, Object>> bucketsOf(
            final Map<String, Object> searchResponse) {
        List<Map<String, Object>> buckets = null;
        if (searchResponse != null) {
            Map<String, Object> aggregations =
                    (Map<String, Object>) searchResponse.get("aggregations");
            if (aggregations != null) {
                Map<String, Object> bucket;
                Map<String, Object> bucketItem;
                List<Map<String, Object>> bucketItems;
                List<Map<String, Object>> bucketsMaps;

                buckets = new ArrayList<>();

                String aggregationName;
                for (Map.Entry<String, Object> entrySet
                        : aggregations.entrySet()) {

                    aggregationName = entrySet.getKey()
                            .substring(entrySet.getKey().indexOf("#") + 1);
                    if (!aggregationName.startsWith("max_")) {
                        bucket = new HashMap<>();
                        Object title =
                                ((Map<String, Object>) ((Map<String, Object>)
                                        aggregations.get(
                                           entrySet.getKey())).get("meta")).get(
                                        "title");
                        if (aggregationName.startsWith("min_")) {
                            bucket.put("name",
                                    aggregationName.replaceAll("min_", ""));
                            bucket.put("type", "minmax");
                            bucketItem = new HashMap<>();
                            bucketItem.put("min",
                                    ((Map<String, Object>) aggregations.get(
                                            entrySet.getKey())).get("value"));
                            bucketItem.put("max",
                                    ((Map<String, Object>) aggregations.get(
                                            entrySet.getKey()
                                                    .replaceAll("min_", "max_")
                                                    .replaceAll("min#",
                                                            "max#"))).get(
                                            "value"));
                            bucket.put("title",
                                    title == null ? bucket.get("name")
                                            : title);
                            bucket.put("item", bucketItem);
                        } else if (!aggregationName.startsWith("max_")) {
                            bucket.put("name", aggregationName);
                            bucket.put("type", "term");
                            bucketsMaps =
                                    (List<Map<String, Object>>) ((Map<String,
                                            Object>)
                                            entrySet.getValue()).get(
                                            "buckets");
                            bucketItems = new ArrayList<>();
                            for (Map<String, Object> bucketsMap : bucketsMaps) {
                                bucketItem = new HashMap<>();
                                bucketItem.put("name",
                                        bucketsMap.get("key").toString());
                                bucketItem.put("label",
                                        bucketsMap.get("key").toString());
                                bucketItem.put("count",
                                        bucketsMap.get("doc_count"));
                                bucketItems.add(bucketItem);
                            }
                            bucket.put("title",
                                    title == null ? bucket.get("name")
                                            : title);
                            bucket.put("items", bucketItems);
                        }
                        buckets.add(bucket);
                    }

                }

            }
        }
        return buckets;
    }

    /**
     * page of the schema.
     *
     * @param searchResponse the searchResponse
     * @param pageSize       the pageSize
     * @param pageNumber     the pageNumber
     * @return data
     */
    private Page<Map<String, Object>> pageOf(
            final Map<String, Object> searchResponse, final Integer pageNumber,
            final Integer pageSize) {
        Page<Map<String, Object>> page = null;
        List<Map<String, Object>> list = resultsOf(searchResponse);
        if (list != null) {
            Integer noOfRecords =
                    (Integer) ((Map<String, Object>) ((Map<String, Object>)
                            searchResponse.get(
                                    "hits")).get("total")).get("value");
            page = new Page(pageNumber, pageSize, Long.valueOf(noOfRecords),
                    list);
        }
        return page;
    }

    /**
     * list the  result of the schema.
     *
     * @param searchResponse the searchResponse
     * @return data
     */
    private List<Map<String, Object>> resultsOf(
            final Map<String, Object> searchResponse) {
        List<Map<String, Object>> list = null;
        if (searchResponse != null) {
            Integer noOfRecords =
                    (Integer) ((Map<String, Object>) ((Map<String, Object>)
                            searchResponse.get(
                                    "hits")).get("total")).get("value");
            if (0 != noOfRecords) {
                List<Map<String, Object>> recordsMapList =
                        (List<Map<String, Object>>) ((Map<String, Object>)
                                searchResponse.get(
                                        "hits")).get("hits");
                list = new ArrayList<>(recordsMapList.size());
                for (Map<String, Object> recordsMapList1 : recordsMapList) {
                    list.add((Map<String, Object>) recordsMapList1.get(
                            "_source"));
                }
            }
        }
        return list;
    }

    /**
     * add aggregation to the schema.
     *
     * @param jsonSchema           the jsonSchema
     * @param searchRequestBuilder the searchRequestBuilder
     */
    private void addAggregations(final JsonSchema jsonSchema,
                        final SearchSourceBuilder searchRequestBuilder) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "Types");
        searchRequestBuilder.aggregation(
                AggregationBuilders.terms("types").setMetadata(map)
                        .field("$type.keyword"));
        jsonSchema.getProperties().entrySet().parallelStream()
                .forEach(entry -> {
                    String filter = (String) entry.getValue().get("filter");
                    if (filter != null) {
                        String title = entry.getKey();
                        if (entry.getValue().get("title") != null) {
                            title = entry.getValue().get("title").toString();
                        }
                        switch (filter) {
                            case "minmax":
                                searchRequestBuilder
                                        .aggregation(AggregationBuilders
                                                .min("min_" + entry.getKey())
                                                .setMetadata(entry.getValue())
                                                .field(entry.getKey()))
                                        .aggregation(AggregationBuilders
                                                .max("max_" + entry.getKey())
                                                .setMetadata(entry.getValue())
                                                .field(entry.getKey()));
                                break;
                            case "term":
                                searchRequestBuilder
                                        .aggregation(AggregationBuilders
                                                .terms(entry.getKey())
                                                .setMetadata(entry.getValue())
                                                .field(entry.getKey()
                                                        + ".keyword"));

                                break;
                        }
                    }

                });

    }

    /**
     * search the schema.
     *
     * @param jsonSchema the jsonSchema
     * @param queryNode  the queryNode
     * @param pageNumber the pageNumber
     * @param pageSize   the pageSize
     * @return data
     */
    public Map<String, Object> searchResponse(final JsonSchema jsonSchema,
                                              final Node queryNode,
                                              final Integer pageNumber,
                                              final Integer pageSize)
            throws DataStoreException {

        String typeName = getTypeName(jsonSchema);

        SearchRequest searchRequest =
                new SearchRequest(getIndexName(typeName));
        searchRequest.types(typeName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(pageSize);
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.query(getQueryBuilder(jsonSchema, queryNode));
        addAggregations(jsonSchema, sourceBuilder);

        searchRequest.source(sourceBuilder);

        LOGGER.debug("Executing elastic search query {}",
                searchRequest);

        try {
            SearchResponse searchResponse =
                    client.search(searchRequest, RequestOptions.DEFAULT);
            return asMap(searchResponse.toString());
        } catch (IOException ex) {
            throw new DataStoreException(
                    "Unable to browse data for " + typeName, ex);
        }

    }

    /**
     * refresh the schema with index.
     *
     * @param anIndexName the indexName
     */
    private void refreshIndex(final String anIndexName) throws IOException {
        RefreshRequest request = new RefreshRequest(anIndexName);
        client.indices().refresh(request, RequestOptions.DEFAULT);
    }

    /**
     * on creates the new schema.
     *
     * @param jsonSchema the jsonSchema
     */
    @Override
    public void onNewSchema(final JsonSchema jsonSchema)
            throws DataStoreException {
        if (jsonSchema.isRoot()) {
            String typeName = jsonSchema.getId();
            String typeIndexName = getIndexName(typeName);
            String originalTypeIndexName = getIndexName(typeName) + "_base";

            try {

                GetIndexRequest request = new GetIndexRequest();
                request.indices(originalTypeIndexName);

                if (!client.indices()
                        .exists(request, RequestOptions.DEFAULT)) {
                    CreateIndexRequest createIndexRequest =
                            new CreateIndexRequest(originalTypeIndexName);
                    CreateIndexResponse createIndexResponse = client.indices()
                            .create(createIndexRequest,
                                    RequestOptions.DEFAULT);

                    if (createIndexResponse.isAcknowledged()) {
                        IndicesAliasesRequest aliasesRequest =
                                new IndicesAliasesRequest();
                        AliasActions aliasAction
                                = new AliasActions(AliasActions.Type.ADD)
                                .index(originalTypeIndexName)
                                .alias(typeIndexName);
                        aliasesRequest.addAliasAction(aliasAction);
                        AcknowledgedResponse indicesAliasesResponse
                                = client.indices()
                                .updateAliases(aliasesRequest,
                                        RequestOptions.DEFAULT);
                    }
                }

            } catch (IOException ex) {
                throw new DataStoreException(
                        "Unable to create index for " + typeName, ex);
            }

        }
    }

    /**
     * updates the schema.
     *
     * @param oldSchema the oldSchema
     * @param newSchema the newSchema
     */
    @Override
    public void onUpdateSchema(final JsonSchema oldSchema,
                               final JsonSchema newSchema)
            throws DataStoreException {
        Map<String, Map<String, Object>> oldPropertiesMap =
                oldSchema.getProperties();
        Map<String, Map<String, Object>> newPropertiesMap =
                newSchema.getProperties();
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
                    String newTypeIndexName =
                            typeIndexAliasName + System.currentTimeMillis();

                    Map<String, String> params =
                            Collections.singletonMap("pretty", "true");
                    String reindexRequest = "{\n"
                            + "  \"source\": {\n"
                            + "    \"index\": "
                            + "\"" + typeIndexAliasName + "\""
                            + ",\n"
                            + "    \"_source\": {\n"
                            + "        \"excludes\": "
                            + removedFields
                            + "    }\n"
                            + "  },\n"
                            + "  \"dest\": {\n"
                            + "    \"index\": \""
                            + newTypeIndexName
                            + "\"\n"
                            + "  }\n"
                            + "}";
                    HttpEntity entity = new NStringEntity(reindexRequest,
                            ContentType.APPLICATION_JSON);

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
                    entity = new NStringEntity(switchReq,
                            ContentType.APPLICATION_JSON);

                    req = new Request("POST", "/_aliases");

                    req.setEntity(entity);

                    client.getLowLevelClient()
                            .performRequest(req);

                    client.getLowLevelClient().performRequest(
                            new Request("DELETE", "/" + typeIndexName));

                } catch (IOException ex) {
                    throw new DataStoreException(
                            "Unable to switch alias index for " + typeName,
                            ex);
                }
            }
        }
    }

    /**
     * find name with index.
     *
     * @param aliasName the aliasName
     * @return data
     */
    private String findIndexName(final String aliasName) throws IOException {
        final StringBuilder index = new StringBuilder();
        GetAliasesRequest requestWithAlias = new GetAliasesRequest(aliasName);
        GetAliasesResponse response = client.indices()
                .getAlias(requestWithAlias, RequestOptions.DEFAULT);
        Map<String, Set<AliasMetadata>> aliasMap = response.getAliases();

        aliasMap.forEach((indexName2, md) -> {
            if (indexName2.startsWith(aliasName)) {
                index.append(indexName2);
            }
        });

        return index.toString().trim().length() == 0 ? null : index.toString();
    }

    /**
     * delete the schema.
     *
     * @param jsonSchema the jsonSchema
     */
    @Override
    public void onDeleteSchema(final JsonSchema jsonSchema)
            throws DataStoreException {
        // If root delete the index
        if (jsonSchema.isRoot()) {
            String typeName = getTypeName(jsonSchema);
            String indexAliasName = getIndexName(typeName);

            try {
                String typeIndexName = findIndexName(indexAliasName);
                DeleteIndexRequest request =
                        new DeleteIndexRequest(typeIndexName);
                AcknowledgedResponse deleteIndexResponse = client.indices()
                        .delete(request, RequestOptions.DEFAULT);
            } catch (IOException ex) {
                throw new DataStoreException(
                        "Unable to delete index for " + typeName, ex);
            }
        }
    }

    /**
     * initialize the schema.
     *
     * @param anDataStore an dataStore
     */
    @Override
    public void onIntialize(final DataStore anDataStore) {
        this.dataStore = anDataStore;
    }

}
