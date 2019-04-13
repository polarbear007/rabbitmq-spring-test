package cn.itcast.rabbitmq.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:messageListenerConfig.xml")
public class MessageListenerTest {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	// 其实这个测试方法本身并不重要，重要的是我们要把容器初始化，然后让配置的 MessageListener 起作用
    // 只要MessageListener 开始监听指定的队列，那么我们就可以往指定队列添加消息，查看效果了
	@Test
	public void test() throws InterruptedException {
		Thread.sleep(100*1000);
	}
	
	
	// 我们已经在配置文件里面指明了 template 默认会使用哪个交换器，使用什么路由键去发送消息
	// 同时也通过 MessageListenerContainer 指定了去监听哪一个队列。 
	// 为了演示方便，我们默认发送的队列跟监听的队列是同一个队列，所以我们可以使用 sendAndReceive() 方法
	@Test
	public void testSendandReceive() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
					Object receive = rabbitTemplate.convertSendAndReceive("task");
					System.out.println(receive);
				}; 
				
			}.start();
		}
		Thread.sleep(10*1000);
	}
	
	@Test
	public void testMessageListenerAdapter() throws InterruptedException {
		// 因为我们需要把java 对象进行类型转换，所以我们不能直接在管理页面手动添加，那么只能添加字符串数据
		// 所以我们使用 模板对象往对应的队列添加消息
		rabbitTemplate.convertAndSend("exchange.test", "test.date.key", new Date());
		rabbitTemplate.convertAndSend("exchange.test", "test.string.key", "hello world");
		
		// 然后停一下小会儿，看能不能自动接收到对应的数据
		Thread.sleep(5*1000);
	}
}
