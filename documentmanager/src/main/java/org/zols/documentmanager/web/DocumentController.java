package org.zols.documentmanager.web;

import org.zols.documentmanager.DocumentStorageManager;
import org.zols.documentmanager.domain.Document;
import org.zols.documentmanager.domain.DocumentStorage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DocumentController {

    @Autowired
    DocumentStorageManager documentStorageManager;

    @RequestMapping(value = "/documents/{name}", method = RequestMethod.GET)
    public String documents(@PathVariable(value = "name") String name, Model map) {
        DocumentStorage documentStorage = documentStorageManager.get(name);
        map.addAttribute("documentStorage", documentStorage);
        map.addAttribute("files", new File(documentStorage.getPath()).listFiles());
        return "org/zols/documentmanager/document";
    }

    @RequestMapping(value = "/documents/{name}", method = RequestMethod.POST)
    public String save(@PathVariable(value = "name") String name, @ModelAttribute("document") Document document, Model map) throws IOException {
        DocumentStorage documentStorage = documentStorageManager.get(name);
        map.addAttribute("documentStorage", documentStorage);
        List<MultipartFile> multipartFiles = document.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(new File(documentStorage.getPath() + File.separator + multipartFile.getOriginalFilename())));
                stream.write(bytes);
                stream.close();
            }
        }
        map.addAttribute("files", new File(documentStorage.getPath()).listFiles());
        return "org/zols/documentmanager/document";
    }
}
