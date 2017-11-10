/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web;


import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zols.datastore.DataStore;
import org.zols.datastore.query.Query;
import static org.zols.datastore.web.util.HttpUtil.getPageUrl;
import static org.zols.datastore.web.util.HttpUtil.getQuery;
import org.zols.datastore.web.util.SpringAggregatedResults;
import static org.zols.datastore.web.util.SpringConverter.getAggregatedResults;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.service.BrowseService;
import org.zols.jsonschema.JsonSchema;
import org.zols.web.PageWrapper;

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
            Pageable pageable,Locale locale) throws DataStoreException {        
        return browseByCategoryWithKeyword(model, schemaName, null, request, pageable,locale);
    }

    @RequestMapping(value = "/{schemaName}/{keyword}")
    public String browseByCategoryWithKeyword(Model model,
            @PathVariable("schemaName") String schemaId,
            @PathVariable("keyword") String keyword,
            HttpServletRequest request,
            Pageable pageable,Locale locale) throws DataStoreException {
        Query query = getQuery(request);
        model.addAttribute("query", query);   
        JsonSchema jsonSchema = dataStore.getSchemaManager().getJsonSchema(schemaId);
        model.addAttribute("schema", jsonSchema.getCompositeSchema());
        model.addAttribute("parents",jsonSchema.getParents());
        SpringAggregatedResults aggregatedResults = getAggregatedResults(categoryService.browseSchema(schemaId, keyword, query,locale,pageable.getPageNumber(),pageable.getPageSize()),pageable);
        model.addAttribute("aggregations",aggregatedResults) ;
        if(aggregatedResults != null) {
            model.addAttribute("pageWrapper", new PageWrapper<>(aggregatedResults.getPage(), "/browse/"+schemaId));
        }
        String pageUrl = getPageUrl(request);
        model.addAttribute("pageurl", pageUrl);
        int indexOfQuestionMark = pageUrl.indexOf("?");
        model.addAttribute("cleanpageurl", (indexOfQuestionMark == -1) ? pageUrl : pageUrl.substring(0, indexOfQuestionMark));
        return "browse";
    }

}
