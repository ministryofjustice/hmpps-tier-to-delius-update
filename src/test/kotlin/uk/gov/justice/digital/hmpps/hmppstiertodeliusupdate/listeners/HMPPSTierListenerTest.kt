package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.CommunityApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.client.HmppsTierApiClient
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.courtRegisterInsertMessage
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.service.TelemetryService

internal class HMPPSTierListenerTest {
  private val gson: Gson = Gson()

  @Test
  internal fun `will call service for a Tier update`() {
    val communityApiClient: CommunityApiClient = mock()
    val telemetryService: TelemetryService = mock()
    val hmppsTierApiClient: HmppsTierApiClient = mock {
      on { getTierByCrn("12345") } doReturn "A0"
    }

    val listener = HMPPSTierListener(communityApiClient = communityApiClient, hmppsTierApiClient = hmppsTierApiClient, telemetryService = telemetryService, gson = gson)

    listener.onRegisterChange(tierUpdateMessage())

    verify(hmppsTierApiClient).getTierByCrn(("12345"))
    verify(communityApiClient).updateTier("A0", "12345")
    verifyNoMoreInteractions(communityApiClient)
    verifyNoMoreInteractions(hmppsTierApiClient)
  }

  @Test
  internal fun `will not call service for events we don't understand`() {
    val communityApiClient: CommunityApiClient = mock()
    val telemetryService: TelemetryService = mock()
    val hmppsTierApiClient: HmppsTierApiClient = mock {
      on { getTierByCrn("12345") } doReturn "A0"
    }

    val listener = HMPPSTierListener(communityApiClient = communityApiClient, hmppsTierApiClient = hmppsTierApiClient, telemetryService = telemetryService, gson = gson)

    listener.onRegisterChange(courtRegisterInsertMessage())

    verifyNoMoreInteractions(communityApiClient)
    verifyNoMoreInteractions(hmppsTierApiClient)
  }
}
