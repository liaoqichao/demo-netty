package netty.demo9.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		System.out.println(getBody(msg.content()));
	}

	private String getBody(ByteBuf content) {
		return (Unpooled.copiedBuffer(content)).toString(CharsetUtil.UTF_8);
	}

}
