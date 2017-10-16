/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.util;

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
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import static org.zols.datastore.elasticsearch.ElasticSearchDataStorePersistence.getQueryBuilder;
import org.zols.datastore.query.Query;
import static org.zols.datastore.util.JsonUtil.asMap;
import static org.zols.datastore.util.JsonUtil.asString;

@Component
public class ElasticSearchUtil {

    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchUtil.class);

    @Value("${spring.application.name}")
    private String indexName;

    //http://teknosrc.com/execute-raw-elasticsearch-query-using-transport-client-java-api/
    @Autowired
    private Client client;

    public AggregatedResults aggregatedSearch(String type,
            String template,
            Map<String, Object> queryValuesMap,
            Pageable pageable, Query query) {
        AggregatedResults aggregatedResults = null;
        queryValuesMap.put("size", pageable.getPageSize());
        queryValuesMap.put("from", (pageable.getPageNumber() * pageable.getPageSize()));
        Map<String, Object> searchResponse = searchResponse(type, template, queryValuesMap, query);
        Page<List> resultsOf = pageOf(searchResponse, pageable);
        if (resultsOf != null) {
            aggregatedResults = new AggregatedResults();
            aggregatedResults.setPage(resultsOf);
            aggregatedResults.setBuckets(bucketsOf(searchResponse));
        }
        return aggregatedResults;
    }

    public AggregatedResults aggregatedSearch(String type,
            String template,
            Map<String, Object> queryValuesMap,
            Pageable pageable) {
        return aggregatedSearch(type, template, queryValuesMap, pageable, null);
    }

//    public List<Map<String, Object>> search(String type, String template, Object model) {
//        return resultsOf(searchResponse(type, template, model));
//    }
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

    private Page<List> pageOf(Map<String, Object> searchResponse, Pageable pageable) {
        Page<List> page = null;
        List<Map<String, Object>> list = resultsOf(searchResponse);
        if (list != null) {
            Integer noOfRecords = (Integer) ((Map<String, Object>) searchResponse.get("hits")).get("total");
            page = new PageImpl(list, pageable, noOfRecords);
        }
        return page;
    }

    public Page<List> pageOf(String type,
            String template,
            Map<String, Object> queryValuesMap, Pageable pageable) {
        Page<List> page = null;
        queryValuesMap.put("size", pageable.getPageSize());
        queryValuesMap.put("from", (pageable.getPageNumber() * pageable.getPageSize()));
        Map<String, Object> searchResponse = searchResponse(type, template, queryValuesMap);
        List<Map<String, Object>> list = resultsOf(searchResponse);
        if (list != null) {
            Integer noOfRecords = (Integer) ((Map<String, Object>) searchResponse.get("hits")).get("total");
            page = new PageImpl(list, pageable, noOfRecords);
        }
        return page;
    }

    public Page<List> pageOf(String type,
            String template,Pageable pageable) {
        
        return pageOf(type, template, new HashMap<String, Object>(2), pageable);
    }

    public List<Map<String, Object>> resultsOf(String type,
            String template,
            Map<String, Object> queryValuesMap) {
        return resultsOf(searchResponse(type, template, queryValuesMap));
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

    public Map<String, Object> searchResponse(String type, String queryText, Query query) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName)
                .setTypes(type);
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
        SearchResponse response = searchRequestBuilder
                .execute()
                .actionGet();
        client.close();
        return asMap(response.toString());
    }

    public Map<String, Object> searchResponse(String type, String queryText) {
        return searchResponse(type, queryText, null);
    }

    public Map<String, Object> searchResponse(String type, String template, Object model, Query query) {
        return searchResponse(type, render(template, model), query);
    }

    public Map<String, Object> searchResponse(String type, String template, Object model) {
        return searchResponse(type, template, model, null);
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
