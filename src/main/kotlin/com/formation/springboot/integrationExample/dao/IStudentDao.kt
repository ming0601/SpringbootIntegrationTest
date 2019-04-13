package com.formation.springboot.integrationExample.dao

import com.formation.springboot.integrationExample.model.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface IStudentDao: JpaRepository<Student, Long> {
    fun findByName(name: String): Student?
}