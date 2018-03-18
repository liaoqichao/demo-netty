package NettyDemo.NettyDemo11.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// 进入聊天室
		channels.add(ctx.channel());
		// 发送消息
		for (Channel channel : channels) {
			if(channel != ctx.channel()) {
				channel.writeAndFlush(new TextWebSocketFrame("["+ctx.channel().remoteAddress()+"]欢迎进入聊天室"));
			}
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 进入聊天室
		channels.remove(ctx.channel());
		// 发送消息
		for (Channel channel : channels) {
			if(channel != ctx.channel()) {
				channel.writeAndFlush(new TextWebSocketFrame("["+ctx.channel().remoteAddress()+"]离开聊天室"));
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			if(channel != incoming) {
				channel.writeAndFlush(new TextWebSocketFrame("用户"+incoming.remoteAddress()+":"+msg.text()));
			} else {
				channel.writeAndFlush(new TextWebSocketFrame("我:"+msg.text()));
			}
		}
	}

}
