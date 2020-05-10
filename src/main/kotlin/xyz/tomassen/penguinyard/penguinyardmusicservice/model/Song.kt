package xyz.tomassen.penguinyard.penguinyardmusicservice.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Song @JsonCreator constructor(@JsonProperty("id") val id: String,
                                         @JsonProperty("userId") val userId: Int)