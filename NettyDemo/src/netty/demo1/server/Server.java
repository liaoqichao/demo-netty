package netty.demo1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

	/**
	 * ChannelOption.SO_BACKLOG������TCP����������ʾTCP�ں�Ϊ��Ӧ�׽����Ŷӵ�������Ӹ���
	 * ��һ�����֣�
	 * TCP�ڽ�������ʱ���ͻ��˻���������˷���SYN(SYN=j)
	 * �ڶ������֣�
	 * �������˽��յ��ͻ��˵�SYN֮��ȷ�Ͽͻ��˵�SYN��ack=j+1����ͬʱ��ͻ��˷���һ��SYN����syn=k��
	 * ��SYN+ACK����Ȼ����SYN ACK���ͻ��ˡ�
	 * ��ʱ��TCP��ѿͻ������ӷŵ�A�����У�Ȼ�������SYN ACK���ͻ��ˡ�
	 * ���͸��ͻ��˺󣬻�Ѹղ���A���е����ӷŵ�B�����С�
	 * ���������֣�
	 * �ͻ����յ�SYN�󣬸��������˷���ACK��
	 * �������˽��յ�ACK����B���е�����ȡ�����Ӷ�����������֡�Ӧ�ó����accept���ء�
	 * 
	 * A���к�B���еĳ���֮����BACKLOG����A��B���г��ȴ���BACKLOG��������µ����Ӷ��ᱻTCP�ں˾ܾ���
	 * ����BACKLOG��С��accept()�ٶȻ�����ϣ�A��B�������˵����µĿͻ����޷����ӡ�
	 * 
	 * BACKLOG�Գ���֧�ֵ�����������Ӱ�죬BACKLOGӰ���ֻ�ǻ�û��acceptȡ��������
	 * 
	 * ʵ����BACKLOG��ֵ��100��
	 * 
	 * Channel���̰߳�ȫ��
	 * 
	 */
	
	private final int port;
	
	public Server(int port){
		this.port = port;
	}
	
	public static void main(String[] args) {
		
		new Server(8765).start();
		
	}

	private void start() {
		/*
		 * 1. ����2���߳��飬�ֱ����ڴ���ͻ���ͨ����accept�¼��ʹ����д
		 * 2. �󶨷����ͨ��NioServerSocketChannel
		 * 3. ����д�¼��߳�ͨ����ServerHandlerȥ���������Ķ�д
		 * 4. �����˿�
		 */
		
		// 1. ����2���߳���
		EventLoopGroup parentGroup = new NioEventLoopGroup();		// ���ڴ���������˽��տͻ������ӣ������ͻ���ͨ����accept�¼�
		EventLoopGroup childGroup = new NioEventLoopGroup();		// ��������ͨ�ţ������д���������д�¼�
		try {
			// 2.���������࣬���߳��飬�󶨶˿ڣ����ò�������childHandler
			ServerBootstrap b = new ServerBootstrap();				// �������������࣬���ڷ�����ͨ����һЩ����
			b.group(parentGroup, childGroup)						// ��2���߳�
			.channel(NioServerSocketChannel.class)					// ָ��NIOģʽ
			.option(ChannelOption.SO_BACKLOG, 1024)					// ����TCP������
			.childOption(ChannelOption.SO_SNDBUF, 32*1024)				// ���÷��ͻ����С
			.childOption(ChannelOption.SO_RCVBUF, 32*1024)				// ���ý��ջ����С
			.childOption(ChannelOption.SO_KEEPALIVE, true)				// �������ӣ�Ĭ����true
			.childHandler(new ChannelInitializer<SocketChannel>(){
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					/*
					 *  �������ý��շ����Ĵ�������ҵ���߼�������ʵ�������ServerHandler��
					 *  ���磬��������handler����������handler������ҵ���handler
					 *  
					 */
					//3.  ���ܵ����һϵ��Handler
					ch.pipeline().addLast(new ServerHandler());		
				}
				
			});
			
			/*
			 * Futrueģʽ���첽�󶨡� 
			 * ���̼߳��������ߣ���һ���̼߳����˿ڣ����߳�û������
			 */
			// 4. �󶨶˿�
			ChannelFuture cf1 = b.bind(port).sync();				// �󶨶˿ڣ���ʼ���ս���������
			
			/*
			 * ������ǰ�̣߳��Ա�֤�������˵�Channel����������
			 * �൱�� Thread.sleep(Integer.MAX_VALUE);
			 * ���ǵ�ͨ���رպ�Ҳ��֪ͨ��������
			 */
			// 5. ����cf1û���������̲߳��ܹر�
			cf1.channel().closeFuture().sync();						// �ȴ�������Socket�رա�
																	// û�����ͻ������Ӳ��ˣ���Ϊ���߳�������
			
//			ChannelFuture cf2 = b.bind(8764).sync();
//			cf2.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 6. �ͷ��߳������Դ
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();	
		}
	}
}
