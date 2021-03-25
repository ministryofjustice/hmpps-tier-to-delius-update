package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.CommunityApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.HmppsTierApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryService
import java.util.UUID

internal class HMPPSTierListenerTest {
  private val gson: Gson = Gson()

  @Test
  internal fun `will call service for a Tier update`() {
    val calculationId = UUID.fromString("e45559d1-3460-4a0e-8281-c736de57c562")
    val communityApiClient: CommunityApiClient = mock()
    val telemetryService: TelemetryService = mock()
    val hmppsTierApiClient: HmppsTierApiClient = mock {
      on { getTierByCrnAndCalculationId("12345", calculationId) } doReturn "A0"
    }

    val listener = HMPPSTierListener(communityApiClient = communityApiClient, hmppsTierApiClient = hmppsTierApiClient, telemetryService = telemetryService, gson = gson, enableUpdates = true)

    listener.onRegisterChange(tierUpdateMessage())

    verify(hmppsTierApiClient).getTierByCrnAndCalculationId("12345", calculationId)
    verify(communityApiClient).updateTier("A0", "12345")
    verifyNoMoreInteractions(communityApiClient)
    verifyNoMoreInteractions(hmppsTierApiClient)
  }

  @Test
  internal fun `will not call service for a Tier update if write-back disabled`() {
    val calculationId = UUID.fromString("e45559d1-3460-4a0e-8281-c736de57c562")
    val communityApiClient: CommunityApiClient = mock()
    val telemetryService: TelemetryService = mock()
    val hmppsTierApiClient: HmppsTierApiClient = mock {
      on { getTierByCrnAndCalculationId("12345", calculationId) } doReturn "A0"
    }

    val listener = HMPPSTierListener(communityApiClient = communityApiClient, hmppsTierApiClient = hmppsTierApiClient, telemetryService = telemetryService, gson = gson, enableUpdates = false)

    listener.onRegisterChange(tierUpdateMessage())

    verify(hmppsTierApiClient).getTierByCrnAndCalculationId("12345", calculationId)

    verifyNoMoreInteractions(hmppsTierApiClient)
    verifyZeroInteractions(communityApiClient)
  }
}
