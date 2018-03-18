package netty.demo10.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	// 原子操作的整型数，多线程时不需要用synchronized
	private static AtomicInteger counter = new AtomicInteger(1);
	
	private String name = "kagayakukii";
	private boolean daemon;
	private int priority;	// 线程优先级
	
	
	public NamedThreadFactory(String name) {
		this(name, false, -1);
	}

	public NamedThreadFactory(String name, boolean daemon) {
		this(name, daemon, -1);
	}

	public NamedThreadFactory(String name, boolean daemon, int priority) {
		this.name = name;
		this.daemon = daemon;
		this.priority = priority;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, name+"["+counter.getAndIncrement()+"]");
		thread.setDaemon(daemon);
		if(priority != -1){
			thread.setPriority(priority);
		}
		return thread;
	}

}
