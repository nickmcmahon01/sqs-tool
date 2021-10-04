package uk.gov.digital.ho.hocs.sqstool

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SqsToolApplication

fun main(args: Array<String>) {
	runApplication<SqsToolApplication>(*args)
}
