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
 * Sharableע�⣬��ʾ���ChannelPipeline�̰߳�ȫ��ʹ��ͬһ��Handler
 */
@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

	private final Bootstrap b;
	private final Timer timer;
	private final int port;
	private final String host;
	/**
	 * �ڴ�Ŀɼ��ԣ�
	 * JVM��Ϊÿ���̷߳�һ������ڴ棬�������Ч�ʣ����a�߳��޸���a�̱߳���i��ֵ��
	 * b�߳��޷���ȡa�̱߳���i������ֵ��������ڴ治�ɼ���
	 * 
	 * A�߳�ִ������
	 * 1. ��ʼ��flagֵΪfalse
	 * 2. ����200ms
	 * 3. ��flagֵ����Ϊtrue
	 * 4. ���flag��ֵ
	 * 
	 * main�̣߳�
	 * 1. ����A�߳�
	 * 2. ��ѭ������ѭ�����ж�A�߳�flag��ֵ�����Ϊtrue���"aaa"Ȼ������ѭ����
	 * 
	 * ���н����true��A�̵߳��Ĳ������, ��ʹflagΪtrue֮�����߳�Ҳ�����aaa��
	 * ԭ����ѭ����while(true){}��Ч��̫�ߣ�ʹ��main�߳�û��ʱ���������л�ȡflag��ֵ��ÿ�ζ�����main�߳��л����ֵ
	 * 	����ʼ��flag��ֵ�������У�main�̻߳�ȡ����flag��ֵ�ŵ����̵߳Ļ����У���������ж�ǰ
	 * ���һЩ��䣬����sysout("bbb")��ʹ��while(true)��task.isFlag()��ʱ����Щ�ӳ٣�main�߳̾���ʱ��
	 * �������л�ȡflag��ֵ���ͻ����aaa��
	 * 	while(true){
	 * 		if(task.isFlag()){sysout("aaa");}
	 * 	}
	 * 
	 * �����������������A�߳��޸���ֵ������main�̵߳õ��Ļ��Ǿ�ֵ����������������ڴ治�ɼ�����
	 * 
	 * 								+--------------+
	 * 								|	  ���� 	   |
	 * 								| flag = false |
	 * 								| 3.flag=true  |
	 * 								+--------------+
	 * 			+-----------------+					+----------------+
	 * 			|  �߳�A			  |					|	main�߳�		 |
	 *   		| 1. flag = false |					|	flag = false |
	 *   		| 2. flag = true  |					|				 |
	 *   		+-----------------+					+----------------+
	 * 
	 * Java��ͬ�����Ʒֳ�2���֣�synchronized��ͬ�����ͬ����������volatile��adj.�ױ�ģ����ȶ��ģ���
	 * ����volatile������ͬ���Խϲ����ʱ�����򵥲��ҿ������ͣ���������ʹ��Ҳ�����׳���
	 * 
	 * ���ṩ��2����Ҫ���ԣ������ԺͿɼ��ԡ������Լ�һ��ֻ����һ���̳߳���ĳ���ض���������˿���ʹ�ø�����ʵ��
	 * �Թ������ݵ�Э������Э�飬����һ��ֻ��һ���߳��ܹ�ʹ�øù������ݡ��ɼ��Լ�ȷ�����ͷ���֮ǰ�Թ�����������
	 * �ĸ��Ķ�������������߳��ǿɼ��ġ�
	 * 
	 * volatile����synchronized�Ŀɼ��ԣ������߱�ԭ���ԡ������˵�߳��ܹ��Զ�����volatile����������ֵ��
	 * 
	 * volatile��������һ������߳�����
	 * 
	 * ��һ����������Ϊ volatile ֮�󣬽��߱��������ԣ�
����	 *  1.��֤�˱��������е��̵߳Ŀɼ��ԣ�����ġ��ɼ��ԡ����籾�Ŀ�ͷ��������һ���߳��޸������������ֵ��
	 * 	 volatile ��֤����ֵ������ͬ�������ڴ棬�Լ�ÿ��ʹ��ǰ���������ڴ�ˢ�¡�����ͨ������������㣬
	 *   ��ͨ������ֵ���̼߳䴫�ݾ���Ҫͨ�����ڴ棨�����Java�ڴ�ģ�ͣ�����ɡ�
����	 *  2.��ָֹ���������Ż�����volatile���εı�������ֵ���ִ����һ����load addl $0x0, (%esp)��������
	 * 	 ��������൱��һ���ڴ����ϣ�ָ��������ʱ���ܰѺ����ָ���������ڴ�����֮ǰ��λ�ã���ֻ��һ��CPU�����ڴ�ʱ��
	 * 	 ������Ҫ�ڴ����ϣ���ʲô��ָ����������ָCPU��������������ָ�������涨��˳��ֿ����͸�����Ӧ��·��Ԫ������
	 * 
	 * ��ȷʹ��volatile������������
	 * 	1. �Ա���д�Ĳ�������������ǰֵ��������volatile int i=0;  i++;��
	 *  2. �ñ���û�а����ھ������������Ĳ���ʽ(Invariants)�С�������volatile int end = 5; if(start < end){...}��
	 *  
	 *  ���ܣ�
	 *  volatile�����Ķ������ͷ�volatile�����Ķ��������ܼ���һ������volatile����д���������ȷ�volatile�����Ĵ�
	 *  ��ΪҪ��֤�ɼ�����Ҫʵ���ڴ�綨��Memory Fence������ʹ��ˣ�volatile���ܿ�����Ȼ������ȡ�͡�
	 *  
	 *  volatile �Ķ�������������ͨ����������ͬ������д������������Ϊ����Ҫ�ڱ��ش����в�������ڴ�����ָ��
	 *  ����֤����������������ִ�С���ʲô��ָ����������ָCPU��������������ָ�������涨��˳��ֿ����͸�����Ӧ��·��Ԫ����
	 *  
	 *  volatile ������������һ�������������ˣ����ܹ���ȫʹ�� volatile ������£�volatile �����ṩһЩ�������Ŀ��������ԡ�
	 *  ����������Ĵ���ҪԶԶ����д������������ȣ�volatile ����ͨ���ܹ�����ͬ�������ܿ�����
	 *  
	 *  ʹ�����ӣ�
	 *  1. ״̬��־��
	 *  	volatile boolean shutdownRequested = false;
	 *  	public void shutdown(){shutdownRequested = true;}
	 *  	public void doWork(){while(!shutdownRequested){...;}}
	 *  	shutdown()��������������һ���߳��ⲿ���õ�
	 *  
	 *  	�ܿ��ܻ��ѭ���ⲿ���� shutdown() ���� ���� ������һ���߳��� ���� ��ˣ�
	 *  	��Ҫִ��ĳ��ͬ����ȷ����ȷʵ�� shutdownRequested �����Ŀɼ��ԡ������ܻ�� JMX ��������GUI �¼�
	 *  	�߳��еĲ�����������ͨ�� RMI ��ͨ��һ�� Web ����ȵ��ã���Ȼ����ʹ��synchronized ���дѭ��Ҫ��
	 *  	ʹ��volatile ״̬��־��д�鷳�ܶࡣ���� volatile ���˱��룬����״̬��־����������
	 *  	�������κ�����״̬����˴˴��ǳ��ʺ�ʹ�� volatile��
	 *  
	 *  	�������͵�״̬��ǵ�һ�����������ǣ�ͨ��ֻ��һ��״̬ת����shutdownRequested ��־�� false ת��Ϊ true��
	 *  	Ȼ�����ֹͣ������ģʽ������չ������ת����״̬��־������ֻ����ת�����ڲ������������²�����չ��
	 *  	�� false �� true����ת���� false�������⣬����ҪĳЩԭ��״̬ת�����ƣ�����ԭ�ӱ�����
	 *  
	 *  	ԭ�ӱ���ͨ��ԭ�Ӳ�������������ʵ�֣����ַ�ʽҲ����CAS,��������ԭ����ĵײ�ʵ�ַ�ʽ��
	 *  	Ҳ��java���ĵײ�ʵ�ַ�ʽ��������������ֵ֮ǰ�жϵ�ǰ�Ƿ�����ֵ���������������ֵ��
	 *  	������������¼�����ֵ���ٳ������á�
	 *  
	 *  2. һ���԰�ȫ���� 
	 *  	ȱ��ͬ���ᵼ���޷�ʵ�ֿɼ��ԣ���ʹ��ȷ����ʱд��������ö�����ԭ��ֵ��ø������ѡ�
	 *  	��ȱ��ͬ��������£����ܻ�����ĳ���������õĸ���ֵ������һ���߳�д�룩�͸ö���״̬�ľ�ֵͬʱ���ڡ�
	 *  	����������������˫�ؼ��������double-checked-locking������ĸ�Դ��
	 *  	���ж���������û��ͬ��������½��ж������������������������ܻῴ��һ�����µ����ã�
	 *  	������Ȼ��ͨ�������ÿ�������ȫ����Ķ��󣩡�
	 *  
	 *  	����ģʽ����һ��ʵ�ַ�ʽ����˫������Demo��Ŀ��SingleDoubleLock��
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
	 *  	��� theFlooble ���ò��� volatile ���ͣ�doWork() �еĴ����ڽ���� theFlooble ������ʱ��
	 *  	����õ�һ������ȫ����� Flooble��
	 * 		��ģʽ��һ����Ҫ�����ǣ��������Ķ���������̰߳�ȫ�ģ�
	 * 		��������Ч�Ĳ��ɱ������Ч���ɱ���ζ�Ŷ����״̬�ڷ���֮����Զ���ᱻ�޸ģ���
	 * 		volatile ���͵����ÿ���ȷ������ķ�����ʽ�Ŀɼ��ԣ�������������״̬�ڷ����󽫷������ģ�
	 * 		��ô����Ҫ�����ͬ����
	 * 	3. �����۲�
	 * 		��ȫʹ�� volatile ����һ�ּ�ģʽ�ǣ����� �������� �۲����������ڲ�ʹ�á�
	 * 		���磬������һ�ֻ����������ܹ��о������¶ȡ�һ����̨�߳̿��ܻ�ÿ�������ȡһ�θô�������
	 * 		�����°�����ǰ�ĵ��� volatile ������Ȼ�������߳̿��Զ�ȡ����������Ӷ���ʱ�ܹ��������µ��¶�ֵ��
	 * 		��ģʽ��ǰ��ģʽ����չ����ĳ��ֵ�������ڳ����ڵ������ط�ʹ�ã�������һ�����¼��ķ�����ͬ������һϵ�ж����¼���
	 * 		���ģʽҪ�󱻷�����ֵ����Ч���ɱ�� ���� ��ֵ��״̬�ڷ����󲻻���ġ�ʹ�ø�ֵ�Ĵ�����Ҫ�����ֵ������ʱ�����仯��
	 *  4. volatile bean
	 *  	volatile bean����JavaBean�����б���������volatile��
	 *  	���ڶ������õ����ݳ�Ա�����õĶ����������Ч���ɱ�ġ����⽫��ֹ��������ֵ�����ԣ�
	 *  	��Ϊ���������ñ�����Ϊ volatile ʱ��ֻ�����ö��������鱾����� volatile ���壩
	 *  5. �����ϵ͵Ķ�-д������
	 *  	ǰ�᣺������ԶԶ����д������
	 *  	@ThreadSafe
	 *		public class CheesyCounter {
	 *		 // Employs the cheap read-write lock trick
	 *		 // All mutative operations MUST be done with the 'this' lock held
	 *		 @GuardedBy("this") private volatile int value;
	 *		
	 *		public int getValue() { return value; }
	 *		
	 *		public synchronized int increment() {
	 *		 return value++; // ����ͬ����1. �Ա���д�Ĳ�������������ǰֵ��������volatile int i=0;  i++;��
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
		System.out.println("��ǰ��·�Ѿ�����������Դ�������Ϊ0");
		attempts = 0;
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 6. ����ʧ�����������ö�ʱ����ʱ��
		System.out.println("ConnectionWatchdog channelInactive ���ӹر�");
		if(reconnect){
			if(attempts < 12){
				attempts++;
			}	
			System.out.println("��·�رգ������е�"+attempts+"������"); // ��·�رգ������е�1������
			int timeout = 2 << attempts; // �����ļ��ʱ���Խ��Խ��, 2 << 3 = 16;  a << x = a*2^x
			timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelInactive();
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		// 5. ��ʱ����������ʱ���ӷ�������
		ChannelFuture cf;
		synchronized (b) {
			// 5.1 ���ݴ���bootstrap��������pipeline��handlers
			b.handler(new ChannelInitializer<Channel>(){

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers()); // bootstrap��������ֻ��Ҫ����handlers�Ϳ���
				}
				
			});
			// 5.2 ��������
			cf = b.connect(host, port);
		}
		
		// 5.3 ���ü�����
		cf.addListener(new ChannelFutureListener(){

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				boolean succeed = future.isSuccess();
				// �������ʧ�������ChannelInactive�������ٴη�������
				if(!succeed){
					System.out.println("��"+attempts+"������ʧ��"); // ��1������ʧ��
					future.channel().pipeline().fireChannelInactive();
				} else {
					System.out.println("��"+attempts+"�������ɹ�");
				}
			}
			
		});
	}

}
