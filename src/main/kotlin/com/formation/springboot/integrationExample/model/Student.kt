package com.formation.springboot.integrationExample.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Student (
        @Id
        @GeneratedValue
        val id: Long = 0,
        val name: String,
        val nationality: String
)