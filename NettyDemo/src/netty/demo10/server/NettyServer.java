package netty.demo10.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * ��������ʼ�����ýӿ�
 */
public interface NettyServer extends Server {
	
	/**
	 * ServerBootstrap�����ɹ������һ��ChannelInitializer�����������ڻ�ȡChannelInitializer
	 */
	public ChannelInitializer<Channel> getChannelInitializer();
	
	/**
	 * ����initializer
	 */
	public void setChannelInitializer(ChannelInitializer<Channel> initializer);
	
	/** 
	 * ��ȡnetty server������
	 */
	public NettyConfig getNettyConfig();
}
