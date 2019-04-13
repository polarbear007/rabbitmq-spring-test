package cn.itcast.pojo;

public class Worker {
	public String handleTask(String task) {
		System.out.println("获取并处理任务：" + task);
		return task + "===> 已经完成";
	}
	
	public void getResult(String result) {
		System.out.println("处理结果：" + result);
	}
}
