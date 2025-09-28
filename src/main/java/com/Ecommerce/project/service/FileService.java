package com.Ecommerce.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    //    this part require some knowledge of file handling
    String  uploadImage(String path, MultipartFile file) throws IOException;
}
