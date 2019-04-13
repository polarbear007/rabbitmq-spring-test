package cn.itcast.pojo;

public class Student {
	public void handle(byte[] message) {
		System.out.println("接收到消息：" + new String(message));
	}
}
