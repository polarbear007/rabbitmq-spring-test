package cn.itcast.amqp.callback;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MyReturnCallback implements RabbitTemplate.ReturnCallback{
	// 必须重写 returnedMessage（） 方法
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		System.out.println("这是一条无法投递的消息");
		System.out.println("replyCode:" + replyCode);
		System.out.println("routingKey:" + routingKey);
		System.out.println("exchange:" + exchange);
		System.out.println("content:" + new String(message.getBody()));
	}

}
