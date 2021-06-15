package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service

enum class TelemetryEventType(val eventName: String) {
  TIER_UPDATE_CALL_SUCCESS("TierUpdateCallSuccess"),
  TIER_UPDATE_CALL_FAILED("TierUpdateCallFailed"),
  TIER_UPDATE_CALL_FAILED_OFFENDER_NOT_FOUND("TierUpdateCallFailedOffenderNotFound");
}
