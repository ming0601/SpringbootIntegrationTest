package com.formation.springboot.integrationExample.controller

import com.formation.springboot.integrationExample.model.Student
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentRestTemplateIntegrationTest {
    @LocalServerPort
    private val port: Int = 0

    val testRestTemplate = TestRestTemplate()
    val headers = HttpHeaders()

    @Test
    @Order(1)
    @Throws(Exception::class)
    fun testCreateStudent() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity(Student(id = 1, name = "Tom", nationality = "American"), headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students"), HttpMethod.POST, entity, String::class)
        val actualURL = response.headers[HttpHeaders.LOCATION]!![0]
        Assertions.assertTrue(actualURL.contains("/formation/integration/students"))
    }

    @Test
    @Order(2)
    @Throws(Exception::class)
    fun testUpdateStudent() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity(Student(id = 1, name = "Tommy", nationality = "British"), headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/student/update/1"), HttpMethod.PUT, entity, String::class)
        val actualURL = response.headers[HttpHeaders.LOCATION]!![0]
        Assertions.assertTrue(actualURL.contains("/formation/integration/student/update/1"))
    }

    @Test
    @Order(3)
    @Throws(Exception::class)
    fun testRetrieveStudentById() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity<Student>(null, headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students/10001"), HttpMethod.GET, entity, String::class)
        val expectedResponse = "{\"id\":10001,\"name\":\"Sebastien\",\"nationality\":\"French\"}"
        JSONAssert.assertEquals(expectedResponse, response.body, false)
    }

    @Test
    @Order(4)
    @Throws(Exception::class)
    fun testRetrieveStudentByName() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        headers.add("name", "Ming")
        val entity = HttpEntity<Student>(null, headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students/name"), HttpMethod.GET, entity, String::class)
        val expectedResponse = "{\"id\":10003,\"name\":\"Ming\",\"nationality\":\"Chinese\"}"
        JSONAssert.assertEquals(expectedResponse, response.body, false)
    }

    @Test
    @Order(5)
    @Throws(Exception::class)
    fun testRetrieveAllStudents() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity<Student>(null, headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students"), HttpMethod.GET, entity, String::class)
        val expectedResponse = "[{\"id\":1,\"name\":\"Tom\",\"nationality\":\"American\"},{\"id\":10001,\"name\":\"Sebastien\",\"nationality\":\"French\"},{\"id\":10002,\"name\":\"Jean\",\"nationality\":\"French\"},{\"id\":10003,\"name\":\"Ming\",\"nationality\":\"Chinese\"}]"
        JSONAssert.assertEquals(expectedResponse, response.body, false)
    }

    @Test
    @Order(6)
    @Throws(Exception::class)
    fun testDeleteStudentById() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity<Student>(null, headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students/10001"), HttpMethod.DELETE, entity, String::class)
        Assertions.assertEquals(202, response.statusCodeValue)
    }

    @Test
    @Order(7)
    @Throws(Exception::class)
    fun testDeleteAllStudent() {
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val entity = HttpEntity<Student>(null, headers)
        val response: ResponseEntity<String> = testRestTemplate.exchange(createURLWithPort("/formation/integration/students"), HttpMethod.DELETE, entity, String::class)
        Assertions.assertEquals(202, response.statusCodeValue)
    }

    private fun createURLWithPort(uri: String): String {
        return "http://localhost:$port$uri"
    }
}