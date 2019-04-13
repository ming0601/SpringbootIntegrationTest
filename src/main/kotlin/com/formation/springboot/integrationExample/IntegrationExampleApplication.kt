package com.formation.springboot.integrationExample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IntegrationExampleApplication

fun main(args: Array<String>) {
	runApplication<IntegrationExampleApplication>(*args)
}
