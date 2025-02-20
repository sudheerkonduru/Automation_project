// filepath: /e:/eclipse/DocumentManagementModulee/src/main/java/com/documentManagement/config/S3Config.java
package com.documentManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            "AKIA4VDBMA55V3WGHPEW", 
            "0cyO7O7N1JtkOxgyF8adujL0oMpK3o8Md5Mf46xl"
        );

        return S3Client.builder()
                .region(Region.of("ap-southeast-1"))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @Bean
    public String bucketName() {
        return "document11";
    }
}