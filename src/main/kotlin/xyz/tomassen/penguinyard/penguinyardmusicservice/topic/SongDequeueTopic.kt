package xyz.tomassen.penguinyard.penguinyardmusicservice.topic

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import xyz.tomassen.penguinyard.penguinyardmusicservice.model.Song

data class SongDequeueTopic @JsonCreator constructor(@JsonProperty("index") val index: Int,
                                                     @JsonProperty("song") val song: Song)