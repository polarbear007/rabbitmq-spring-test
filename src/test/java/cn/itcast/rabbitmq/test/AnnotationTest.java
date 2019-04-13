package cn.itcast.rabbitmq.test;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.amqp.config.RabbitConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RabbitConfig.class})
public class AnnotationTest {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired
	private MessageConverter messageConverter;
	
	// 看一下能不能连接成功
	@Test
	public void test() {
		System.out.println(connectionFactory.createConnection());
	}
	
	// 看一下能不能正常发送消息
	@Test
	public void test2() {
		String msg = "hello world";
		rabbitTemplate.convertAndSend(msg);
		System.out.println(rabbitTemplate.receiveAndConvert("default.send.queue"));
	}
	
	// 看一下能不能正常接收消息
	@Test
	public void test3() {
		rabbitTemplate.convertAndSend("default.receive.key", "哈哈哈");
		Object message = rabbitTemplate.receiveAndConvert();
		System.out.println("接收到消息：" + message);
	}
	
	// 测试一下 returnCallback 好不好用
	// 故意写一个匹配不到任何队列的的 routingKey
	@Test
	public void test4() throws InterruptedException {
		rabbitTemplate.convertAndSend("hello.world", "hello world");
		Thread.sleep(10*1000);
	}
	
	// 测试一下  confirmCallback 好不好用
	// 故意写一个不存在的 交换器名,看会不会调用 confirmCallback
	// ===> 可以调用 confirmCallback ，但是拿不到原来的消息，因为本身 confirm 模式就是返回 nack 或者 ack，通知一下而已
	//      在 rabbit-client 里面，如果我们想要拿到发送的消息，我们得在发送之前保存起来
	//      在spring 中，你也可以把消息事先保存起来，但是回调函数往往跟我们的业务方法是分开的，你无法拿到下面的 msg 内容。
	//      当然，你可以说使用 ThreadLocal ,发送之前先把消息保存在本地线程中，如果出现回调，我们就从本地线程找回消息
	//      如果你的业务方法一次只发送一条消息，那么可能不会有什么问题； 但是如果你的业务方法一次性是发送大量数据的话，比如说1000条
	//      rabbitmq 并不会给你返回 1000 条确认消息，而是随机的，比如你发了10条才给你回个 ack ，表示前面10条都没问题
	//      比如给你回个  nack ，表示前面 200 条中可能有一条出错了。 
	
	//   ===> 异步confirm 模式处理起来非常麻烦，如果你对消息的安全性要求非常高的话，建议使用事务进行处理。
	@Test
	public void test5() throws InterruptedException {
		String msg = "hello";
		for (int i = 0; i < 10; i++) {
			rabbitTemplate.convertAndSend("exchange.not.exist", "any.key", msg);
		}
		
		Thread.sleep(1000*1000);
	}
	
	// 测试 @rabbitListener 能不能正常接收消息，我们只需要开启容器，默认就是开始监听了
	@Test
	public void test6() throws InterruptedException {
		// 这里我们只管发送消息就好了，监听和回调都是自动调用的
		for (int i = 0; i < 10; i++) {
			String msg = "hello" + i;
			System.out.println("发送消息：" + msg);
			rabbitTemplate.convertAndSend(msg);
		}
		Thread.sleep(10*1000);
	}
	
	@Test
	public void test7() throws InterruptedException {
		// 同样，我们这里只管发送一个Date 类型的对象到 default.receive.queue 队列
		// 看看 @rabbitListener 能不能监听并消费这条消息，并把消息转成 Date 对象
		Date date = new Date();
		System.out.println("发送了一个对象：" + date);
		rabbitTemplate.convertAndSend("default.receive.key", date);
		Thread.sleep(10*1000);
	}
	
	@Test
	public void test8() throws InterruptedException {
		String task = "背一遍乘法口诀";
		System.out.println("发送任务：" + task);
		rabbitTemplate.convertAndSend("default.exchange", "rpc.task.key", task);
		Thread.sleep(10*1000);
	}
	
	// 测试 @payLoad 和 @Header 参数能不能正常获取对应的数据
	// 如果 @payLoad 对应的参数是  byte[] 类型，那么我们发送数据的时候就直接   send() 
	// 如果 @payLoad 对应的参数是某个 java 类型，那么我们发送数据的时候应该使用 converAndSend() 
	@Test
	public void test9() throws InterruptedException {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		messageProperties.setHeader("user_id", UUID.randomUUID().toString());
		Message message = messageConverter.toMessage("hello world", messageProperties);
		
		rabbitTemplate.send("default.exchange", "result.bindingKey", message);
		
		Thread.sleep(10*1000);
	}
	
	// 测试@rabbitHandler 
	@Test
	public void test10() throws InterruptedException{
		rabbitTemplate.convertAndSend("default.exchange", "multi.method.test.key", "hello world");
		rabbitTemplate.convertAndSend("default.exchange", "multi.method.test.key", new Date());
		rabbitTemplate.convertAndSend("default.exchange", "multi.method.test.key",10086);
		//rabbitTemplate.convertAndSend("default.exchange", "multi.method.test.key", 100.12);
		Thread.sleep(10*1000);
	}
}
