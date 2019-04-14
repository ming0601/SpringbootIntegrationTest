package com.formation.springboot.integrationExample.controller

import com.formation.springboot.integrationExample.model.Student
import com.formation.springboot.integrationExample.service.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.lang.RuntimeException

@RestController
@RequestMapping("/formation/integration", consumes = ["application/json"], produces = ["application/json"])
class StudentController @Autowired constructor(val studentService: StudentService) {

    @PostMapping("/students")
    fun createStudent(@RequestBody student: Student): ResponseEntity<Unit> {
        return try {
            val result = studentService.createStudent(student)
            val location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.id).toUri()
            ResponseEntity.created(location).build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/students/{id}")
    fun updateStudent(
            @PathVariable id: Long,
            @RequestBody student: Student): ResponseEntity<Unit> {
        return try {
            val result = studentService.updateStudent(student)
            val location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.id).toUri()
            ResponseEntity.created(location).build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/students/{id}")
    fun findStudentById(@PathVariable id: Long): Student? {
        return try {
            studentService.findStudentById(id)
        } catch (e: Exception){
            throw RuntimeException("Failed to find a student by his id when calling StudentService")
        }
    }

    @GetMapping("/students/name")
    fun findStudentByName(@RequestHeader name: String): Student? {
        return try {
            studentService.findStudentByName(name)
        } catch (e: Exception){
            throw RuntimeException("Failed to find a student by his name when calling StudentService")
        }
    }

    @GetMapping("/students")
    fun findAllStudents(): List<Student?> {
        return try {
            studentService.findAllStudents()
        } catch (e: Exception){
            throw RuntimeException("Failed to find all students when calling StudentService")
        }
    }

    @DeleteMapping("/students/{id}")
    fun deleteStudentById(@PathVariable id: Long): ResponseEntity<Unit> {
        val result = studentService.deleteStudentById(id)
        return if (result) ResponseEntity.accepted().build()
        else ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/students")
    fun deleteAllStudents(): ResponseEntity<Unit> {
        val result = studentService.deleteAllStudents()
        return if (result) ResponseEntity.accepted().build()
        else ResponseEntity.badRequest().build()
    }
}