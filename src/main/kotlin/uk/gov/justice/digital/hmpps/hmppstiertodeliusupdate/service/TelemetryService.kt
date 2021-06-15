package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryEventType.TIER_UPDATE_CALL_FAILED
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryEventType.TIER_UPDATE_CALL_FAILED_OFFENDER_NOT_FOUND
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryEventType.TIER_UPDATE_CALL_SUCCESS
import java.util.UUID

@Component
class TelemetryService(@Autowired private val telemetryClient: TelemetryClient) {

  private fun trackEvent(eventType: TelemetryEventType, customDimensions: Map<String, String?>) {
    telemetryClient.trackEvent(eventType.eventName, customDimensions, null)
  }

  fun successfulWrite(crn: String, calculationId: UUID) {
    trackEvent(
      TIER_UPDATE_CALL_SUCCESS,
      mapOf(
        "crn" to crn,
        "calculationId" to calculationId.toString()
      )
    )
  }

  fun failedWrite(crn: String, calculationId: UUID) {
    trackEvent(
      TIER_UPDATE_CALL_FAILED,
      mapOf(
        "crn" to crn,
        "calculationId" to calculationId.toString()
      )
    )
  }

  fun offenderNotFoundFailedWrite(crn: String, calculationId: UUID) {
    trackEvent(
      TIER_UPDATE_CALL_FAILED_OFFENDER_NOT_FOUND,
      mapOf(
        "crn" to crn,
        "calculationId" to calculationId.toString()
      )
    )
  }
}
