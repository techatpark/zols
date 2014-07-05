package org.zols.documents.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zols.documents.domain.Document;
import org.zols.documents.domain.DocumentRepository;
import org.zols.documents.domain.Upload;
import org.zols.documents.service.DocumentRepositoryService;
import org.zols.documents.service.DocumentService;

@RestController
@RequestMapping(value = "/api/documents")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentController.class);

    @Autowired
    DocumentRepositoryService documentRepositoryService;
    @Autowired
    DocumentService documentService;

    @RequestMapping(value = "/{documentRepositoryName}/**", method = RequestMethod.GET)
    public List<Document> list(@PathVariable(value = "documentRepositoryName") String documentRepositoryName,HttpServletRequest request) {
        String folderPath = request.getRequestURL().toString();
        String textToMatch = "/api/documents/"+documentRepositoryName+"/"; 
        int startPointOdFolderPath = folderPath.indexOf(textToMatch);
        if(startPointOdFolderPath != -1) {
            folderPath = folderPath.substring(startPointOdFolderPath+textToMatch.length());
            LOGGER.info("Listing documents from Repository {} under folder {}", documentRepositoryName,folderPath);
            return documentService.list(documentRepositoryName,folderPath);
        }
        else {
            LOGGER.info("Listing documents from Repository {}", documentRepositoryName);
            return documentService.list(documentRepositoryName);
        }        
    }

    @RequestMapping(value = "/{documentRepositoryName}", method = RequestMethod.POST)
    public String upload(@PathVariable(value = "documentRepositoryName") String documentRepositoryName, @ModelAttribute("document") Upload document, Model map) throws IOException {
        DocumentRepository documentRepository = documentRepositoryService.read(documentRepositoryName);
        map.addAttribute("documentRepository", documentRepository);
        documentService.upload(documentRepository, document);
        map.addAttribute("files", new File(documentRepository.getPath()).listFiles());
        return "org/zols/documentmanager/document";
    }
}
