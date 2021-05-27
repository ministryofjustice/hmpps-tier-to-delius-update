package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.integration.endtoend

import org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS
import org.awaitility.kotlin.await
import org.awaitility.kotlin.ignoreException
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.awaitility.kotlin.withPollInterval
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.notFoundResponse
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType.APPLICATION_JSON
import org.mockserver.model.RequestDefinition
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage

class TierTest : MockedEndpointsTestBase() {

  @Test
  fun `will consume a TIER_CALCULATION_COMPLETE message, retrieve calculation and send update to community-api`() {
    setupTierCalculationResponse()

    val tierWriteback = request().withPath("/offenders/crn/12345/tier/B3").withMethod("POST")
    setupTierWriteback(tierWriteback)
    awsSqsClient.sendMessage(queue, tierUpdateMessage())

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue() } matches { it == 0 }
    (await withPollInterval ONE_HUNDRED_MILLISECONDS).ignoreException(IllegalArgumentException::class)
      .untilAsserted { communityApi.verify(tierWriteback) }
  }

  @Test
  fun `leaves message on queue if tier calculation cannot be found`() {
    var notFoundRequest = setupNotFoundTierCalculationResponse()
    awsSqsClient.sendMessage(queue, tierUpdateMessage())
    Thread.sleep(1000L)
    hmppsTier.verify(notFoundRequest)
  }

  private fun setupTierCalculationResponse() {
    val request = request().withPath("/crn/12345/tier/e45559d1-3460-4a0e-8281-c736de57c562")
    hmppsTier.`when`(request).respond(
      response().withContentType(APPLICATION_JSON).withBody("{\"tierScore\":\"B3\"}")
    )
  }

  private fun setupNotFoundTierCalculationResponse(): RequestDefinition {
    val request = request().withPath("/crn/12345/tier/e45559d1-3460-4a0e-8281-c736de57c562")

    hmppsTier.`when`(request).respond(
      notFoundResponse()
    )
    return request
  }

  private fun setupTierWriteback(tierWriteback: HttpRequest) {
    communityApi.`when`(tierWriteback).respond(
      response().withStatusCode(200)
    )
  }
}
