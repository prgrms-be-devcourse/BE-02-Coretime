package com.prgrms.coretime;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class S3MockConfig {
  @Value("${cloud.aws.region.static}")
  private String region;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Bean
  public S3Mock s3Mock() {
    return new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
  }

  @Bean(destroyMethod = "shutdown")
  @Primary
  public AmazonS3Client amazonS3Client(S3Mock s3Mock) {
    s3Mock.start();
    AwsClientBuilder.EndpointConfiguration endpoint = new EndpointConfiguration("http://localhost:8001", region);
    AmazonS3Client client = (AmazonS3Client) AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(
            new AnonymousAWSCredentials()
        )).build();
    client.createBucket(bucket);
    return client;
  }
}
