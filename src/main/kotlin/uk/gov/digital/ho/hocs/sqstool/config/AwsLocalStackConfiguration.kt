package uk.gov.digital.ho.hocs.sqstool.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = ["sqs-provider"], havingValue = "localstack")
class AwsLocalStackConfiguration(
  @Value("\${search-queue.endpoint}") val searchEndpoint: String,
  @Value("\${search-dlq.endpoint}") val searchDlqEndpoint: String,
  @Value("\${audit-queue.endpoint}") val auditEndpoint: String,
  @Value("\${audit-dlq.endpoint}") val auditDlqEndpoint: String,
  @Value("\${document-queue.endpoint}") val documentEndpoint: String,
  @Value("\${document-dlq.endpoint}") val documentDlqEndpoint: String,
  @Value("\${notify-queue.endpoint}") val notifyEndpoint: String,
  @Value("\${notify-dlq.endpoint}") val notifyDlqEndpoint: String,
  @Value("\${sqs-region}") val region: String
) {

  @Bean(name = ["searchAwsSqsClient"])
  fun searchAwsSqsClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(searchEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["searchAwsSqsDlqClient"])
  fun searchAwsSqsDlqClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(searchDlqEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["auditAwsSqsClient"])
  fun auditAwsSqsClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(auditEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["auditAwsSqsDlqClient"])
  fun auditAwsSqsDlqClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(auditDlqEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["documentAwsSqsClient"])
  fun documentAwsSqsClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(documentEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["documentAwsSqsDlqClient"])
  fun documentAwsSqsDlqClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(documentDlqEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["notifyAwsSqsClient"])
  fun notifyAwsSqsClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(notifyEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }

  @Bean(name = ["notifyAwsSqsDlqClient"])
  fun notifyAwsSqsDlqClient(): AmazonSQSAsync {
    return AmazonSQSAsyncClientBuilder.standard()
      .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(notifyDlqEndpoint, region))
      .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
      .build()
  }
}
