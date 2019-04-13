package cn.itcast.pojo;

import java.util.Date;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class PojoForTestAnotation {
	@RabbitListener(queues= {"default.send.queue"})
	public void handleSendQueue(String message) {
		System.out.println("从 default.send.queue 队列中获取一条消息：" + message);
	}
	
	// 因为我们配置了 MessageConverter ，如果我们监听的队列中的消息保存的数据就是特定的对象（比如Date）
	// 所以我们可以直接把Message 的body 部分转成指定的对象，在下面的回调函数中拿到
	@RabbitListener(queues= {"default.receive.queue"})
	public void handleReceiveQueue(Date date) {
		System.out.println("从 default.receive.queue 队列中获取一个Date 对象: " + date);
	}
	
	
	// 这个 RabbitListener 并不监听什么队列，只是用来声明队列的
	@RabbitListener(queuesToDeclare= {
			            // 声明一个普通的队列
						@Queue(name="myNormalQueue", durable="true", autoDelete="false"),
						// 声明一个延迟队列，消息会有统一的过期时间
						@Queue(name="myDelayedQueue", arguments= {
								@Argument(name="x-message-ttl", value="6000", type="java.lang.Long")
						}),
						// 声明一个延迟队列，如果消息过期，死信会使用指定的交换器，指定的路由键转发到对应的死信队列中
						@Queue(name="orderQueue", arguments= {
								@Argument(name="x-message-ttl", value="1800000", type="java.lang.Long"),
								@Argument(name="x-dead-letter-exchange", value="exchange.dlx"),
								@Argument(name="x-dead-letter-routing-key", value="order.dlx.key")
						})
					 },
			        // 如果我们想要声明队列或者交换器等，一般都要指明 rabbitAdmin
					admin="rabbitAdmin"
	               )
	public void handleNothing() {
		System.out.println("什么事情都不干，就是声明一下队列、交换器等的绑定关系");
	}
	
	@RabbitListener(bindings= {
			@QueueBinding(
					// 这个 value 就是指队列， 接收的是一个 @Queue 注解
					value=@Queue(name="myQueue"),
					exchange=@Exchange(name="myExchange", type=ExchangeTypes.DIRECT),
					// 这个key 就是指 BindingKey ，接收的是一个数组，但是我们一般就一个 BindingKey，也可以直接写字符串
					key= "my.binding.key"
			)
	})
	public void handleNothing2() {
		System.out.println("什么事情都不干，就是声明一下队列、交换器等的绑定关系");
	}
	
	// 这是监听 rpc.task.queue 队列的回调方法
	// 如果处理成功，则会把处理结果通过   default.exchange 交换器， rpc.result.key 
	@RabbitListeners({
		@RabbitListener(bindings= {
				@QueueBinding(value=@Queue(name="rpc.task.queue"),
					  	  exchange=@Exchange(name="default.exchange"),
					      key="rpc.task.key"
					     )
		}),
		@RabbitListener(queues="rpc.task.queue")
	})
	@SendTo("default.exchange/rpc.result.key")
	public String handleTask(String task) {
		System.out.println("接收到任务：" + task);
		String result = task + "===> 已经完成";
		return result;
	}
	
	@RabbitListeners({
		// 监听 rpc.result.queue 队列，这是 rpc 调用后，保存处理结果消息的队列
		// 这个方法就是专门监听处理结果的
		@RabbitListener(queues="rpc.result.queue"),
		// 声明一下 rpc.task.queue 和 rpc.result.queue  队列
		// 并把这两个队列跟 default.exchange 队列绑定
		@RabbitListener(bindings= {
									@QueueBinding(value=@Queue(name="rpc.result.queue"),
												   exchange=@Exchange(name="default.exchange"),
												   key="rpc.result.key"
												 )
								 })
	})
	public void getResult(String result) {
		System.out.println("接收处理结果：" + result);
	}
	
	// 分别获取 payload 和  header 参数
	@RabbitListener(queues="result.queue")
	public void getResultWithHeaders(@Payload String content, 
			                         @Header(name="user_id", required=true) String userId,
			                         Message message) {
		System.out.println("content: " + content);
		System.out.println("userId: " + userId);
		System.out.println("messageProperties: " + message.getMessageProperties());
	}
	

}
