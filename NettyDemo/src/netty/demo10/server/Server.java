package netty.demo10.server;

import java.net.InetSocketAddress;

/**
 * 服务器管理接口
 */
public interface Server {
	
	public interface TransmissionProtocol{
		
	}
	
	/**
	 * 服务器使用协议
	 */
	public enum TRANSMISSION_PROTOCAL implements TransmissionProtocol{
		TCP, UDP
	}
	
	public TransmissionProtocol getTransmissionProtocol();
	
	/**
	 * 启动服务器
	 */
	public void startServer() throws Exception;
	
	public void startServer(int port) throws Exception;
	
	public void startServer(InetSocketAddress socketAddress)throws Exception;
	
	/**
	 * 关闭服务器
	 */
	public void stopServer() throws Exception;
	
	InetSocketAddress getSocketAddress();
	
	
}
