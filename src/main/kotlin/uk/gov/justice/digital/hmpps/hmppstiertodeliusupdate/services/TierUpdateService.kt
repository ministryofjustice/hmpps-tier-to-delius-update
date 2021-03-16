package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate.model.TierUpdate

@Service
class TierUpdateService {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun updateTier(tier: TierUpdate) {
    log.info("About to update court $tier")
  }
}
