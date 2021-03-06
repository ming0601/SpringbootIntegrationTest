package com.formation.springboot.integrationExample.dao

import com.formation.springboot.integrationExample.model.Student
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@DataJpaTest
class IStudentDaoTest {
    @Autowired
    lateinit var dao: IStudentDao
    lateinit var hardcodedStudent: Student

    @BeforeEach
    fun setup() {
        hardcodedStudent = Student(name = "Tom", nationality = "American")
        dao.save(hardcodedStudent)
    }
    @AfterEach
    fun flush() {
        dao.deleteAll()
    }

    @DisplayName("Save student in DB should return the right one")
    @Test
    fun testSaveStudent() {
        val result = dao.save(hardcodedStudent)
        Assertions.assertEquals(hardcodedStudent.id, result.id)
        Assertions.assertEquals(hardcodedStudent.name, result.name)
        Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
    }

    @DisplayName("Update student in DB should return the right one")
    @Test
    fun testUpdateStudent() {
        val updatedStudent = Student(hardcodedStudent.id, name = "Tommy", nationality = "British")
        val result = dao.save(updatedStudent)
        Assertions.assertEquals(updatedStudent.id, result.id)
        Assertions.assertEquals(updatedStudent.name, result.name)
        Assertions.assertEquals(updatedStudent.nationality, result.nationality)
    }

    @DisplayName("Retrieve a student in DB should return the right one or throws an exception when getting the result")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "Exception")
    fun testRetrieveStudentById(case: String) {
        when (case) {
            "OK" -> {
                val result = dao.findById(hardcodedStudent.id)
                Assertions.assertEquals(hardcodedStudent.id, result.get().id)
                Assertions.assertEquals(hardcodedStudent.name, result.get().name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.get().nationality)
            }
            "Exception" -> {
                val exception = Assertions.assertThrows(NoSuchElementException::class.java) {dao.findById(100).get()}
                Assertions.assertTrue(exception.message!!.contains("No value present"))
            }
        }
    }

    @DisplayName("Retrieve a student in DB should return the right one or null if no student is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null")
    fun testRetrieveStudentByName(case: String) {
        when (case) {
            "OK" -> {
                val result = dao.findByName("Tom")
                Assertions.assertEquals(hardcodedStudent.id, result!!.id)
                Assertions.assertEquals(hardcodedStudent.name, result.name)
                Assertions.assertEquals(hardcodedStudent.nationality, result.nationality)
            }
            "null" -> Assertions.assertEquals(null, dao.findByName("unknown"))
        }
    }

    @DisplayName("Retrieve all student in DB should return a list of all students or an empty list if none is found")
    @ParameterizedTest(name = "Case {index}: Testing {0}")
    @CsvSource("OK", "null")
    fun testRetrieveAllStudents(case: String) {
        when (case) {
            "OK" -> {
                val newStudent = Student(id = 0, name = "Tommy", nationality = "British")
                dao.save(newStudent)
                val result = dao.findAll()
                Assertions.assertEquals(hardcodedStudent.id, result[0].id)
                Assertions.assertEquals(hardcodedStudent.name, result[0].name)
                Assertions.assertEquals(hardcodedStudent.nationality, result[0].nationality)

                Assertions.assertEquals(newStudent.id, result[1].id)
                Assertions.assertEquals(newStudent.name, result[1].name)
                Assertions.assertEquals(newStudent.nationality, result[1].nationality)
            }
            "null" -> {
                dao.deleteAll()
                val result = dao.findAll()
                Assertions.assertEquals(0, result.size)
            }
        }
    }

    @DisplayName("Delete a student by his id should erase him from DB")
    @Test
    fun testDeleteStudentById() {
        dao.deleteById(hardcodedStudent.id)
        val result = dao.findById(hardcodedStudent.id)
        val exception = Assertions.assertThrows(NoSuchElementException::class.java) {result.get()}
        Assertions.assertTrue(exception.message!!.contains("No value present"))
    }

    @DisplayName("Delete all students should erase all from DB")
    @Test
    fun testDeleteAllStudents() {
        val newStudent = Student(name = "Tommy", nationality = "British")
        dao.save(newStudent)
        dao.deleteAll()
        val result = dao.findAll()
        Assertions.assertEquals(0, result.size)
    }
}