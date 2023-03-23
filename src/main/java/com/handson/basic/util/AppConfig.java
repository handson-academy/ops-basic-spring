package com.handson.basic.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig  {

    @Autowired
    private Environment env;

    private String secretKey;
    private String accessKey;

    @PostConstruct
    public void init() {
        this.accessKey = env.getRequiredProperty("amazon.aws.accesskey").trim();
        this.secretKey = env.getRequiredProperty("amazon.aws.secretkey").trim();
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3 amazonS3() {
        ClientConfiguration cf = new ClientConfiguration();
        cf.setMaxErrorRetry(5);
        cf.setConnectionTTL(TimeUnit.MINUTES.toMillis(5));
        cf.setMaxConnections(100);
        cf.setMaxErrorRetry(5);
        cf.setSignerOverride("AWSS3V4SignerType");
        return AmazonS3ClientBuilder.standard()
                .withRegion("eu-central-1")
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(cf)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .build();
    }
}
