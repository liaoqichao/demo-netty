package netty.demo10.server;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public abstract class AbstractNettyServer implements NettyServer {

	public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);
	
	protected final NettyConfig nettyConfig;
	
	protected  ChannelInitializer<Channel> channelInitializer;
	
	public AbstractNettyServer(NettyConfig nettyConfig, ChannelInitializer<Channel> channelInitializer) {
		this.nettyConfig = nettyConfig;
		this.channelInitializer = channelInitializer;
	}

	@Override
	public void startServer(int port) throws Exception {
		nettyConfig.setPortNumber(port);
		nettyConfig.setSocketAddress(new InetSocketAddress(port));
		startServer();
	}

	@Override
	public void startServer(InetSocketAddress socketAddress) throws Exception {
		nettyConfig.setSocketAddress(socketAddress);
	}

	@Override
	public void stopServer() throws Exception {
		ChannelGroupFuture f = ALL_CHANNELS.close();
		try {
			f.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null != nettyConfig.getParentGroup()){
				nettyConfig.getParentGroup().shutdownGracefully();
			}
			if(null != nettyConfig.getChildGroup()){
				nettyConfig.getChildGroup().shutdownGracefully();
			}
		}
	}
	
	@Override
	public InetSocketAddress getSocketAddress() {
		return nettyConfig.getSocketAddress();
	}

	@Override
	public ChannelInitializer<Channel> getChannelInitializer() {
		return channelInitializer;
	}

	@Override
	public NettyConfig getNettyConfig() {
		return nettyConfig;
	}

	protected EventLoopGroup getParentGroup(){
		return nettyConfig.getParentGroup();
	}
	
	protected EventLoopGroup getChildGroup(){
		return nettyConfig.getChildGroup();
	}
}
