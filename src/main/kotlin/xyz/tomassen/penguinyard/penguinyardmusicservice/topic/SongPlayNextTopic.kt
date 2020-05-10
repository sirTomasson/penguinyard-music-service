package xyz.tomassen.penguinyard.penguinyardmusicservice.topic

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SongPlayNextTopic @JsonCreator constructor(@JsonProperty("playNext") val playNext: Boolean)