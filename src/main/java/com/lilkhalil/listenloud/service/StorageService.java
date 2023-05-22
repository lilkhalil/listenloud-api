package com.lilkhalil.listenloud.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3 s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    private final String BUCKET_URL = "https://listenloudstorage.storage.yandexcloud.net/";

    public String upload(MultipartFile file) throws IOException {
        String fileKey = file.getContentType().substring(0, 6) + UUID.randomUUID().toString() + file.getOriginalFilename();
        File fileObj = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileKey, fileObj));
        fileObj.delete();
        return BUCKET_URL + fileKey;
    }

    public void delete(String fileUrl) {
        String fileKey = fileUrl.split(BUCKET_URL)[1];
        s3Client.deleteObject(bucketName, fileKey);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("Error: Converting multipartFile to file has been failed");
        }
        return convertedFile;
    }

    public void isValidMediaType(MultipartFile file) throws NotValidContentTypeException {
        if (file.getContentType().startsWith("audio")) {
            if (!file.getContentType().equals("audio/mpeg") && !file.getContentType().equals("audio/wav"))
                throw new NotValidContentTypeException("Error: File format compatible with audio: MPEG, WAV!");
        }
        else if (file.getContentType().startsWith("image")) {
            if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png"))
                throw new NotValidContentTypeException("Error: File format compatible with image: JPEG, PNG");
        }
        else throw new NotValidContentTypeException("Error: Invalid file format is given!");
    }

}
