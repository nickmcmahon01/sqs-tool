package uk.gov.digital.ho.hocs.sqstool.service

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.digital.ho.hocs.sqstool.config.QueueHelper
import uk.gov.digital.ho.hocs.sqstool.domain.enum.QueuePairName

@Service
class QueueAdminService(
   private val queueHelper: QueueHelper
) {
    /* Transfer the messages one at a time */
    fun transferMessages(name: QueuePairName) {
        with (queueHelper.getQueuePair(name)) {
            repeat(getMessageCount(dlqClient, dlqEndpoint)) {
                val msg = dlqClient.receiveOneMessage(dlqEndpoint)
                mainClient.sendMessage(mainEndpoint, msg.body)
                dlqClient.deleteMessage(DeleteMessageRequest(dlqEndpoint, msg.receiptHandle))
            }.also { log.info("Transferred messages from $name dead letter queue to main queue") }
        }
    }

    /* Remove messages from the DLQ */
    fun purgeMessages(name: QueuePairName) {
        with (queueHelper.getQueuePair(name)) {
            dlqClient.purgeQueue(PurgeQueueRequest(dlqEndpoint)).also { log.info("Purged the dead letter queue") }
        }
    }

    private fun getMessageCount(amazonSQS: AmazonSQS, dlqUrl : String) : Int {
        val messageCount = amazonSQS.getQueueAttributes(dlqUrl, listOf("ApproximateNumberOfMessages")).attributes["ApproximateNumberOfMessages"]?.toInt() ?: 0
        log.info("Found $messageCount messages")
        return messageCount
    }

    private fun AmazonSQSAsync.receiveOneMessage(dlqEndpoint : String) = this.receiveMessage(ReceiveMessageRequest(dlqEndpoint).withMaxNumberOfMessages(1)).messages.first()

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}