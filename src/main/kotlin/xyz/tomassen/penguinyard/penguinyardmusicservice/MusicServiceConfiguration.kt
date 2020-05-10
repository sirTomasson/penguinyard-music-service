package xyz.tomassen.penguinyard.penguinyardmusicservice

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MusicServiceConfiguration {

    @Bean fun connectionFactory() = CachingConnectionFactory("localhost")

    @Bean fun amqpAdmin() = RabbitAdmin(connectionFactory())

    @Bean fun jsonMessageConverter() = Jackson2JsonMessageConverter()

    @Bean fun rabbitTemplate(): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory())
        template.messageConverter = jsonMessageConverter()
        return template
    }
}