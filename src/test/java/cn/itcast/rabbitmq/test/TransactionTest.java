package cn.itcast.rabbitmq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.amqp.config.RabbitTransactionConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RabbitTransactionConfig.class})
public class TransactionTest {
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Test
	public void test() {
		System.out.println(connectionFactory.createConnection());
	}
}
