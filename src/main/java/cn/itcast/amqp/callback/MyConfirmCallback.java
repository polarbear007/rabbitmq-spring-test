package cn.itcast.amqp.callback;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MyConfirmCallback implements RabbitTemplate.ConfirmCallback{
	// 第一个参数是 message 的包装类，我们知道一下就好了
	// 第二个参数就是表示服务器是否接收到这条消息，true 表示服务器返回  Basic.ack ; false 表示返回 Basic.nack
	// 第三个参数一般是失败的时候才看的，是失败的原因 
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.println("是否成功接收消息：" + ack);
	}
}
