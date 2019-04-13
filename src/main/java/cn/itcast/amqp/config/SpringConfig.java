package cn.itcast.amqp.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost("192.168.48.131");
		factory.setPort(5672);
		factory.setUsername("admin");
		factory.setPassword("admin");
		factory.setVirtualHost("helloworld2");
		factory.setPublisherReturns(true);
		return factory;
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(@Autowired ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMandatory(true);
		template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
				System.out.println("这是一条无法投递的消息");
				System.out.println("replyCode:" + replyCode);
				System.out.println("routingKey:" + routingKey);
				System.out.println("exchange:" + exchange);
				System.out.println("content:" + new String(message.getBody()));
			}
		});
		return template;
	}
}
