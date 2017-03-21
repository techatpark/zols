/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zols.datastore.DataStore;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import org.zols.datastore.query.Query;
import static org.zols.datastore.web.util.HttpUtil.getPageUrl;
import static org.zols.datastore.web.util.HttpUtil.getQuery;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.service.BrowseService;

@Controller
@RequestMapping("/browse")
public class BrowseController {

    @Autowired
    private BrowseService categoryService;
    
    @Autowired
    private DataStore dataStore;

    @RequestMapping(value = "/{schemaName}")
    public String browseByCategory(Model model,
            @PathVariable("schemaName") String schemaName,
            HttpServletRequest request,
            Pageable pageable) throws DataStoreException {        
        return browseByCategoryWithKeyword(model, schemaName, null, request, pageable);
    }

    @RequestMapping(value = "/{schemaName}/{keyword}")
    public String browseByCategoryWithKeyword(Model model,
            @PathVariable("schemaName") String schemaName,
            @PathVariable("keyword") String keyword,
            HttpServletRequest request,
            Pageable pageable) throws DataStoreException {
        Query query = getQuery(request);
        model.addAttribute("query", query);   
        Map<String,Object> jsonSchema = dataStore.getRawJsonSchema(schemaName);
        model.addAttribute("schema", jsonSchema);
        List<String> parentNames = jsonSchema(jsonSchema).hierarchy();

        if(parentNames != null && parentNames.size() > 1) {
            List<Map<String,Object>> parents = new ArrayList<>();
            for (String parentName : parentNames) {
                parents.add(dataStore.getSchema(parentName));
            }
            model.addAttribute("parents",parents );
        }
        
        model.addAttribute("aggregations", categoryService.browseSchema(schemaName, keyword, query,pageable));
        String pageUrl = getPageUrl(request);
        model.addAttribute("pageurl", pageUrl);
        int indexOfQuestionMark = pageUrl.indexOf("?");
        model.addAttribute("cleanpageurl", (indexOfQuestionMark == -1) ? pageUrl : pageUrl.substring(0, indexOfQuestionMark));
        return "browse";
    }

}
