package cn.itcast.pojo;

import java.util.Date;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RabbitListeners({
	@RabbitListener(bindings= {
			@QueueBinding(
							value=@Queue(name="multi.method.test.queue"),
							exchange=@Exchange(name="default.exchange"),
							key="multi.method.test.key"
					)
	}),
	@RabbitListener(queues="multi.method.test.queue" )
})
public class PojoForMultiMethodTest {
	@RabbitHandler(isDefault=true)
	//@RabbitListener(queuesToDeclare= {@Queue(name="string.result.queue")})
	@SendTo("string.result.queue")
	public String handleString(@Payload String stringMessage) {
		System.out.println("******************");
		System.out.println("调用handleString() 方法");
		System.out.println("stringMessage: " + stringMessage);
		System.out.println("******************");
		return stringMessage;
	}
	
	@RabbitHandler
	//(queuesToDeclare= {@Queue(name="date.result.queue")})
	@SendTo("date.result.queue")
	public Date handleDate(@Payload Date dateMessage) {
		System.out.println("******************");
		System.out.println("调用handleDate() 方法");
		System.out.println("dateMessage: " + dateMessage);
		System.out.println("******************");
		return dateMessage;
	}
	
	@RabbitHandler
	//@RabbitListener(queuesToDeclare= {@Queue(name="integer.result.queue")})
	@SendTo("integer.result.queue")
	public Integer handleInteger(@Payload Integer intMessage) {
		System.out.println("******************");
		System.out.println("调用handleInteger() 方法");
		System.out.println("intMessage: " + intMessage);
		System.out.println("******************");
		return intMessage;
	}
}
