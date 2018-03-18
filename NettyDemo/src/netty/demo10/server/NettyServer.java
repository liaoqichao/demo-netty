package netty.demo10.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * 服务器初始化配置接口
 */
public interface NettyServer extends Server {
	
	/**
	 * ServerBootstrap创建成功后会有一个ChannelInitializer，本方法用于获取ChannelInitializer
	 */
	public ChannelInitializer<Channel> getChannelInitializer();
	
	/**
	 * 设置initializer
	 */
	public void setChannelInitializer(ChannelInitializer<Channel> initializer);
	
	/** 
	 * 获取netty server的配置
	 */
	public NettyConfig getNettyConfig();
}
