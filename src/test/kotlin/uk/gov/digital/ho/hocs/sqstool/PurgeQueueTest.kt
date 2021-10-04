package uk.gov.digital.ho.hocs.sqstool

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.digital.ho.hocs.sqstool.domain.QueuePair
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("localstack")
class PurgeQueueTest {

  @Autowired
  internal lateinit var searchAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var searchAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var auditAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var auditAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var documentAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var documentAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var notifyAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var notifyAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Value("\${search-queue.sqs-queue}")
  lateinit var searchQueueUrl: String

  @Value("\${search-dlq.sqs-queue}")
  lateinit var searchDlqUrl: String

  @Value("\${audit-queue.sqs-queue}")
  lateinit var auditQueueUrl: String

  @Value("\${audit-dlq.sqs-queue}")
  lateinit var auditDlqUrl: String

  @Value("\${document-queue.sqs-queue}")
  lateinit var documentQueueUrl: String

  @Value("\${document-dlq.sqs-queue}")
  lateinit var documentDlqUrl: String

  @Value("\${notify-queue.sqs-queue}")
  lateinit var notifyQueueUrl: String

  @Value("\${notify-dlq.sqs-queue}")
  lateinit var notifyDlqUrl: String

  @BeforeEach
  fun `purge Queues`() {
    searchAwsSqsClient.purgeQueue(PurgeQueueRequest(searchQueueUrl))
    searchAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(searchDlqUrl))
    auditAwsSqsClient.purgeQueue(PurgeQueueRequest(auditQueueUrl))
    auditAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(auditDlqUrl))
    documentAwsSqsClient.purgeQueue(PurgeQueueRequest(documentQueueUrl))
    documentAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(documentDlqUrl))
    notifyAwsSqsClient.purgeQueue(PurgeQueueRequest(notifyQueueUrl))
    notifyAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(notifyDlqUrl))
  }


  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `purge 1 message from DLQ`(queuePair : QueuePair, queuePairName : String) {
    with (queuePair) {
      putMessageOnDlq(dlqClient, dlqEndpoint,1)
      webTestClient.get().uri("/purgedlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqEndpoint) } matches { it == 0 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `purge 2 messages from DLQ to main queue`(queuePair : QueuePair, queuePairName : String) {
    with (queuePair) {
      putMessageOnDlq(dlqClient, dlqEndpoint,2)
      webTestClient.get().uri("/purgedlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqEndpoint) } matches { it == 0 }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["","?","?queue=","?queue=search","?queue=SAUSAGES"])
  fun `throws an exception if no valid queue pair is referenced`(queryString : String){
      webTestClient.get().uri("/purgedlq$queryString")
        .exchange()
        .expectStatus()
        .is4xxClientError
  }

  fun getQueuePairs() : Stream<Arguments> {
    return Stream.of(
      Arguments.of(QueuePair(searchAwsSqsClient, searchQueueUrl, searchAwsSqsDlqClient, searchDlqUrl), "SEARCH"),
      Arguments.of(QueuePair(auditAwsSqsClient, auditQueueUrl, auditAwsSqsDlqClient, auditDlqUrl), "AUDIT"),
      Arguments.of(QueuePair(documentAwsSqsClient, documentQueueUrl, documentAwsSqsDlqClient, documentDlqUrl),"DOCUMENT"),
      Arguments.of(QueuePair(notifyAwsSqsClient, notifyQueueUrl, notifyAwsSqsDlqClient, notifyDlqUrl), "NOTIFY")
    )
  }
}
