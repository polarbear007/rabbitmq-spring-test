package cn.itcast.pojo;

import java.util.Date;

public class Service {
	public Date handleDate(Date date) {
		System.out.println("从queue.for.date.handler队列中接收到一条消息：" + date);
		return new Date();
	}
	
	public void handleString(String message) {
		System.out.println("从queue.for.string.handler队列中接收到一条消息：" + message);
	}
}
