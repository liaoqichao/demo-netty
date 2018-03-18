package netty.demo10.server;

import java.net.InetSocketAddress;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 用于配置server
 */
public class NettyConfig {
	
	private Map<ChannelOption<?>, Object> channelOptions;
	
	/**
	 * reactor多线程模型中的acceptor
	 */
	private NioEventLoopGroup parentGroup;
	
	/**
	 * reactor多线程模型中的ThreadPool
	 */
	private NioEventLoopGroup childGroup;
	
	/**
	 * parentGroup的线程数
	 */
	private int parentThreadCount;
	
	/**
	 * childGroup的线程数
	 */
	private int childThreadCount;
	
	private InetSocketAddress socketAddress;
	
	private int portNumber = 12345;
	
	protected ChannelInitializer<Channel> channelInitailizer;

	public Map<ChannelOption<?>, Object> getChannelOptions() {
		return channelOptions;
	}

	public void setChannelOption(Map<ChannelOption<?>, Object> channelOptions) {
		this.channelOptions = channelOptions;
	}

	public synchronized NioEventLoopGroup getParentGroup() {
		if(null == parentGroup){
			if(0 >= parentThreadCount){
				parentGroup = new NioEventLoopGroup();
			} else{
				parentGroup = new NioEventLoopGroup(parentThreadCount);
			}
		}
		return parentGroup;
	}
	public synchronized void setParentGroup(NioEventLoopGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public synchronized NioEventLoopGroup getChildGroup() {
		if(null == childGroup){
			if(0 >= childThreadCount){
				childGroup = new NioEventLoopGroup();
			} else{
				childGroup = new NioEventLoopGroup(childThreadCount);
			}
		}
		return childGroup;
	}

	public synchronized void setChildGroup(NioEventLoopGroup childGroup) {
		this.childGroup = childGroup;
	}

	public int getParentThreadCount() {
		return parentThreadCount;
	}

	public void setParentThreadCount(int parentThreadCount) {
		this.parentThreadCount = parentThreadCount;
	}

	public int getChildThreadCount() {
		return childThreadCount;
	}

	public void setChildThreadCount(int childThreadCount) {
		this.childThreadCount = childThreadCount;
	}

	public synchronized InetSocketAddress getSocketAddress() {
		if(null == socketAddress){
			socketAddress = new InetSocketAddress(portNumber);
		}
		return socketAddress;
	}

	public synchronized void setSocketAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	
	
}
