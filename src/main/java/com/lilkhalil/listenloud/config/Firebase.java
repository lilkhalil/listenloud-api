package com.lilkhalil.listenloud.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class Firebase {
    
    @Bean
    public FirebaseApp initializeStorage() throws IOException {

        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket("listenloud-storage.appspot.com")
            .build();

        return FirebaseApp.initializeApp(options);
    }

}
