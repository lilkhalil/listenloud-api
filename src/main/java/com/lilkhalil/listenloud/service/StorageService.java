package com.lilkhalil.listenloud.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final FirebaseApp firebaseApp;
    
    @Value("${application.storage.url}")
    private String URL;

    public String upload(MultipartFile file) throws IOException {
        StorageClient storageClient = StorageClient.getInstance(firebaseApp);

        Bucket bucket = storageClient.bucket();

        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();

        bucket.create(fileName, file.getInputStream(), file.getContentType());
        
        return String.format(URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }   

    public ByteArrayResource download(String fileUrl) {
        StorageClient storageClient = StorageClient.getInstance(firebaseApp);

        Bucket bucket = storageClient.bucket();

        String fileName = this.extractFileName(fileUrl);

        Blob blob = bucket.get(fileName);

        return new ByteArrayResource(blob.getContent());
    }

    public void delete(String fileUrl) {
        StorageClient storageClient = StorageClient.getInstance(firebaseApp);

        Bucket bucket = storageClient.bucket();

        String fileName = this.extractFileName(fileUrl);

        Blob blob = bucket.get(fileName);

        blob.delete();
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

    private String extractFileName(String url) {

        Pattern pattern = Pattern.compile("https://firebasestorage\\.googleapis\\.com/v0/b/listenloud-storage\\.appspot\\.com/o/(.*?)\\?alt=media");
    
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) return matcher.group(1);

        else return null;      
    }

}
