/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch.util;

import org.zols.datastore.query.AggregatedResults;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import static org.slf4j.LoggerFactory.getLogger;
import static org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence.getQueryBuilder;
import org.zols.datastore.query.Page;
import org.zols.datastore.query.Query;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asString;
import org.zols.jsonschema.JsonSchema;

public class ElasticSearchUtil {

    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchUtil.class);

    private final Client client;

    private final String indexName;

    public ElasticSearchUtil(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
    }

    public AggregatedResults aggregatedSearch(JsonSchema jsonSchema,
            String template,
            Map<String, Object> queryValuesMap,
            Integer pageNumber,
            Integer pageSize, Query query) {
        AggregatedResults aggregatedResults = null;
        queryValuesMap.put("size", pageSize);
        queryValuesMap.put("from", pageNumber * pageSize);
        Map<String, Object> searchResponse = searchResponse(jsonSchema, template, queryValuesMap, query, pageNumber, pageSize);
        Page<Map<String, Object>> resultsOf = pageOf(searchResponse, pageNumber, pageSize);
        if (resultsOf != null) {
            aggregatedResults = new AggregatedResults();
            aggregatedResults.setPage(resultsOf);
            aggregatedResults.setBuckets(bucketsOf(searchResponse));
        }
        return aggregatedResults;
    }

    public AggregatedResults aggregatedSearch(JsonSchema jsonSchema,
            String template,
            Map<String, Object> queryValuesMap,
            Integer pageNumber,
            Integer pageSize) {
        return aggregatedSearch(jsonSchema, template, queryValuesMap, pageNumber, pageSize, null);
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

                    aggregationName = entrySet.getKey();
                    if (!aggregationName.startsWith("max_")) {
                        bucket = new HashMap<>();
                        if (aggregationName.startsWith("min_")) {
                            bucket.put("name", aggregationName.replaceAll("min_", ""));
                            bucket.put("type", "minmax");
                            bucketItem = new HashMap<>();
                            bucketItem.put("min", ((Map<String, Object>) aggregations.get(aggregationName)).get("value"));
                            bucketItem.put("max", ((Map<String, Object>) aggregations.get(aggregationName.replaceAll("min_", "max_"))).get("value"));
                            bucket.put("item", bucketItem);
                        } else {
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

    public Page<List> pageOf(JsonSchema jsonSchema,
            String template,
            Map<String, Object> queryValuesMap, Integer pageNumber,
            Integer pageSize) {
        Page<List> page = null;
        queryValuesMap.put("size", pageSize);
        queryValuesMap.put("from", pageNumber * pageSize);
        Map<String, Object> searchResponse = searchResponse(jsonSchema, template, queryValuesMap, pageNumber, pageSize);
        List<Map<String, Object>> list = resultsOf(searchResponse);
        if (list != null) {
            Long noOfRecords = new Long(((Map<String, Object>) searchResponse.get("hits")).get("total").toString());
            page = new Page(pageNumber, pageSize, noOfRecords, list);
        }
        return page;
    }

    public Page<List> pageOf(JsonSchema jsonSchema,
            String template, Integer pageNumber,
            Integer pageSize) {

        return pageOf(jsonSchema, template, new HashMap<>(2), pageNumber, pageSize);
    }

    public List<Map<String, Object>> resultsOf(JsonSchema jsonSchema,
            String template,
            Map<String, Object> queryValuesMap, Integer pageNumber,
            Integer pageSize) {
        return resultsOf(searchResponse(jsonSchema, template, queryValuesMap, pageNumber, pageSize));
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

    private void addAggregations(JsonSchema jsonSchema,
            SearchRequestBuilder searchRequestBuilder) {
        searchRequestBuilder.addAggregation(AggregationBuilders.terms("Types").field("$type"));
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
                                .addAggregation(AggregationBuilders.min("min_" + title).field(entry.getKey()))
                                .addAggregation(AggregationBuilders.max("max_" + title).field(entry.getKey()));
                        break;
                    case "terms":
                        searchRequestBuilder
                                .addAggregation(AggregationBuilders.terms(title).field(entry.getKey()));

                        break;
                }
            }

        });

    }

    public Map<String, Object> searchResponse(JsonSchema jsonSchema, String queryText, Query query, Integer pageNumber, Integer pageSize) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName)
                .setTypes(jsonSchema.getJSONPropertyName(jsonSchema.getRoot().getId()));

        if (pageNumber != null) {
            searchRequestBuilder.setSize(pageSize)
                    .setFrom(pageNumber * pageSize);
        }
        if (query != null) {
            QueryBuilder builder = getQueryBuilder(query);
            Map<String, Object> queryAsMap = asMap(queryText);
            List filter = (List) ((Map<String, Object>) ((Map<String, Object>) queryAsMap.get("query")).get("bool")).get("filter");
            filter.add(asMap(builder.toString()));
            String filterAppendedQuery = asString(queryAsMap);
            LOGGER.debug("Executing Elastic Search Query\n{}", filterAppendedQuery);
            searchRequestBuilder
                    .setSource(filterAppendedQuery);
        } else {
            LOGGER.debug("Executing Elastic Search Query\n{}", queryText);
            searchRequestBuilder
                    .setSource(queryText);
        }

        addAggregations(jsonSchema, searchRequestBuilder);

        SearchResponse response = searchRequestBuilder
                .execute()
                .actionGet();

        return asMap(response.toString());
    }

    public Map<String, Object> searchResponse(JsonSchema jsonSchema, String queryText, Integer pageNumber, Integer pageSize) {
        return searchResponse(jsonSchema, queryText, null, pageNumber, pageSize);
    }

    public Map<String, Object> searchResponse(JsonSchema jsonSchema, String template, Object model, Query query, Integer pageNumber, Integer pageSize) {
        return searchResponse(jsonSchema, render(template, model), query, pageNumber, pageSize);
    }

    public Map<String, Object> searchResponse(JsonSchema jsonSchema, String template, Object model, Integer pageNumber, Integer pageSize) {
        return searchResponse(jsonSchema, template, model, null, pageNumber, pageSize);
    }

    public static String getContentFromClasspath(String resourcePath) {
        InputStream inputStream = ElasticSearchUtil.class.getResourceAsStream(resourcePath);
        java.util.Scanner scanner = new java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        String theString = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return theString;
    }

    public static String render(String templateName, Object model) {
        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(getContentFromClasspath("/search/templates/" + templateName + ".mustache")), "example");
        mustache.execute(writer, model);
        try {
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ElasticSearchUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return writer.toString();
    }

}
