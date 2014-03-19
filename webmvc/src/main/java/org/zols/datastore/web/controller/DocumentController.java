package org.zols.datastore.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.zols.datastore.domain.Document;

@Controller
public class DocumentController {

	
	@RequestMapping(value = "/documents/{{name}}", method = RequestMethod.GET)
	public String documents(@PathVariable(value = "name") String name) {
		return "com/zols/datastore/document";
	}
	
	@RequestMapping(value = "/documents/save", method = RequestMethod.POST)
	public String save(
			@ModelAttribute("document") Document document,
					Model map) {
		
		List<MultipartFile> files = document.getFiles();

		List<String> fileNames = new ArrayList<String>();
		
		if(null != files && files.size() > 0) {
			for (MultipartFile multipartFile : files) {

				String fileName = multipartFile.getOriginalFilename();
				fileNames.add(fileName);
				//Handle file content - multipartFile.getInputStream()

			}
		}
		
		map.addAttribute("files", fileNames);
		return "file_upload_success";
	}
}
