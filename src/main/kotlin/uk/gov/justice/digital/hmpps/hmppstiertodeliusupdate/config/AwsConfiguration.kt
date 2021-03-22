package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@EnableSqs
@Configuration
@ConditionalOnProperty(name = ["sqs.provider"], havingValue = "aws")
class AwsConfiguration(
  @Value("\${sqs.probation.access-key-id}") val accessKeyId: String,
  @Value("\${sqs.probation.secret-access-key}") val secretKey: String,
  @Value("\${sqs.endpoint.region}") val region: String,
) {

  @Primary
  @Bean
  fun amazonSQSAsync(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(accessKeyId, secretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Primary
  @Bean
  fun simpleMessageListenerContainerFactory(amazonSQSAsync: AmazonSQSAsync):
    SimpleMessageListenerContainerFactory {
      val factory = SimpleMessageListenerContainerFactory()
      factory.setAmazonSqs(amazonSQSAsync)
      factory.setMaxNumberOfMessages(1)
      factory.setWaitTimeOut(20)
      return factory
    }
}
