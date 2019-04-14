package com.formation.springboot.integrationExample.controller

import com.formation.springboot.integrationExample.model.Student
import com.formation.springboot.integrationExample.service.StudentService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StudentControllerTest {
    @Autowired
    lateinit var studentController: StudentController

    @MockBean
    lateinit var studentService: StudentService

    lateinit var hardcodedStudent: Student

    @BeforeEach
    fun setup() {
        hardcodedStudent = Student(name = "Tom", nationality = "American")
    }


    @DisplayName("Save student when calling StudentService should return the right one and return 201, if failed 400")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testSaveStudent(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(hardcodedStudent).`when`(studentService).createStudent(hardcodedStudent)
                val result = studentController.createStudent(hardcodedStudent)
                Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
                Assertions.assertEquals(201, result.statusCodeValue)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(studentService).createStudent(hardcodedStudent)
                val result = studentController.createStudent(hardcodedStudent)
                Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
                Assertions.assertEquals(400, result.statusCodeValue)
            }
        }
    }

    @DisplayName("Update student when calling StudentService should return the right one and return 201, if failed 400")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testUpdateStudent(case: String) {
        val updatedStudent = Student(hardcodedStudent.id, name = "Tommy", nationality = "British")
        when (case) {
            "OK" -> {
                Mockito.doReturn(updatedStudent).`when`(studentService).updateStudent(updatedStudent)
                val result = studentController.updateStudent(updatedStudent.id, updatedStudent)
                Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
                Assertions.assertEquals(201, result.statusCodeValue)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(studentService).updateStudent(updatedStudent)
                val result = studentController.updateStudent(updatedStudent.id, updatedStudent)
                Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
                Assertions.assertEquals(400, result.statusCodeValue)            }
        }
    }

    @DisplayName("Retrieve a student when calling StudentService should return the right one, null if exception")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null", "exception")
    fun testRetrieveStudent(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(hardcodedStudent).`when`(studentService).findStudentById(hardcodedStudent.id)
                val result = studentController.findStudentById(hardcodedStudent.id)
                Assertions.assertEquals(hardcodedStudent.id, result!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "null" -> {
                Mockito.doReturn(null).`when`(studentService).findStudentById(43265)
                Assertions.assertNull(studentController.findStudentById(43265))
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(studentService).findStudentById(100)
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentController.findStudentById(100)}
                Assertions.assertTrue(exception.message!!.contains("Failed to find a student by his id when calling StudentService"))
            }
        }
    }

    @DisplayName("Retrieve a student when calling StudentService should return the right one or null if no student is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null", "exception")
    fun testRetrieveStudentByName(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(hardcodedStudent).`when`(studentService).findStudentByName("Tom")
                val result = studentController.findStudentByName("Tom")
                Assertions.assertEquals(hardcodedStudent.id, result!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "null" -> {
                Mockito.doReturn(null).`when`(studentService).findStudentByName("exception")
                Assertions.assertNull(studentController.findStudentByName("exception"))
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(studentService).findStudentByName("exception")
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentController.findStudentByName("exception")}
                Assertions.assertTrue(exception.message!!.contains("Failed to find a student by his name when calling StudentService"))
            }
        }
    }

    @DisplayName("Retrieve all student when calling StudentService should return a list of all students or an empty list if none is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "noData", "exception")
    fun testRetrieveAllStudents(case: String) {
        when (case) {
            "OK" -> {
                val newStudent = Student(id = 20, name = "Tommy", nationality = "British")
                Mockito.doReturn(listOf(hardcodedStudent, newStudent)).`when`(studentService).findAllStudents()
                val result = studentController.findAllStudents()
                Assertions.assertEquals(hardcodedStudent.id, result[0]!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result[0]!!.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result[0]!!.nationality)

                Assertions.assertEquals(newStudent.id, result[1]!!.id)
                Assertions.assertEquals(newStudent.name, result[1]!!.name)
                Assertions.assertEquals(newStudent.nationality, result[1]!!.nationality)
            }
            "noData" -> {
                Mockito.doReturn(listOf<Student>()).`when`(studentService).findAllStudents()
                val result = studentController.findAllStudents()
                Assertions.assertEquals(0, result.size)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(studentService).findAllStudents()
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentController.findAllStudents()}
                Assertions.assertTrue(exception.message!!.contains("Failed to find all students when calling StudentService"))
            }
        }
    }

    @DisplayName("Delete a student by his id when calling StudentService should erase him from DB and return 202, if failed 400")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "KO")
    fun testDeleteStudentById(case: String) {
        when (case) {
            "OK" ->  {
                Mockito.doReturn(true).`when`(studentService).deleteStudentById(hardcodedStudent.id)
                val result = studentController.deleteStudentById(hardcodedStudent.id)
                Assertions.assertEquals(HttpStatus.ACCEPTED, result.statusCode)
                Assertions.assertEquals(202, result.statusCodeValue)
            }
            "exception" -> {
                Mockito.doReturn(false).`when`(studentService).deleteStudentById(hardcodedStudent.id)
                val result = studentController.deleteStudentById(hardcodedStudent.id)
                Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
                Assertions.assertEquals(400, result.statusCodeValue)
            }
        }
    }

    @DisplayName("Delete all students when calling StudentService should erase all from DB and return 202, if failed 400")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "KO")
    fun testDeleteAllStudents(case: String) {
        when (case) {
            "OK" ->  {
                Mockito.doReturn(true).`when`(studentService).deleteAllStudents()
                val result = studentController.deleteAllStudents()
                Assertions.assertEquals(HttpStatus.ACCEPTED, result.statusCode)
                Assertions.assertEquals(202, result.statusCodeValue)
            }
            "exception" -> {
                Mockito.doReturn(false).`when`(studentService).deleteAllStudents()
                val result = studentController.deleteAllStudents()
                Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
                Assertions.assertEquals(400, result.statusCodeValue)
            }
        }
    }
}