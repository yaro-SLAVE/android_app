package com.example.normalapp

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Profile(val id: Int, val user: Int, val logo: String?, val birth_date: Date)