package netty.demo5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.demo5.pojo.MessageEntity;

public class AckServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("服务器响应处理器激活");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("服务器响应处理器读完");
		MessageEntity.Message message = (MessageEntity.Message)msg;
		String snd = message.getSnd();
		System.out.println(snd);
		
		
		MessageEntity.Message.Builder builder = MessageEntity.Message.newBuilder();
		builder.setRcv("你好，"+message.getUsername());
		builder.setUsername(message.getUsername());
		builder.setPsw(message.getPsw());
		builder.setValidateCode(message.getValidateCode());
		MessageEntity.Message response = builder.build();
				
		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	
}
