package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service

enum class TelemetryEventType(val eventName: String) {
  TIER_UPDATE_CALL_SUCCESS("TierWriteSuccess"),
  TIER_UPDATE_CALL_FAILED("TierWriteFailed");
}
