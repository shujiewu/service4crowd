package cn.edu.buaa.act.data.processor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
	@Bean
    public Queue queueOne(){
		return new Queue("runtime_pro_response");
	}
	@Bean
	public Binding queueTwoBinding(){
		return BindingBuilder.bind(queueOne()).to(new TopicExchange("RUNTIME_RESPONSE")).with("RUNTIME_RESPONSE");
	}

	@Bean
	public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}
	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	@Bean
	public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory mqConnectionFactory){
		SimpleRabbitListenerContainerFactory listenerContainerFactory=new SimpleRabbitListenerContainerFactory();
		listenerContainerFactory.setConnectionFactory(mqConnectionFactory);
		listenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
		return listenerContainerFactory;
	}
}