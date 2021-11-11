package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import java.time.Duration
import java.time.temporal.ChronoUnit


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
      .retrieve().toBodilessEntity().retryWhen(Retry
        .fixedDelay(3, Duration.of(2, ChronoUnit.SECONDS))
        .filter(this::is5xxServerError)).block()
  }

  private fun is5xxServerError(throwable: Throwable) : Boolean{
    return throwable is WebClientResponseException &&
      throwable.statusCode.is5xxServerError
  }

  companion object {
    private val log = LoggerFactory.getLogger(CommunityApiClient::class.java)
  }
}
