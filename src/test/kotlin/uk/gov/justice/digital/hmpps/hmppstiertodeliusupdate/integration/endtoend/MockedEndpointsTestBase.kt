package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.integration.endtoend

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import com.google.gson.Gson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType.APPLICATION_JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingQueueException

@SpringBootTest()
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
internal abstract class MockedEndpointsTestBase {

  @Qualifier("tiercalculationqueue-sqs-client")
  @Autowired
  internal lateinit var awsSqsClient: AmazonSQS

  protected val queue by lazy { hmppsQueueService.findByQueueId("tiercalculationqueue")?.queueUrl ?: throw MissingQueueException("HmppsQueue tiercalculationqueue queue not found") }
  protected val dlq by lazy { hmppsQueueService.findByQueueId("tiercalculationqueue")?.dlqUrl ?: throw MissingQueueException("HmppsQueue tiercalculationqueue dlq not found") }

  @Autowired
  protected lateinit var hmppsQueueService: HmppsQueueService

  var hmppsTier: ClientAndServer = startClientAndServer(8091)
  var communityApi: ClientAndServer = startClientAndServer(8092)

  private var oauthMock: ClientAndServer = startClientAndServer(9090)

  private val gson: Gson = Gson()

  @BeforeEach
  fun before() {
    awsSqsClient.purgeQueue(PurgeQueueRequest(queue))
    awsSqsClient.purgeQueue(PurgeQueueRequest(dlq))
    setupOauth()
  }

  @AfterEach
  fun reset() {
    hmppsTier.reset()
    communityApi.reset()
    oauthMock.reset()
    awsSqsClient.purgeQueue(PurgeQueueRequest(queue))
    awsSqsClient.purgeQueue(PurgeQueueRequest(dlq))
  }

  @AfterAll
  fun tearDownServer() {
    hmppsTier.stop()
    communityApi.stop()
    oauthMock.stop()
  }

  fun setupOauth() {
    val response = response().withContentType(APPLICATION_JSON)
      .withBody(gson.toJson(mapOf("access_token" to "ABCDE", "token_type" to "bearer")))
    oauthMock.`when`(request().withPath("/auth/oauth/token").withBody("grant_type=client_credentials")).respond(response)
  }

  fun getNumberOfMessagesCurrentlyOnQueue(): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(queue, listOf("ApproximateNumberOfMessages"))
    return queueAttributes.attributes["ApproximateNumberOfMessages"]?.toInt()
  }

  fun getNumberOfMessagesCurrentlyOnDLQ(): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(dlq, listOf("ApproximateNumberOfMessages"))
    return queueAttributes.attributes["ApproximateNumberOfMessages"]?.toInt()
  }
}
