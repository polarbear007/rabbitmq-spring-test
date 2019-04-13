package cn.itcast.rabbitmq.test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class HelloWorld {
	
	// 使用官方客户端工具连接  rabbitmq 的方式
	@Test
	public void test() throws IOException, TimeoutException {
		// 创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.48.130");
		factory.setPort(5672);
		factory.setUsername("root");
		factory.setPassword("root");
		factory.setVirtualHost("my_vhost");
		
		// 获取连接对象, 这个连接对象其实就是
		Connection conn = factory.newConnection();
		
		// 获取信道对象
		Channel channel = conn.createChannel();
		
		// 然后通过信道发送各种命令
		channel.exchangeDeclare("exchange_test", BuiltinExchangeType.DIRECT);
		channel.queueDeclare("queue_test", false, false, false, null);
		channel.queueBind("queue_test", "exchange_test", "test");
		
		channel.close();
		conn.close();
	}
}
