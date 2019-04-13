package cn.itcast.amqp.message.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class MyMessageListener implements MessageListener{

	@Override
	public void onMessage(Message message) {
		System.out.println("接收到一条消息：" + new String(message.getBody()));
	}

}
