package com.formation.springboot.integrationExample.service

import com.formation.springboot.integrationExample.dao.IStudentDao
import com.formation.springboot.integrationExample.model.Student
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.RuntimeException

@Service
class StudentService {
    @Autowired
    lateinit var dao: IStudentDao

    fun saveStudent(student: Student): Student {
        return try {
            dao.save(student)
        } catch (e: Exception) {
            throw RuntimeException("Failed to save student when calling DAO")
        }
    }

    fun updateStudent(student: Student): Student {
        return try {
            dao.save(student)
        } catch (e: Exception) {
            throw RuntimeException("Failed to update student when calling DAO")
        }
    }

    fun findStudentById(id: Long): Student? {
        return try {
            dao.findById(id).get()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException -> null
                else -> throw RuntimeException("Failed to find a student by his id when calling DAO")
            }
        }
    }

    fun findStudentByName(name: String): Student? {
        return try {
            dao.findByName(name)
        } catch (e: Exception) {
            throw RuntimeException("Failed to find a student by his name when calling DAO")
        }
    }

    fun findAllStudents(): List<Student> {
        return try {
            val result = dao.findAll()
            if (result.size > 0) result else listOf()
        } catch (e: Exception) {
            throw RuntimeException("Failed to find all students when calling DAO")
        }
    }

    fun deleteStudentById(id: Long): Boolean {
        return try {
            dao.deleteById(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteAllStudents(): Boolean {
        return try {
            dao.deleteAll()
            true
        } catch (e: Exception) {
            false
        }
    }
}