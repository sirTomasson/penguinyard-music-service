package xyz.tomassen.penguinyard.penguinyardmusicservice.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User @JsonCreator constructor(@JsonProperty("id") val id: Int,
                                         @JsonProperty("login") val login: String)
