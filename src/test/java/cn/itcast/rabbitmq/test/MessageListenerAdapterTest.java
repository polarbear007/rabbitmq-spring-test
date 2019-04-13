package cn.itcast.rabbitmq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:messageListenerAdapterConfig.xml")
public class MessageListenerAdapterTest {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Test
	public void testMessageListenerAdapter() throws InterruptedException {
		String task = "背一遍乘法口诀";
		System.out.println("发送任务：" + task);
		rabbitTemplate.convertAndSend("exchange.test", "task.key", task);
		
		// 然后停一下小会儿，看能不能自动接收到对应的数据
		Thread.sleep(5*1000);
	}
}
