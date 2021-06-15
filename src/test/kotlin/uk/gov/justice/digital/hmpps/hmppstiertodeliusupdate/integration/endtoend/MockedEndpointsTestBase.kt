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
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest()
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
internal abstract class MockedEndpointsTestBase {
  @Qualifier("awsSqsClient")
  @Autowired
  internal lateinit var awsSqsClient: AmazonSQS

  @Value("\${sqs.queue}")
  lateinit var queue: String

  var hmppsTier: ClientAndServer = startClientAndServer(8091)
  var communityApi: ClientAndServer = startClientAndServer(8092)

  private var oauthMock: ClientAndServer = startClientAndServer(9090)

  private val gson: Gson = Gson()

  @BeforeEach
  fun before() {
    awsSqsClient.purgeQueue(PurgeQueueRequest(queue))
    setupOauth()
  }

  @AfterEach
  fun reset() {
    hmppsTier.reset()
    communityApi.reset()
    oauthMock.reset()
    awsSqsClient.purgeQueue(PurgeQueueRequest(queue))
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

  fun getNumberOfMessagesCurrentlyNotVisibleOnQueue(): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(queue, listOf("ApproximateNumberOfMessagesNotVisible"))
    return queueAttributes.attributes["ApproximateNumberOfMessagesNotVisible"]?.toInt()
  }
}
