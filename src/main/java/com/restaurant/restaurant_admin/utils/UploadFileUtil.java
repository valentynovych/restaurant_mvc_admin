package com.restaurant.restaurant_admin.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class UploadFileUtil {
    @Value("${upload.path}")
    private String uploadPath;

    public List<String> saveUploadFiles(List<MultipartFile> multipartFileList, List<String> imageNames) throws IOException {

        ArrayList<String> fileNameList = new ArrayList<>(imageNames);
        if (multipartFileList.size() > imageNames.size()) {
            int res = multipartFileList.size() - imageNames.size();
            for (int i = 0; i < res; i++) {
                fileNameList.add("");
            }
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        for (int i = 0; i < fileNameList.size(); i++) {
            MultipartFile image = multipartFileList.get(i);
            if (image.getOriginalFilename().equals("empty.png")) {
                File deleteImage = new File(uploadPath + "/" + fileNameList.get(i));
                deleteImage.delete();
                fileNameList.set(i, null);
            } else if (!image.isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                String fileName = uuidFile + "_" + image.getOriginalFilename();
                image.transferTo(new File(uploadPath + "/" + fileName));
                fileNameList.set(i, fileName);
            }
        }
        return fileNameList;
    }

    public void deleteUploadFile(String filename) {
            File deletedImage = new File(uploadPath + "/" + filename);
            if (deletedImage.delete()) {
                System.out.println("image" + deletedImage.getAbsolutePath() + "has been delete");
            } else {
                System.out.println("image" + deletedImage.getAbsolutePath() + "not delete");
            }
        }

    public String saveImage(MultipartFile previewIconFile) throws IOException {
        createDirectoryIfNotExist();
        MultipartFile image = previewIconFile;

        String uuidFile = UUID.randomUUID().toString();
        String fileName = uuidFile + "_" + image.getOriginalFilename();
        image.transferTo(new File(uploadPath + "/" + fileName));

        return fileName;
    }

    private void createDirectoryIfNotExist() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean mkdir = uploadDir.mkdir();
            if (mkdir) {
                log.info("create directory for uploads file");
            } else {
                log.info("directory for uploads file is not create");
            }
        }
    }
}
