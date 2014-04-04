/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documentmanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zols.documentmanager.domain.Document;

/**
 *
 * @author navin_kr
 */
@Service
public class DocumentManager {

    public void upload(Document document, String documentPath) throws IOException {
        List<MultipartFile> multipartFiles = document.getFiles();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                //Handle file content - multipartFile.getInputStream()
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(new File(documentPath + File.separator + multipartFile.getOriginalFilename())));
                stream.write(bytes);
                stream.close();
            }
        }
    }

}
