package com.example.driveservice.config;

import com.example.driveservice.dao.mongo.DriveRepository;
import com.example.driveservice.dao.redis.VfsCacheRepository;
import com.example.driveservice.fs.VirtualFileSystemFactory;
import com.example.driveservice.service.UploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ApplicationConfig {

  @Value("${aws.region}")
  private String region;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  @Bean
  public VirtualFileSystemFactory virtualFileSystemFactory(DriveRepository repo,
      VfsCacheRepository cacheRepo, UploadService uploadService, ObjectMapper objectMapper) {
    return new VirtualFileSystemFactory(repo, cacheRepo, uploadService, objectMapper);
  }
}
