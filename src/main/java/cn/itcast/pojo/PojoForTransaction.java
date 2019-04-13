package cn.itcast.pojo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener()
public class PojoForTransaction {
	
}
