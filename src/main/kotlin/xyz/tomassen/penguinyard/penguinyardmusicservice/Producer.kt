package xyz.tomassen.penguinyard.penguinyardmusicservice

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import xyz.tomassen.penguinyard.penguinyardmusicservice.model.Song
import xyz.tomassen.penguinyard.penguinyardmusicservice.model.User
import xyz.tomassen.penguinyard.penguinyardmusicservice.topic.SongDequeueTopic
import xyz.tomassen.penguinyard.penguinyardmusicservice.topic.SongPlayNextTopic

fun main(args: Array<String>) {
    val context = AnnotationConfigApplicationContext(MusicServiceConfiguration::class.java)
    val amqpTemplate = context.getBean(AmqpTemplate::class.java)
    val song = Song("Blaat", 1)
    amqpTemplate.convertAndSend("songTopic", song)
    println("Sent: $song for play")

    Thread.sleep(1000)
    val user = User(1, "youri")
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.USER_ENQUEUE_TOPIC, user)
    println("Sent: $user for enqueue")

    Thread.sleep(1000)
    val eric = User(2, "eric")
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.USER_ENQUEUE_TOPIC, eric)
    println("Sent: $eric for enqueue")

    Thread.sleep(1000)
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.USER_DEQUEUE_TOPIC, user)
    println("Sent: $user for dequeue")

    Thread.sleep(1000)
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.SONG_ENQUEUE_TOPIC, song)
    println("Sent: $song for enqueue")

    Thread.sleep(1000)
    val songDequeueTopic = SongDequeueTopic(0, song)
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.SONG_DEQUEUE_TOPIC, songDequeueTopic)
    println("Sent: $songDequeueTopic for dequeue")


    Thread.sleep(1000)
    val songByEric = Song("Rick Ross", 2)
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.SONG_ENQUEUE_TOPIC, songByEric)
    println("Sent: $songByEric for enqueue")

    Thread.sleep(1000)
    val songPlayNextTopic = SongPlayNextTopic(true)
    amqpTemplate.convertAndSend(PenguinyardMusicServiceApplication.SONG_PLAY_NEXT_TOPIC, songPlayNextTopic)
    println("Sent: $songPlayNextTopic")
}
