/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.util;

import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import static org.zols.datastore.query.Filter.Operator.EXISTS_IN;
import static org.zols.datastore.query.Filter.Operator.IN_BETWEEN;
import org.zols.datastore.query.Query;

/**
 *
 */
public class HttpUtil {

    /**
     * Removes page,size kind of parameters from request url
     *
     * @param request
     * @return
     */
    public static String getPageUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();
            url = url.replaceAll("[&?]page.*?(?=&|\\?|$)", "")
                    .replaceAll("[&?]size.*?(?=&|\\?|$)", "");
        }
        return url;
    }

    public static Query getQuery(HttpServletRequest request) {
        Query query = null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null) {
//            parameterMap.remove("page");
//            parameterMap.remove("size");
            if (!parameterMap.isEmpty()) {
                query = new Query();
                for (Map.Entry<String, String[]> entrySet : parameterMap.entrySet()) {
                    String k = entrySet.getKey();
                    String[] v = entrySet.getValue();
                    if (!k.equals("page") && !k.equals("size")) {
                        if (v.length == 1) {
                            String value = v[0];
                            if (value.contains(",")) {
                                query.addFilter(new Filter(k, EXISTS_IN, Arrays.asList(value.split(","))));
                            } else if (value.matches("\\[(.*?)\\]")) {
                                String[] rangeValues = value.substring(1).replaceAll("]", "").split("-");
                                query.addFilter(new Filter(k, IN_BETWEEN, Double.parseDouble(rangeValues[0]), Double.parseDouble(rangeValues[1])));
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
