package netty.demo10.server;

import java.util.Map;
import java.util.Set;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyTCPServer extends AbstractNettyServer {

	private ServerBootstrap b;
	
	public NettyTCPServer(NettyConfig nettyConfig, ChannelInitializer<Channel> channelInitializer) {
		super(nettyConfig, channelInitializer);
	}

	@Override
	public void setChannelInitializer(ChannelInitializer<Channel> initializer) {
		this.channelInitializer = initializer;
		b.childHandler(channelInitializer);
	}

	@Override
	public void startServer() throws Exception {
		try {
			b = new ServerBootstrap();
			Map<ChannelOption<?>, Object> channelOptions = nettyConfig.getChannelOptions();
			if(null != channelOptions){
                Set<ChannelOption<?>> keySet = channelOptions.keySet();
                // ªÒ»°configuration≈‰÷√µΩchannelOption
                for(ChannelOption option : keySet){
                    b.option(option, channelOptions.get(option));
                }
			}
			b.group(getParentGroup(), getChildGroup())
			.channel(NioServerSocketChannel.class)
			.childHandler(getChannelInitializer());
			
			Channel serverChannel = b.bind(nettyConfig.getSocketAddress()).channel();
			ALL_CHANNELS.add(serverChannel);
		} catch (Exception e) {
			e.printStackTrace();
			super.stopServer();
		}
	}
	
	@Override
	public TransmissionProtocol getTransmissionProtocol(){
		return TRANSMISSION_PROTOCAL.TCP;
	}

}
