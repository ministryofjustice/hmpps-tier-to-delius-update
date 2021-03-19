package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.integration.endtoend

import com.amazonaws.services.sqs.AmazonSQS
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage

@SpringBootTest
@ActiveProfiles("test")
class TierTest {
  @Qualifier("awsSqsClient")
  @Autowired
  internal lateinit var awsSqsClient: AmazonSQS

  @Value("\${sqs.queue}")
  lateinit var queue: String

  @Test
  fun `will consume a HMPPS_TIER_CALCULATION_COMPLETE message`() {
    val message = tierUpdateMessage()

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue() } matches { it == 0 }

    awsSqsClient.sendMessage(queue, message)

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue() } matches { it == 0 }
  }

  fun getNumberOfMessagesCurrentlyOnQueue(): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(queue, listOf("ApproximateNumberOfMessages"))
    return queueAttributes.attributes["ApproximateNumberOfMessages"]?.toInt()
  }

  fun String.queueUrl(): String = awsSqsClient.getQueueUrl(this).queueUrl
}
