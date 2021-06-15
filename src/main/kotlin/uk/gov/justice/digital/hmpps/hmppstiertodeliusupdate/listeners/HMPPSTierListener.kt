package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.CommunityApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.HmppsTierApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryService
import java.util.UUID

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

  @SqsListener(value = ["\${sqs.queue}"], deletionPolicy = ON_SUCCESS)
  fun onRegisterChange(message: String) {
    val sqsMessage: SQSMessage = gson.fromJson(message, SQSMessage::class.java)
    val changeEvent: TierChangeEvent = gson.fromJson(sqsMessage.message, TierChangeEvent::class.java)
    updateTier(crn = changeEvent.crn, calculationId = changeEvent.calculationId)
  }

  private fun updateTier(crn: String, calculationId: UUID) {
    try {
      hmppsTierApiClient.getTierByCrnAndCalculationId(crn, calculationId).let {
        communityApiClient.updateTier(it, crn).also {
          telemetryService.successfulWrite(crn, calculationId)
        }
      }
    } catch (e: WebClientResponseException.NotFound) {
      if (e.responseBodyAsString.contains("Offender with CRN $crn not found")) {
        telemetryService.offenderNotFoundFailedWrite(crn, calculationId)
      } else {
        telemetryService.failedWrite(crn, calculationId)
        throw e
      }
    } catch (e: Exception) {
      telemetryService.failedWrite(crn, calculationId)
      throw e
    }
  }

  private data class TierChangeEvent(
    val crn: String,
    val calculationId: UUID
  )

  private data class SQSMessage(@SerializedName("Message") val message: String)
}
