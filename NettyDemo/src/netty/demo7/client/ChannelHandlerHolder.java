package netty.demo7.client;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {

	/**
	 * ������ConnectionWatchDog��ʵ������ӿڣ��ɾ�����ʵ��
	 * @return
	 * @author ������
	 * @date 2018��2��22�� ����11:01:58
	 * @version
	 */
	public ChannelHandler[] handlers();
}
