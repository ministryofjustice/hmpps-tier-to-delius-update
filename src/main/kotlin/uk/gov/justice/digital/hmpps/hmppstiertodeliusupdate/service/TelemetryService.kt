package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.model.TierUpdate

@Component
class TelemetryService(@Autowired private val telemetryClient: TelemetryClient) {

  private fun trackEvent(eventType: TelemetryEventType, customDimensions: Map<String, String?>) {
    telemetryClient.trackEvent(eventType.eventName, customDimensions, null)
  }

  fun successfulWrite(tierUpdate: TierUpdate) {
    trackEvent(
      TelemetryEventType.TIER_UPDATE_CALL_SUCCESS,
      mapOf(
        "crn" to tierUpdate.crn
      )
    )
  }

  fun failedWrite(tierUpdate: TierUpdate) {
    trackEvent(
      TelemetryEventType.TIER_UPDATE_CALL_FAILED,
      mapOf(
        "crn" to tierUpdate.crn
      )
    )
  }
}
