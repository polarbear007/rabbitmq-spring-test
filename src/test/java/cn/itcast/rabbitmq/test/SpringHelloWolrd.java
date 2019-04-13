package cn.itcast.rabbitmq.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringHelloWolrd {
	@Autowired
	private CachingConnectionFactory connectionFactory;
	
	@Autowired
	private RabbitAdmin rabbitAdmin;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private DirectExchange exchangeTest;
	
	@Autowired
	private Queue queueTest;
	
	@Autowired
	private RabbitTemplate rabbitTemplate2;
	
	@Autowired
	private RabbitTemplate rabbitTemplate3;
	
	@Autowired
	private RabbitTemplate rabbitTemplate4;
	
	// 自己手动创建对象
	@Test
	public void test() {
		// 注意： 与spring 整合以后，spring 提供了一个 ConnectionFactory 接口， 不要跟官方客户端工具
		//      的 ConnectionFactory 弄混了
		// 我们这里使用的是 spring 的 ConnectionFactory 接口的实现类： CachingConnectionFactory
		CachingConnectionFactory factory = new CachingConnectionFactory();
		// 设置连接参数，这些东西都差不多
		factory.setHost("192.168.48.130");
		factory.setPort(5672);
		factory.setUsername("root");
		factory.setPassword("root");
		factory.setVirtualHost("my_vhost");
		
		// 我们首先要创建一个 RabbitAdmin 对象，这个对象可以创建、绑定、删除交换器和队列等
		AmqpAdmin admin = new RabbitAdmin(factory);
		// 声明一个队列
		admin.declareQueue(new Queue("myqueue"));
		
		// 然后我们创建一个 RabbitTemplate 对象，这个模板对象可以用来发送消息和接收消息
		AmqpTemplate template = new RabbitTemplate(factory);
		// 往myqueue 队列 发送一条消息
		template.convertAndSend("myqueue", "foo");
		// 从 myqueue 队列接收一条消息
		String foo = (String) template.receiveAndConvert("myqueue");
		System.out.println(foo);
	}
	
	@Test
	public void test2() {
		System.out.println(connectionFactory);
		System.out.println(rabbitAdmin);
		System.out.println(rabbitTemplate);
	}
	
	@Test
	public void testBinding() {
		// 创建一个Binding 对象
		Binding binding = new Binding("queue_test", 
				                      Binding.DestinationType.QUEUE, 
				                      "exchange_test", "test.routing.key", 
				                      null);
		
		// 调用事先创建好的那个 rabbitAdmin 来发送绑定关系的命令
		// 如果rabbitMQ 服务器中没有  queue_test 队列 或者 没有 exchange_test 交换器
		// 又或者两个都没有，也没有关系，会自动创建的。
		rabbitAdmin.declareBinding(binding);
	}
	
	// 另一种声明绑定的方式
	@Test
	public void tsetBindingBuilder() {
		Binding binding = BindingBuilder.bind(queueTest).to(exchangeTest).with("test2.routing.key");
		rabbitAdmin.declareBinding(binding);
	}
	
	// 自动声明绑定
	// 默认第一次获取连接以后，就会根据 xml 里面的配置 自动声明和绑定 队列、交换器
	@Test
	public void testAutoDeclare() {
		connectionFactory.createConnection();
	}
	
	// 如果我们是给  rabbitAdmin 的构造方法传入一个  connectionFactory 的话
	// 那么 rabbitAdmin 会在构造方法中再new 一个新的  rabbitTemplate 
	
	// 如果我们是给 rabbitAdmin 的构造方法传入一个  rabbitTemplate 的话
	// 那么 rabbitAdmin 会在构造方法中根据 template 去拿到  connectionFacory 对象
	@Test
	public void testRabbitAdmin() {
		System.out.println(rabbitAdmin.getRabbitTemplate() == rabbitTemplate);
	}
	
	// 发送消息
	@Test
	public void testRabbitTemplateSendMessage1() {
		// 创建Message 对象
		MessageProperties messageProperties = MessagePropertiesBuilder.newInstance()
												.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
												.build();
		Message message = new Message("hello world".getBytes(), messageProperties);
		
		// 然后使用 rabbitTemplate 发送message 
		// 同时指定  交换器名  和  路由鍵
		rabbitTemplate.send("exchange_test", "test.routing.key", message);
	}
	
	// 上面发送消息还需要指定交换器名和路由键，比较麻烦
	// 一般来说，我们某个方法发送的消息都是通过一个固定的交换器，发送到固定的一个队列中的
	// 那么我们就可以直接在 template 里面指定好这个默认的交换器和路由键，以后就直接发送消息就可以了
	@Test
	public void testRabbitTemplateSendMessage2() {
		// 创建Message 对象
		MessageProperties messageProperties = MessagePropertiesBuilder.newInstance()
												.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
												.build();
		Message message = new Message("hello world".getBytes(), messageProperties);
		
		// 然后使用 rabbitTemplate 发送message 
		// 同时指定  交换器名  和  路由鍵
		rabbitTemplate2.send(message);
	}
	
	 // 虽然我们配置了默认的交换器和路由键，确实方便了一些，但是那个 Message 对象很麻烦
	 // 所以我们可以考虑使用 template 提供的其他方法来发送消息
	
	//  convertAndSend()  方法，支持发送一个 Object 对象，不用我们封装 message 对象
	//  template 对象会自动帮我们把 Object 对象封装成 Message 对象去发送
	@Test
	public void testRabbitTemplateSendMessage3() {
		// 首先，我们可以自己指定交换器和路由键
		//rabbitTemplate.convertAndSend("exchange_test", "test.routing.key", "hello java");
		// 我们也可以使用默认的交换器和路由键
		//rabbitTemplate2.convertAndSend("你好呀");
		
		// 直接发送一个java 对象
		// 如果这个对象不是 string 对象， template 默认会帮我们把这个对象先序列化，再发送到rabbitmq 服务器
		 rabbitTemplate2.convertAndSend(new Date());
		 //rabbitTemplate2.convertAndSend(new Integer(12));
	}
	
	// 接收一条消息
	@Test
	public void testRabbitTemplateReceiveMessage1() {
		// 直接指定一个队列名，没有指定时间的，默认等待时间为0，即马上返回，可能得到一个null
//		Message message1 = rabbitTemplate.receive("queue_test");
//		System.out.println(new String(message1.getBody()));
		
		// 指定一个队列名，同时指定最长等待时间，如果超过这个时间，还没有拿到消息，就返回null
		Message message2 = rabbitTemplate.receive("queue_ttl", 1000);
		System.out.println(message2);
	}
	
	// 前面接收消息的方式已经比较简单了，但是如果一个方法接收的队列是固定的，那么就可以配置一个固定的队列
	@Test
	public void testRabbitTemplateReceiveMessage2() {
		// 配置了固定的队列以后，我们就可以直接用一个无参的 receive() 方法去接收消息了
//		Message message = rabbitTemplate3.receive();
//		System.out.println(new String(message.getBody()));
		
		// 上面的方法可能会遇到问题：
		//  我们接收到的消息可能并不都是字符串，也可能是序列化后的二进制数据
		// 如果我们都使用上面的  new String(message.getBody()) ,可能得到一个乱码的结果 
		
		// 这个时候，我们就可以使用  receiveAndConvert() 方法，把得到的结果自动转成Object 类型
		// 就算原来是字符串，那么转成 Object 类型也是可以的
		Object obj = rabbitTemplate3.receiveAndConvert();
		System.out.println(obj);
		
	}
	
	// 一般来我们的方法接收的数据类型都是固定的，比如都是 User 对象或者 Teacher 对象
	// 那么这个时候，我们就可以指定反序列化对后，自动把 Object 类型转成指定的类型
	
	// 如果我们想要使用这个，我们还得再把 template 对象配置一个SmartMessageConverter转换器，不然会报错
	// 如果我们不配置的话，默认是 SimpleMessageConverter 
	// template's message converter must be a SmartMessageConverter
	
	// SmartMessageConverter 的一个实现类就是 Jackson2JsonMessageConverter
	// 所以我们还得导入 jackson 的jar 包，再配置一个 Jackson2JsonMessageConverter 对象 
	@Test
	public void testRabbitTemplateReceiveMessage3() {
		
		// 配置了不同的类型转换器，所以我们就用同一个模板对象发送消息，再接收消息
		// 因为如果你使用其他模板发送消息，用的模板不同，无法反序列化
		rabbitTemplate4.convertAndSend(new Date());
		
		ParameterizedTypeReference<Date> ref = ParameterizedTypeReference.forType(Date.class);
		Date date = rabbitTemplate4.receiveAndConvert(ref);
		System.out.println(date);
	}
}
