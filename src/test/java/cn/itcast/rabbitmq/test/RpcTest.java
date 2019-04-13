package cn.itcast.rabbitmq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:rpcConfig.xml")
public class RpcTest {
	@Autowired
	private RabbitTemplate clientTemplate;
	
	@Autowired
	private RabbitTemplate serverTemplate;

	@Test
	public void testClientTemplate() throws InterruptedException {
		String task = "背一遍乘法口诀";
		System.out.println("发送任务：" + task);
		Object result = clientTemplate.convertSendAndReceive(task);
		System.out.println("处理结果：" + result);
		Thread.sleep(15 * 1000);
	}
	
	@Test
	public void testServerTemplate() throws InterruptedException{
		// 因为我们在 serverTemplate 里面配置了默认的 queue ，所以这里不需要指定从哪个队列获取消息
		serverTemplate.receiveAndReply(new ReceiveAndReplyCallback<String, String>() {
			@Override
			public String handle(String task) {
				System.out.println("接收任务：" + task);
				return task + "===> 已经完成";
			}
		});
		
		Thread.sleep(10*1000);
	}
}
