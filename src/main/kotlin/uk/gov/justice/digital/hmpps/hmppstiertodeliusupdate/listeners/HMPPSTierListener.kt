package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.CommunityApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.HmppsTierApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.InvalidMessageException
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryService
import java.util.*

@Service
class HMPPSTierListener(
  private val communityApiClient: CommunityApiClient,
  private val hmppsTierApiClient: HmppsTierApiClient,
  private val telemetryService: TelemetryService,
  private val gson: Gson
) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @JmsListener(destination = "\${sqs.queue.name}")
  fun onRegisterChange(message: String) {
    val sqsMessage: SQSMessage = gson.fromJson(message, SQSMessage::class.java)
    log.info("Received message ${sqsMessage.MessageId}")
    val changeEvent: TierChangeEvent = gson.fromJson(sqsMessage.Message, TierChangeEvent::class.java)
    when (changeEvent.eventType) {
      EventType.HMPPS_TIER_CALCULATION_COMPLETE -> updateTier(crn = changeEvent.crn, calculationId = changeEvent.calculationId)
      else -> {
        telemetryService.invalidMessage(sqsMessage.MessageId)
        throw InvalidMessageException("Received a message I wasn't expecting $changeEvent")
      }
    }
  }

  private fun updateTier(crn: String, calculationId: UUID) {
    try {
        hmppsTierApiClient.getTierByCrnAndCalculationId(crn, calculationId).let {
          communityApiClient.updateTier(it, crn)
        }.also {
          telemetryService.successfulWrite(crn, calculationId)
        }
    } catch (e: Exception) {
      telemetryService.failedWrite(crn, calculationId)
      throw e
    }
  }

  data class TierChangeEvent(
    val eventType: EventType?,
    val crn: String,
    val calculationId: UUID
  )

  data class SQSMessage(val Message: String, val MessageId: String)

  enum class EventType {
    HMPPS_TIER_CALCULATION_COMPLETE,
  }
}
