package org.zols.documentmanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zols.documentmanager.DocumentManager;
import org.zols.documentmanager.DocumentStorageManager;
import org.zols.documentmanager.domain.Document;
import org.zols.documentmanager.domain.DocumentStorage;

@Controller
public class DocumentController {

    @Autowired
    DocumentStorageManager documentStorageManager;
    @Autowired
    DocumentManager documentManager;

    @RequestMapping(value = "/documents/{name}", method = RequestMethod.GET)
    @ApiIgnore
    public String documents(@PathVariable(value = "name") String name, Model map) {
        DocumentStorage documentStorage = documentStorageManager.get(name);
        map.addAttribute("documentStorage", documentStorage);
        map.addAttribute("files", new File(documentStorage.getPath()).listFiles());
        return "org/zols/documentmanager/document";
    }

    @RequestMapping(value = "/documents/{name}", method = RequestMethod.POST)
    @ApiIgnore
    public String save(@PathVariable(value = "name") String name, @ModelAttribute("document") Document document, Model map) throws IOException {
        DocumentStorage documentStorage = documentStorageManager.get(name);
        map.addAttribute("documentStorage", documentStorage);
        documentManager.upload(document, documentStorage.getPath());
        map.addAttribute("files", new File(documentStorage.getPath()).listFiles());
        return "org/zols/documentmanager/document";
    }
}
