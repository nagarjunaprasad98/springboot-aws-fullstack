package com.sb1.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

@Configuration
public class CloudWatchConfig {

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public CloudWatchLogsClient cloudWatchLogsClient() {

        System.out.println(System.getenv("AWS_ACCESS_KEY_ID"));
        System.out.println(System.getenv("AWS_SECRET_ACCESS_KEY"));

        System.out.println("AWS ACCESS KEY LOADED = " + (accessKey != null && !accessKey.isBlank()));
        System.out.println("AWS SECRET KEY LOADED = " + (secretKey != null && !secretKey.isBlank()));
        System.out.println("AWS REGION = " + region);

        AwsBasicCredentials creds =
                AwsBasicCredentials.create(accessKey, secretKey);

        return CloudWatchLogsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(creds)
                )
                .build();
    }
}
