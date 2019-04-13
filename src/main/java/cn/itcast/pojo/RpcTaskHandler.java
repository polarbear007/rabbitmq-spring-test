package cn.itcast.pojo;

public class RpcTaskHandler {
	public String handle(String task) {
		System.out.println("接收到任务：" + task);
		return task + "===> 已完成";
	}
}
