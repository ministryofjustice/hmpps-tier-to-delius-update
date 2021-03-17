package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Component
class HmppsTierApiClient(@Qualifier("hmppsTierWebClientAppScope") private val webClient: WebClient) {

  fun getTierByCrn(crn: String, calculationId: UUID): String {
    return getTierByCrnCall(crn, calculationId)
      .also {
        log.info("Fetching Tier for $crn")
        log.debug("Body: $it for $crn")
      }
  }

  private fun getTierByCrnCall(crn: String, calculationId: UUID): String {
    return webClient
      .get()
      .uri("/offenders/crn/$crn/tier/$calculationId")
      .retrieve()
      .bodyToMono(TierDto::class.java)
      .block()?.tierScore ?: throw EntityNotFoundException("No Tier record found for $crn")
  }

  companion object {
    private val log = LoggerFactory.getLogger(HmppsTierApiClient::class.java)
  }
}

private data class TierDto @JsonCreator constructor(
  @JsonProperty("tierScore")
  val tierScore: String
)
