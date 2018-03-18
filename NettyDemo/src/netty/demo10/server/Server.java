package netty.demo10.server;

import java.net.InetSocketAddress;

/**
 * ����������ӿ�
 */
public interface Server {
	
	public interface TransmissionProtocol{
		
	}
	
	/**
	 * ������ʹ��Э��
	 */
	public enum TRANSMISSION_PROTOCAL implements TransmissionProtocol{
		TCP, UDP
	}
	
	public TransmissionProtocol getTransmissionProtocol();
	
	/**
	 * ����������
	 */
	public void startServer() throws Exception;
	
	public void startServer(int port) throws Exception;
	
	public void startServer(InetSocketAddress socketAddress)throws Exception;
	
	/**
	 * �رշ�����
	 */
	public void stopServer() throws Exception;
	
	InetSocketAddress getSocketAddress();
	
	
}
