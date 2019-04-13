package cn.itcast.amqp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ComponentScan(basePackages= {"cn.itcast.pojo"})
public class RabbitConfig {
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setUri("amqp://admin:admin@192.168.48.131:5672/annotation");

		// 设置发送者确认，如果发送的消息没有被交换器接收，返回 nack ； 如果有被某个交换器接收，返回 ack
		// 我们可以设置一个 confirmCallback 回调接收返回的消息 （防止消息丢失）
		// 不管有没有到达服务器，回调函数都会返回结果
		factory.setPublisherConfirms(true);
		// 设置 mandatory 参数，如果发送的消息到达了交换器，但是没有被某个队列保存起来，那么就会把这条消息
		// 退回给发送者。我们可以使用一个 returnCallback 回调接收返回的消息（防止消息丢失）
		// 如果消息没有到达服务器的交换器（比如你写错交换器名），那么消息会直接丢弃；
		// 如果消息正常保存队列，不会回调； 如果消息到达交换器，但是没有匹配的队列，则回调。
		factory.setPublisherReturns(true);
		factory.setChannelCacheSize(30);
		return factory;
	}

	@Bean
	public RabbitAdmin rabbitAdmin() {
		// 建议使用这个构造方法传入 connectionFactory ,而不是传入一个 rabbitTemplate
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
	
	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		// 设置messageConverter
		rabbitTemplate.setMessageConverter(messageConverter());
		// 省得接收数据的时候写队列名
		rabbitTemplate.setDefaultReceiveQueue("default.receive.queue");
		// 省得发送数据的时候写交换器名和路由键
		rabbitTemplate.setExchange("default.exchange");
		rabbitTemplate.setRoutingKey("default.send.key");
		// 这是一个复合字符串，默认会把这个值添加到发送的消息的 replyTo 属性上
		rabbitTemplate.setReplyAddress("default.exchange/result.bindingKey");

		// 配置 confirmCallback ，记得在 connectionFactory 那里开启confirm 模式
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				if (!ack) {
					System.out.println("一条消息被退回：" + correlationData);
				}
			}
		});

		// 配置 returnCallback ,记得要在 connectionFactory 那里开启 return 模式
		// 还要再设置 mandatory 参数为 true
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				System.out.println("一条无法找到匹配的队列被退回：" + new String(message.getBody()));
			}
		});

		return rabbitTemplate;
	}

	@Bean("rabbitListenerContainerFactory")
	public RabbitListenerContainerFactory<SimpleMessageListenerContainer> containerFactory(){
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setMessageConverter(messageConverter());
		return factory;
	}
	
	@Bean
	public Exchange defaultExchange() {
		return new DirectExchange("default.exchange", true, false);
	}

	@Bean
	public Queue defaultReceiveQueue() {
		return new Queue("default.receive.queue", true);
	}

	@Bean
	public Binding binding() {
		return new Binding("default.receive.queue", 
							DestinationType.QUEUE, 
							"default.exchange", 
							"default.receive.key",
							null);
	}

	@Bean
	public Queue defaultSendQueue() {
		return new Queue("default.send.queue", true);
	}

	@Bean
	public Binding binding2() {
		return new Binding("default.send.queue", 
							DestinationType.QUEUE, 
							"default.exchange", 
							"default.send.key", 
							null);
	}

	@Bean
	public Queue resultQueue() {
		return new Queue("result.queue", true);
	}

	@Bean
	public Binding binding3() {
		return new Binding("result.queue", 
							DestinationType.QUEUE, 
							"default.exchange", 
							"result.bindingKey", 
							null);
	}
}
