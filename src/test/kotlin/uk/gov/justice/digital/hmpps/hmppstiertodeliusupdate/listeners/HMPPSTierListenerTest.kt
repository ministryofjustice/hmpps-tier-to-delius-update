package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.listeners

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.courtRegisterInsertMessage
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.helpers.tierUpdateMessage
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.model.TierUpdate
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.services.TierUpdateService

internal class HMPPSTierListenerTest {
  private val tierUpdateService: TierUpdateService = mock()
  private val gson: Gson = Gson()
  private val listener: HMPPSTierListener =
    HMPPSTierListener(tierUpdateService = tierUpdateService, gson = gson)

  @Test
  internal fun `will call service for a Tier update`() {
    listener.onRegisterChange(tierUpdateMessage())

    verify(tierUpdateService).updateTier(TierUpdate("12345"))
  }

  @Test
  internal fun `will not call service for events we don't understand`() {
    listener.onRegisterChange(courtRegisterInsertMessage())

    verifyNoMoreInteractions(tierUpdateService)
  }
}
