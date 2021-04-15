package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.integration.endtoend

import com.amazonaws.services.sqs.AmazonSQS
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType.APPLICATION_JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
class TierTest {
  @Qualifier("awsSqsClient")
  @Autowired
  internal lateinit var awsSqsClient: AmazonSQS

  @Value("\${sqs.queue}")
  lateinit var queue: String

  private var hmppsTier: ClientAndServer = startClientAndServer(8081)
  private var communityApi: ClientAndServer = startClientAndServer(8082)

  @AfterEach
  fun reset() {
    hmppsTier.reset()
    communityApi.reset()
  }

  @AfterAll
  fun tearDownServer() {
    hmppsTier.stop()
    communityApi.stop()
  }

  @Test
  fun `will consume a TIER_CALCULATION_COMPLETE message, retrieve calculation and send update to community-api`() {
    setupTierCalculationResponse()
    val message = tierUpdateMessage()

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue() } matches { it == 0 }
    val tierWriteback = request().withPath("/offenders/crn/12345/tier/B3").withMethod("POST")
    setupTierWriteback(tierWriteback)

    awsSqsClient.sendMessage(queue, message)

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue() } matches { it == 0 }
    communityApi.verify(tierWriteback)
  }

  private fun setupTierCalculationResponse() {
    hmppsTier.`when`(request().withPath("/crn/12345/tier/e45559d1-3460-4a0e-8281-c736de57c562")).respond(
      response().withContentType(APPLICATION_JSON).withBody("{\"tierScore\":\"B3\"}")
    )
  }

  private fun setupTierWriteback(tierWriteback: HttpRequest?) {
    communityApi.`when`(tierWriteback).respond(
      response().withStatusCode(200)
    )
  }

  fun getNumberOfMessagesCurrentlyOnQueue(): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(queue, listOf("ApproximateNumberOfMessages"))
    return queueAttributes.attributes["ApproximateNumberOfMessages"]?.toInt()
  }
}
