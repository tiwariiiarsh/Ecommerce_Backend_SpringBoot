package com.Ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    //    this part require some knowledge of file handling
    @Override
    public String  uploadImage(String path, MultipartFile file) throws IOException {
//        file name of current/original file
        String originalFileName = file.getOriginalFilename();
//        generate unique file name
        String randomId = UUID.randomUUID().toString();
//        randomId:"abc123xyz",originalFileName: "photo.png",concat:"abc123xyz" + ".png" → "abc123xyz.png"
//        path → folder ka path jaha file save karni hai (ex: "uploads/images").
//        filename → naya banaya hua unique file name (ex: "abc123.png").
//        File.pathSeparator ❌ → ye system ka path separator hota hai (: in Mac/Linux, ; in Windows). Ye folders ke beech use nahi hota.
//                Example: PATH environment variable me C:\bin;C:\jdk\bin → yaha ; separator hai.
        String filename = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + filename;

//        check if path exist and create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
//        upload to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));

//        returning filename
        return filename;

    }

}
