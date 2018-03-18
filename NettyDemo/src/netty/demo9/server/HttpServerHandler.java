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
//		System.out.println("�ͻ��ˣ�"+ctx.channel().remoteAddress());
//		ctx.writeAndFlush("�ͻ���"+InetAddress.getLocalHost().getHostName()+"�ɹ���������������ӣ�");
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
			result = "δ֪����";
			send(ctx, result, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		FullHttpRequest req = (FullHttpRequest)msg;
		try {
			String path = req.uri();	// ����·��
			String body = getBody(req);	// ��ȡ����
			HttpMethod method = req.method();	// ����ʽ
			if(!path.contains("demo9")){
				result = "�Ƿ�����";
				send(ctx, result, HttpResponseStatus.BAD_REQUEST);
				return;
			}
			System.out.println("���յ�"+method.name()+"����");
			if(HttpMethod.GET.equals(method)){
				// ����ҵ��..
				System.out.println(path.substring(path.indexOf("?")+1));
				result = "GET����";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.POST.equals(method)){
				System.out.println("body:"+body);
				result = "POST����";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.PUT.equals(method)){
				System.out.println("body:"+body);
				result = "PUT����";
				send(ctx, result, HttpResponseStatus.OK);
			} else if(HttpMethod.DELETE.equals(method)){
				System.out.println("body:"+body);
				result = "DELETE����";
				send(ctx, result, HttpResponseStatus.OK);
			}
		} catch(Exception e){
			System.err.println("������ʧ��");
			e.printStackTrace();
		} finally {
			req.release();
		}
	}
	
	/**
	 * ��ȡbody����
	 * @param req
	 * @return
	 * @author ������
	 * @date 2018��2��23�� ����4:06:02
	 * @version
	 */
	private String getBody(FullHttpRequest req) {
		ByteBuf buf = req.content();
		return buf.toString(CharsetUtil.UTF_8);
	}

	/**
	 * ��Ӧ
	 * @param ctx
	 * @param result2
	 * @param badRequest
	 * @author ������
	 * @date 2018��2��23�� ����3:55:20
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
