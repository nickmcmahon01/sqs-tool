package uk.gov.digital.ho.hocs.sqstool.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.digital.ho.hocs.sqstool.service.QueueAdminService
import uk.gov.digital.ho.hocs.sqstool.domain.enum.QueuePairName

@RestController
class QueueAdminController(private val queueAdminService: QueueAdminService) {

  @GetMapping("/transfer")
  fun transferMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair : QueuePairName) {
    queueAdminService.transferMessages(pair)
  }

  @GetMapping("/purgedlq")
  fun purgeMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair : QueuePairName) {
    queueAdminService.purgeMessages(pair)
  }

}
