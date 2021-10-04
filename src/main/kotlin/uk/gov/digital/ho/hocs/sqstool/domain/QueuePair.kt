package uk.gov.digital.ho.hocs.sqstool.domain

import com.amazonaws.services.sqs.AmazonSQSAsync

data class QueuePair(
  val mainClient : AmazonSQSAsync,
  val mainEndpoint : String,
  val dlqClient : AmazonSQSAsync,
  val dlqEndpoint : String
)

