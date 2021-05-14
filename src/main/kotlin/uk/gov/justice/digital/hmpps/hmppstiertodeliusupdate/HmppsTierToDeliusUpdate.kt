package uk.gov.justice.digital.hmpps.hmppstiertodeliusupdate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication()
@EnableRetry
class HmppsTierToDeliusUpdate

fun main(args: Array<String>) {
  runApplication<HmppsTierToDeliusUpdate>(*args)
}
