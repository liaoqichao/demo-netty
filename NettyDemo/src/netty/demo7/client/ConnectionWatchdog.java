package netty.demo7.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

/**
 * Sharable注解，表示多个ChannelPipeline线程安全地使用同一个Handler
 */
@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

	private final Bootstrap b;
	private final Timer timer;
	private final int port;
	private final String host;
	/**
	 * 内存的可见性：
	 * JVM会为每个线程分一块独立内存，用于提高效率，如果a线程修改了a线程变量i的值，
	 * b线程无法获取a线程变量i的最新值，这叫做内存不可见。
	 * 
	 * A线程执行任务：
	 * 1. 初始化flag值为false
	 * 2. 休眠200ms
	 * 3. 将flag值设置为true
	 * 4. 输出flag的值
	 * 
	 * main线程：
	 * 1. 启动A线程
	 * 2. 死循环，死循环中判断A线程flag的值，如果为true输出"aaa"然后跳出循环。
	 * 
	 * 运行结果：true（A线程第四步输出）, 即使flag为true之后，主线程也不输出aaa。
	 * 原因：死循环（while(true){}）效率太高，使得main线程没有时间在主存中获取flag的值，每次都是在main线程中缓存的值
	 * 	（初始化flag的值在主存中，main线程获取主存flag的值放到本线程的缓存中）。如果在判断前
	 * 添加一些语句，例如sysout("bbb")，使得while(true)到task.isFlag()的时间有些延迟，main线程就有时间
	 * 从主存中获取flag的值，就会输出aaa。
	 * 	while(true){
	 * 		if(task.isFlag()){sysout("aaa");}
	 * 	}
	 * 
	 * 对于上面这种情况：A线程修改了值，但是main线程得到的还是旧值，这种情况叫做“内存不可见”。
	 * 
	 * 								+--------------+
	 * 								|	  主存 	   |
	 * 								| flag = false |
	 * 								| 3.flag=true  |
	 * 								+--------------+
	 * 			+-----------------+					+----------------+
	 * 			|  线程A			  |					|	main线程		 |
	 *   		| 1. flag = false |					|	flag = false |
	 *   		| 2. flag = true  |					|				 |
	 *   		+-----------------+					+----------------+
	 * 
	 * Java的同步机制分成2部分：synchronized（同步块或同步方法）、volatile（adj.易变的，不稳定的）。
	 * 其中volatile变量的同步性较差（但有时它更简单并且开销更低），而且其使用也更容易出错
	 * 
	 * 锁提供了2种主要特性：互斥性和可见性。互斥性即一次只允许一个线程持有某个特定的锁，因此可以使用该特性实现
	 * 对共享数据的协调访问协议，这样一次只有一个线程能够使用该共享数据。可见性即确保在释放锁之前对共享数据做出
	 * 的更改对于随后获得锁的线程是可见的。
	 * 
	 * volatile具有synchronized的可见性，但不具备原子性。这就是说线程能够自动发现volatile变量的最新值。
	 * 
	 * volatile不会像锁一样造成线程阻塞
	 * 
	 * 当一个变量定义为 volatile 之后，将具备两种特性：
　　	 *  1.保证此变量对所有的线程的可见性，这里的“可见性”，如本文开头所述，当一个线程修改了这个变量的值，
	 * 	 volatile 保证了新值能立即同步到主内存，以及每次使用前立即从主内存刷新。但普通变量做不到这点，
	 *   普通变量的值在线程间传递均需要通过主内存（详见：Java内存模型）来完成。
　　	 *  2.禁止指令重排序优化。有volatile修饰的变量，赋值后多执行了一个“load addl $0x0, (%esp)”操作，
	 * 	 这个操作相当于一个内存屏障（指令重排序时不能把后面的指令重排序到内存屏障之前的位置），只有一个CPU访问内存时，
	 * 	 并不需要内存屏障；（什么是指令重排序：是指CPU采用了允许将多条指令不按程序规定的顺序分开发送给各相应电路单元处理）。
	 * 
	 * 正确使用volatile变量的条件：
	 * 	1. 对变量写的操作不能依赖当前值。（不能volatile int i=0;  i++;）
	 *  2. 该变量没有包含在具有其他变量的不变式(Invariants)中。（不能volatile int end = 5; if(start < end){...}）
	 *  
	 *  性能：
	 *  volatile变量的读操作和非volatile变量的读操作性能几乎一样，而volatile变量写操作开销比非volatile变量的大，
	 *  因为要保证可见性需要实现内存界定（Memory Fence），即使如此，volatile的总开销仍然比锁获取低。
	 *  
	 *  volatile 的读性能消耗与普通变量几乎相同，但是写操作稍慢，因为它需要在本地代码中插入许多内存屏障指令
	 *  来保证处理器不发生乱序执行。（什么是指令重排序：是指CPU采用了允许将多条指令不按程序规定的顺序分开发送给各相应电路单元处理）
	 *  
	 *  volatile 操作不会像锁一样造成阻塞，因此，在能够安全使用 volatile 的情况下，volatile 可以提供一些优于锁的可伸缩特性。
	 *  如果读操作的次数要远远超过写操作，与锁相比，volatile 变量通常能够减少同步的性能开销。
	 *  
	 *  使用例子：
	 *  1. 状态标志。
	 *  	volatile boolean shutdownRequested = false;
	 *  	public void shutdown(){shutdownRequested = true;}
	 *  	public void doWork(){while(!shutdownRequested){...;}}
	 *  	shutdown()方法可能是另外一个线程外部调用的
	 *  
	 *  	很可能会从循环外部调用 shutdown() 方法 —— 即在另一个线程中 —— 因此，
	 *  	需要执行某种同步来确保正确实现 shutdownRequested 变量的可见性。（可能会从 JMX 侦听程序、GUI 事件
	 *  	线程中的操作侦听程序、通过 RMI 、通过一个 Web 服务等调用）。然而，使用synchronized 块编写循环要比
	 *  	使用volatile 状态标志编写麻烦很多。由于 volatile 简化了编码，并且状态标志并不依赖于
	 *  	程序内任何其他状态，因此此处非常适合使用 volatile。
	 *  
	 *  	这种类型的状态标记的一个公共特性是：通常只有一种状态转换；shutdownRequested 标志从 false 转换为 true，
	 *  	然后程序停止。这种模式可以扩展到来回转换的状态标志，但是只有在转换周期不被察觉的情况下才能扩展（
	 *  	从 false 到 true，再转换到 false）。此外，还需要某些原子状态转换机制，例如原子变量。
	 *  
	 *  	原子变量通过原子操作（函数）来实现：这种方式也叫做CAS,他不仅是原子类的底层实现方式，
	 *  	也是java锁的底层实现方式。就是在设置新值之前判断当前是否还是老值，如果是则设置新值，
	 *  	如果不是则重新计算新值后再尝试设置。
	 *  
	 *  2. 一次性安全发布 
	 *  	缺乏同步会导致无法实现可见性，这使得确定何时写入对象引用而不是原语值变得更加困难。
	 *  	在缺乏同步的情况下，可能会遇到某个对象引用的更新值（由另一个线程写入）和该对象状态的旧值同时存在。
	 *  	（这就是造成著名的双重检查锁定（double-checked-locking）问题的根源，
	 *  	其中对象引用在没有同步的情况下进行读操作，产生的问题是您可能会看到一个更新的引用，
	 *  	但是仍然会通过该引用看到不完全构造的对象）。
	 *  
	 *  	单例模式其中一个实现方式就有双重锁。Demo项目中SingleDoubleLock类
	 *  
	 *  	public class BackgroundFloobleLoader {
	 *		public volatile Flooble theFlooble;
	 *		
	 *		public void initInBackground() {
	 *		 // do lots of stuff
	 *		 theFlooble = new Flooble(); // this is the only write to theFlooble
	 *		 }
	 *		}
	 *		public class SomeOtherClass {
	 *		 public void doWork() {
	 *		 while (true) { 
	 *		 // do some stuff...
	 *		 // use the Flooble, but only if it is ready
	 *		 if (floobleLoader.theFlooble != null) 
	 *		 doSomething(floobleLoader.theFlooble);
	 *		 }
	 *		 }
	 *		}
	 *  	如果 theFlooble 引用不是 volatile 类型，doWork() 中的代码在解除对 theFlooble 的引用时，
	 *  	将会得到一个不完全构造的 Flooble。
	 * 		该模式的一个必要条件是：被发布的对象必须是线程安全的，
	 * 		或者是有效的不可变对象（有效不可变意味着对象的状态在发布之后永远不会被修改）。
	 * 		volatile 类型的引用可以确保对象的发布形式的可见性，但是如果对象的状态在发布后将发生更改，
	 * 		那么就需要额外的同步。
	 * 	3. 独立观察
	 * 		安全使用 volatile 的另一种简单模式是：定期 “发布” 观察结果供程序内部使用。
	 * 		例如，假设有一种环境传感器能够感觉环境温度。一个后台线程可能会每隔几秒读取一次该传感器，
	 * 		并更新包含当前文档的 volatile 变量。然后，其他线程可以读取这个变量，从而随时能够看到最新的温度值。
	 * 		该模式是前面模式的扩展；将某个值发布以在程序内的其他地方使用，但是与一次性事件的发布不同，这是一系列独立事件。
	 * 		这个模式要求被发布的值是有效不可变的 —— 即值的状态在发布后不会更改。使用该值的代码需要清楚该值可能随时发生变化。
	 *  4. volatile bean
	 *  	volatile bean即把JavaBean中所有变量都加上volatile。
	 *  	对于对象引用的数据成员，引用的对象必须是有效不可变的。（这将禁止具有数组值的属性，
	 *  	因为当数组引用被声明为 volatile 时，只有引用而不是数组本身具有 volatile 语义）
	 *  5. 开销较低的读-写锁策略
	 *  	前提：读操作远远大于写操作。
	 *  	@ThreadSafe
	 *		public class CheesyCounter {
	 *		 // Employs the cheap read-write lock trick
	 *		 // All mutative operations MUST be done with the 'this' lock held
	 *		 @GuardedBy("this") private volatile int value;
	 *		
	 *		public int getValue() { return value; }
	 *		
	 *		public synchronized int increment() {
	 *		 return value++; // 必须同步。1. 对变量写的操作不能依赖当前值。（不能volatile int i=0;  i++;）
	 *		 }
	 *		}
	 */
	private volatile boolean reconnect = true;
	private int attempts;
	
	public ConnectionWatchdog(Bootstrap b, Timer timer, int port, String host, boolean reconnect) {
		super();
		this.b = b;
		this.timer = timer;
		this.port = port;
		this.host = host;
		this.reconnect = reconnect;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("当前链路已经激活，重连尝试次数重置为0");
		attempts = 0;
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 6. 连接失败则重新设置定时器的时间
		System.out.println("ConnectionWatchdog channelInactive 链接关闭");
		if(reconnect){
			if(attempts < 12){
				attempts++;
			}	
			System.out.println("链路关闭，将进行第"+attempts+"次重连"); // 链路关闭，将进行第1次重连
			int timeout = 2 << attempts; // 重连的间隔时间会越来越长, 2 << 3 = 16;  a << x = a*2^x
			timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelInactive();
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		// 5. 定时器触发，定时连接服务器端
		ChannelFuture cf;
		synchronized (b) {
			// 5.1 根据传来bootstrap重新设置pipeline的handlers
			b.handler(new ChannelInitializer<Channel>(){

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers()); // bootstrap传过来，只需要填入handlers就可以
				}
				
			});
			// 5.2 重新连接
			cf = b.connect(host, port);
		}
		
		// 5.3 设置监听器
		cf.addListener(new ChannelFutureListener(){

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				boolean succeed = future.isSuccess();
				// 如果重连失败则调用ChannelInactive方法，再次发出重连
				if(!succeed){
					System.out.println("第"+attempts+"次重连失败"); // 第1次重连失败
					future.channel().pipeline().fireChannelInactive();
				} else {
					System.out.println("第"+attempts+"次重连成功");
				}
			}
			
		});
	}

}
