package netty.demo9.server;

import java.net.InetAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	
	private String result = "";
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("客户端："+ctx.channel().remoteAddress());
//		ctx.writeAndFlush("客户端"+InetAddress.getLocalHost().getHostName()+"成功与服务器建立连接！");
//		ctx.fireChannelActive();
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(!(msg instanceof FullHttpRequest)){
			result = "未知请求";
			send(ctx, result, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		FullHttpRequest req = (FullHttpRequest)msg;
		try {
			String path = req.uri();	// 请求路径
			String body = getBody(req);	// 获取参数
			HttpMethod method = req.method();	// 请求方式
			if(!path.contains("demo9")){
				result = "非法请求";
				send(ctx, result, HttpResponseStatus.BAD_REQUEST);
				return;
			}
			System.out.println("接收到"+method.name()+"请求");
			if(HttpMethod.GET.equals(method)){
				// 处理业务..
				System.out.println(path.substring(path.indexOf("?")+1));
				result = "GET请求";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.POST.equals(method)){
				System.out.println("body:"+body);
				result = "POST请求";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.PUT.equals(method)){
				System.out.println("body:"+body);
				result = "PUT请求";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.DELETE.equals(method)){
				System.out.println("body:"+body);
				result = "DELETE请求";
				send(ctx, result, HttpResponseStatus.OK);
			}
		} catch(Exception e){
			System.err.println("请求处理失败");
			e.printStackTrace();
		} finally {
			req.release();
		}
	}
	
	/**
	 * 获取body参数
	 * @param req
	 * @return
	 * @author 廖启超
	 * @date 2018年2月23日 下午4:06:02
	 * @version
	 */
	private String getBody(FullHttpRequest req) {
		ByteBuf buf = req.content();
		return buf.toString(CharsetUtil.UTF_8);
	}

	/**
	 * 响应
	 * @param ctx
	 * @param result2
	 * @param badRequest
	 * @author 廖启超
	 * @date 2018年2月23日 下午3:55:20
	 * @version
	 */
	private void send(ChannelHandlerContext ctx, String result2, HttpResponseStatus status) {
		FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(result2.getBytes(CharsetUtil.UTF_8)));
		resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	
}
