package cn.itcast.rabbitmq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.amqp.config.SpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:helloworld2.xml")
public class SpringHelloWolrd2 {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Test
	public void testReturnCallBack() {
		rabbitTemplate.send("exchange.test",
				            "routing.key.test", 
				            MessageBuilder.withBody("return test".getBytes()).build());
	}
	
	@Test
	public void testReturnCallBack2() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		
		RabbitTemplate template = context.getBean(rabbitTemplate.getClass());
		
		template.send("exchange.test",
	            "routing.key.test", 
	            MessageBuilder.withBody("return test".getBytes()).build());
		
		context.close();
	}
	
	// confirm 回调只要交换器有接收到消息就直接返回  true ，就算最终这条消息没有被 保存到队列中
	// return 回调（mandatory参数）则关注的是消息有没有被 确实保存到某个队列中
	@Test
	public void testConfirmCallback() {
		rabbitTemplate.send("exchange.test", "routing.key.test",
				        MessageBuilder.withBody("hello".getBytes()).build());
	}
	
	// 如果一条消息根本就到达不了交换器，那么 confirm 回调会返回 false
	// 但是 return 回调则根本不起作用
	@Test
	public void testConfirmCallback2() {
		// 这次我们不仅路由键乱写，连接交换器名也是乱写的
		rabbitTemplate.send("exchange.test.hello", "routing.key.test",
				        MessageBuilder.withBody("hello".getBytes()).build());
	}
}
