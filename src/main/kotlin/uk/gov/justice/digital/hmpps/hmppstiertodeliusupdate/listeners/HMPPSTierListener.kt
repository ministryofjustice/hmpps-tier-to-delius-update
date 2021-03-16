package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.model.TierUpdate
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.services.TierUpdateService

@Service
class HMPPSTierListener(
  private val tierUpdateService: TierUpdateService,
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
      EventType.HMPPS_TIER_CALCULATION_COMPLETE -> tierUpdateService.updateTier(TierUpdate(crn = changeEvent.crn))
      else -> log.info("Received a message I wasn't expecting $changeEvent")
    }
  }

  data class TierChangeEvent(
    val eventType: EventType?,
    val crn: String
  )

  data class SQSMessage(val Message: String, val MessageId: String)

  enum class EventType {
    HMPPS_TIER_CALCULATION_COMPLETE,
  }
}
