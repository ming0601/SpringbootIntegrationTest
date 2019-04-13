package com.formation.springboot.integrationExample.service

import com.formation.springboot.integrationExample.dao.IStudentDao
import com.formation.springboot.integrationExample.model.Student
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
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StudentServiceTest {
    @MockBean
    lateinit var dao: IStudentDao

    @Autowired
    lateinit var studentService: StudentService

    lateinit var hardcodedStudent: Student

    @BeforeEach
    fun setup() {
        hardcodedStudent = Student(name = "Tom", nationality = "American")
    }

    @DisplayName("Save student when calling DAO should return the right one")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testSaveStudent(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(hardcodedStudent).`when`(dao).save(hardcodedStudent)
                val result = studentService.saveStudent(hardcodedStudent)
                Assertions.assertEquals(hardcodedStudent.id, result.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).save(hardcodedStudent)
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentService.saveStudent(hardcodedStudent)}
                Assertions.assertTrue(exception.message!!.contains("Failed to save student when calling DAO"))
            }
        }
    }

    @DisplayName("Update student when calling DAO should return the right one")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testUpdateStudent(case: String) {
        val updatedStudent = Student(hardcodedStudent.id, name = "Tommy", nationality = "British")
        when (case) {
            "OK" -> {
                Mockito.doReturn(updatedStudent).`when`(dao).save(updatedStudent)
                val result = studentService.updateStudent(updatedStudent)
                Assertions.assertEquals(updatedStudent.id, result.id)
                Assertions.assertEquals(updatedStudent.name, result.name)
                Assertions.assertEquals(updatedStudent.nationality, result.nationality)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).save(updatedStudent)
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentService.updateStudent(updatedStudent)}
                Assertions.assertTrue(exception.message!!.contains("Failed to update student when calling DAO"))
            }
        }
    }

    @DisplayName("Retrieve a student when calling DAO should return the right one, null if exception")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null", "exception")
    fun testRetrieveStudent(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(Optional.of(hardcodedStudent)).`when`(dao).findById(hardcodedStudent.id)
                val result = studentService.findStudentById(hardcodedStudent.id)
                Assertions.assertEquals(hardcodedStudent.id, result!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "null" -> {
                Mockito.doThrow(NoSuchElementException("No value present")).`when`(dao).findById(100)
                val result = studentService.findStudentById(100)
                Assertions.assertEquals(null, result)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).findById(100)
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentService.findStudentById(100)}
                Assertions.assertTrue(exception.message!!.contains("Failed to find a student by his id when calling DAO"))
            }
        }
    }

    @DisplayName("Retrieve a student when calling DAO should return the right one or null if no student is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null", "exception")
    fun testRetrieveStudentByName(case: String) {
        when (case) {
            "OK" -> {
                Mockito.doReturn(hardcodedStudent).`when`(dao).findByName("Tom")
                val result = studentService.findStudentByName("Tom")
                Assertions.assertEquals(hardcodedStudent.id, result!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "null" -> {
                Mockito.doReturn(null).`when`(dao).findByName("unknown")
                Assertions.assertEquals(null, studentService.findStudentByName("unknown"))
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).findByName("exception")
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentService.findStudentByName("exception")}
                Assertions.assertTrue(exception.message!!.contains("Failed to find a student by his name when calling DAO"))
            }
        }
    }

    @DisplayName("Retrieve all student when calling DAO should return a list of all students or an empty list if none is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "noData", "exception")
    fun testRetrieveAllStudents(case: String) {
        when (case) {
            "OK" -> {
                val newStudent = Student(id = 20, name = "Tommy", nationality = "British")
                Mockito.doReturn(listOf(hardcodedStudent, newStudent)).`when`(dao).findAll()
                val result = studentService.findAllStudents()
                Assertions.assertEquals(hardcodedStudent.id, result[0].id)
                Assertions.assertEquals(hardcodedStudent.name, result[0].name)
                Assertions.assertEquals(hardcodedStudent.nationality, result[0].nationality)

                Assertions.assertEquals(newStudent.id, result[1].id)
                Assertions.assertEquals(newStudent.name, result[1].name)
                Assertions.assertEquals(newStudent.nationality, result[1].nationality)
            }
            "noData" -> {
                Mockito.doReturn(listOf<Student>()).`when`(dao).findAll()
                val result = studentService.findAllStudents()
                Assertions.assertEquals(0, result.size)
            }
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).findAll()
                val exception = Assertions.assertThrows(RuntimeException::class.java) {studentService.findAllStudents()}
                Assertions.assertTrue(exception.message!!.contains("Failed to find all students when calling DAO"))
            }
        }
    }

    @DisplayName("Delete a student by his id when calling DAO should erase him from DB")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testDeleteStudentById(case: String) {
        when (case) {
            "OK" ->  Assertions.assertTrue(studentService.deleteStudentById(hardcodedStudent.id))
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).deleteById(100)
                Assertions.assertFalse(studentService.deleteStudentById(100))
            }
        }
    }

    @DisplayName("Delete all students when calling DAO should erase all from DB")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "exception")
    fun testDeleteAllStudents(case: String) {
        when (case) {
            "OK" ->  Assertions.assertTrue(studentService.deleteAllStudents())
            "exception" -> {
                Mockito.doThrow(RuntimeException::class.java).`when`(dao).deleteAll()
                Assertions.assertFalse(studentService.deleteAllStudents())
            }
        }
    }
}