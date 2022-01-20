/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.util;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.zols.datastore.query.Filter;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import static org.zols.datastore.query.Filter.Operator.IN_BETWEEN;

/**
 *
 */
public class HttpUtil {

    private HttpUtil() {

    }

    /**
     * Removes page,size kind of parameters from request url.
     *
     * @param request
     * @return url
     */
    public static String getPageUrl(final HttpServletRequest request) {
        String url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();
            url = url.replaceAll("[&?]page.*?(?=&|\\?|$)", "")
                    .replaceAll("[&?]size.*?(?=&|\\?|$)", "");
        }
        return url;
    }

    /**
     * get the query.
     *
     * @param request the request
     * @return query
     */
    public static Condition<MapQuery> getQuery(
            final HttpServletRequest request) {
        Condition<MapQuery> condition = null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null) {
//            parameterMap.remove("page");
//            parameterMap.remove("size");
            if (!parameterMap.isEmpty()) {
                for (Map.Entry<String, String[]> entrySet
                        : parameterMap.entrySet()) {
                    String k = entrySet.getKey();
                    String[] v = entrySet.getValue();
                    if (!k.equals("page") && !k.equals("size")
                            && !k.equals("lang") && !k.equals("q")) {
                        if (v.length == 1) {
                            String value = v[0];
                            if (value.contains(",")) {
                                if (condition == null) {
                                    condition = new MapQuery().string(k)
                                            .in(Arrays.asList(
                                                    value.split(",")));
                                } else {
                                    condition.and().string(k).in(Arrays.asList(
                                            value.split(",")));
                                }
                                //query.addFilter(new Filter(k, EXISTS_IN, )));
                            } else if (value.matches("\\[(.*?)\\]")) {
                                String[] rangeValues =
                                        value.substring(1).replaceAll("]", "")
                                                .split("-");
                                if (condition == null) {
                                    condition = new MapQuery().doubleNum(k)
                                            .gte(Double.parseDouble(
                                                    rangeValues[0])).and()
                                            .doubleNum(k)
                                            .gte(Double.parseDouble(
                                                    rangeValues[1]));
                                } else {
                                    condition.and().doubleNum(k)
                                            .gte(Double.parseDouble(
                                                    rangeValues[0])).and()
                                            .doubleNum(k)
                                            .gte(Double.parseDouble(
                                                    rangeValues[1]));
                                }
                            } else {
                                if (condition == null) {
                                    condition =
                                            new MapQuery().string(k).eq(value);
                                } else {
                                    condition.and().string(k).eq(value);
                                }
                            }
                        }
                    }
                }
            }
        }
        return condition;
    }

    /**
     * get the legacy query.
     *
     * @param request the request
     * @return query
     */
    public static Query getLegacyQuery(final HttpServletRequest request) {
        Query query = null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null) {
//            parameterMap.remove("page");
//            parameterMap.remove("size");
            if (!parameterMap.isEmpty()) {
                query = new Query();
                for (Map.Entry<String,
                        String[]> entrySet : parameterMap.entrySet()) {
                    String k = entrySet.getKey();
                    String[] v = entrySet.getValue();
                    if (!k.equals("page") && !k.equals("size")
                            && !k.equals("lang") && !k.equals("q")) {
                        if (v.length == 1) {
                            String value = v[0];
                            if (value.contains(",")) {
                                query.addFilter(new Filter(k, EXISTS_IN,
                                        Arrays.asList(value.split(","))));
                            } else if (value.matches("\\[(.*?)\\]")) {
                                String[] rangeValues =
                                        value.substring(1).replaceAll("]", "")
                                                .split("-");
                                query.addFilter(new Filter(k, IN_BETWEEN,
                                        Double.parseDouble(rangeValues[0]),
                                        Double.parseDouble(rangeValues[1])));
                            } else {
                                query.addFilter(new Filter(k, EQUALS, value));
                            }
                        }
                    }
                }
                if (query.getFilters().isEmpty()) {
                    query = null;
                }
            }
        }
        return query;
    }
}
