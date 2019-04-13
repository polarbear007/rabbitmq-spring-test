package cn.itcast.amqp.message.listener;

import org.springframework.amqp.support.ConsumerTagStrategy;

public class MyConsumerTagStrategy implements ConsumerTagStrategy{

	@Override
	public String createConsumerTag(String queue) {
		if(queue.equals("queue.for.date.handler")) {
			return "dateConsumer";
		}else if(queue.equals("queue.for.string.handler")){
			return "stringConsumer";
		}
		return null;
	}

}
