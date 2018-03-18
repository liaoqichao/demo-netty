package netty.demo7.client;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {

	/**
	 * 抽象类ConnectionWatchDog不实现这个接口，由具体类实现
	 * @return
	 * @author 廖启超
	 * @date 2018年2月22日 上午11:01:58
	 * @version
	 */
	public ChannelHandler[] handlers();
}
