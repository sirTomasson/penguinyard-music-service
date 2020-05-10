package xyz.tomassen.penguinyard.penguinyardmusicservice

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import xyz.tomassen.penguinyard.penguinyardmusicservice.model.Song
import xyz.tomassen.penguinyard.penguinyardmusicservice.model.User
import xyz.tomassen.penguinyard.penguinyardmusicservice.topic.SongDequeueTopic
import xyz.tomassen.penguinyard.penguinyardmusicservice.topic.SongPlayNextTopic

@SpringBootApplication
class PenguinyardMusicServiceApplication {
    companion object {

        /**
         * Enqueue user topic.
         */
        const val USER_ENQUEUE_TOPIC = "userEnqueueTopic"

        /**
         * Dequeue user topic.
         */
        const val USER_DEQUEUE_TOPIC = "userDequeueTopic"

        /**
         * User queue topic.
         */
        const val USER_QUEUE_TOPIC = "userQueueTopic"

        /**
         * Enqueue a song from user queue topic.
         */
        const val SONG_ENQUEUE_TOPIC = "songEnqueueTopic"

        /**
         * Dequeue a song from user queue topic.
         */
        const val SONG_DEQUEUE_TOPIC = "songDequeueTopic"

        /**
         * Play next song topic.
         */
        const val SONG_PLAY_NEXT_TOPIC = "songPlayNextTopic"

        /**
         * Current song playing topic.
         */
        const val SONG_TOPIC = "songTopic"

    }

    /**
     * Users in order of who gets to play the song; Top user is currently playing.
     */
    private val _userQueue = mutableListOf<User>()

    /**
     * Id user mapping.
     */
    private val _userIdSongMap = mutableMapOf<Int, MutableList<Song>>()

    @Autowired
    lateinit var amqpTemplate: AmqpTemplate

    @RabbitListener(queues = [USER_ENQUEUE_TOPIC])
    fun listenForUserEnqueueTopic(user: User) {
        println("Received: $user for enqueue")
        if (!_userQueue.contains(user) && !_userIdSongMap.containsKey(user.id)) {
            _userQueue.add(user)
            _userIdSongMap[user.id] = mutableListOf()
            amqpTemplate.convertAndSend(USER_QUEUE_TOPIC, _userQueue)
        }
    }

    @RabbitListener(queues = [USER_DEQUEUE_TOPIC])
    fun listenForUserDequeueTopic(user: User) {
        println("Received: $user for dequeue")
        if (_userQueue.remove(user) && _userIdSongMap.remove(user.id) != null) {
            amqpTemplate.convertAndSend(USER_QUEUE_TOPIC, _userQueue)
        }
    }

    @RabbitListener(queues = [USER_QUEUE_TOPIC])
    fun listenForUserQueueTopic(users: List<User>) {
        println("Received: $users updated")
    }

    @RabbitListener(queues = [SONG_ENQUEUE_TOPIC])
    fun listenForUserEnqueueTopic(song: Song) {
        println("Received: $song for enqueue")
        if (_userIdSongMap.containsKey(song.userId)) {
            val songs = _userIdSongMap[song.userId]
            songs?.add(song)
        }
    }

    @RabbitListener(queues = [SONG_DEQUEUE_TOPIC])
    fun listenForSongDequeueTopic(songDequeueTopic: SongDequeueTopic) {
        println("Received: $songDequeueTopic for dequeue")
        if (_userIdSongMap.containsKey(songDequeueTopic.song.userId)) {
            val songs = _userIdSongMap[songDequeueTopic.song.userId]
            songs?.removeAt(songDequeueTopic.index)
        }
    }

    @RabbitListener(queues = [SONG_PLAY_NEXT_TOPIC])
    fun listenForSongPlayNextTopic(songPlayNextTopic: SongPlayNextTopic) {
        println("Received: songPlayNextTopic")
        if (_userQueue.isNotEmpty()) {
            _userQueue.add(_userQueue.removeAt(0)) // add user back at end of the queue

            for (user in _userQueue) {
                val songs = _userIdSongMap[user.id]
                if (songs!!.isNotEmpty()) {
                    val nextSong = songs.removeAt(0)
                    amqpTemplate.convertAndSend(SONG_TOPIC, nextSong)
                    songs.add(nextSong) // add to back of the queue

                    _userQueue.remove(user)
                    _userQueue.add(0, user)
                    // notify that _userQueue order has changed
                    amqpTemplate.convertAndSend(USER_QUEUE_TOPIC, _userQueue)
                    return
                }
            }
        }
    }

    @RabbitListener(queues = [SONG_TOPIC])
    fun listenForSongTopic(song: Song) {
        println("Received: $song")
    }

    @Bean
    fun userEnqueueTopic() = Queue(USER_ENQUEUE_TOPIC)

    @Bean
    fun userDequeueTopic() = Queue(USER_DEQUEUE_TOPIC)

    @Bean
    fun userQueueTopic() = Queue(USER_QUEUE_TOPIC)

    @Bean
    fun songEnqueueTopic() = Queue(SONG_ENQUEUE_TOPIC)

    @Bean
    fun songDequeTopic() = Queue(SONG_DEQUEUE_TOPIC)

    @Bean
    fun songPlayNextTopic() = Queue(SONG_PLAY_NEXT_TOPIC)

    @Bean
    fun songTopic() = Queue(SONG_TOPIC)
}

fun main(args: Array<String>) {
    runApplication<PenguinyardMusicServiceApplication>(*args)
}
