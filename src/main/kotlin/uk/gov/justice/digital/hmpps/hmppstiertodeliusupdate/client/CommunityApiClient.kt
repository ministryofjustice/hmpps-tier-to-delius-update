package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class CommunityApiClient(@Qualifier("communityWebClientAppScope") private val webClient: WebClient) {

  fun updateTier(tier: String, crn: String) {
    return updateTierCall(tier, crn)
      .also {
        log.info("Updated Tier for $crn")
        log.debug("Body: $tier for $crn")
      }
  }

  private fun updateTierCall(tier: String, crn: String) {
    webClient
      .post()
      .uri("/offenders/crn/$crn/tier/$tier")
      .retrieve()
      .toBodilessEntity()
      .block()
  }

  companion object {
    private val log = LoggerFactory.getLogger(CommunityApiClient::class.java)
  }
}
