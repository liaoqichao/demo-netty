package netty.demo8.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class BussinessServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String req = (String)msg;
		if("bye".equals(req)){
			ChannelFuture cf = ctx.channel().close();
			cf.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						System.out.println("连接关闭成功");
					} else{
						System.out.println("连接关闭失败");
					}
				}
				
			});
		} else{
			String resp = "你好";
			ChannelFuture cf = ctx.writeAndFlush(Unpooled.copiedBuffer(resp.getBytes(CharsetUtil.UTF_8)));
			cf.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						System.out.println("服务器端响应客户端成功");
					} else{
						System.out.println("服务器端响应客户端失败");
					}
				}
				
			});
		}
	}

	
}
