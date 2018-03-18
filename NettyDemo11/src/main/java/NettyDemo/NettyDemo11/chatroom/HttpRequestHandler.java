package NettyDemo.NettyDemo11.chatroom;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedNioFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements ChannelHandler {

	private String chatUri;
	
	private File indexFile;
	
	public HttpRequestHandler(String chatUri) throws URISyntaxException {
		String srcRootPath = this.getClass().getResource("/").getPath();
		indexFile = new File(srcRootPath+"source/index.html");
		this.chatUri = chatUri;
	}
	

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String reqUri = request.uri().toString();
		// 和前台js的websocket请求uri匹配，chat后面不要加"/"，否则equals不成立
		if(chatUri.equals(reqUri)) {
			System.out.println("websocket请求");
			/*
			 * netty中使用引用计数机制来管理资源,当一个实现ReferenceCounted的对象实例化时,引用计数置1.
			 * 客户代码中需要保持一个该对象的引用时需要调用接口的retain方法将计数增1.对象使用完毕时调用release将计数减1.
			 * 当引用计数变为0时,对象将释放所持有的底层资源或将资源返回资源池.
			 * 
			 * 按上述规则使用Direct和Pooled的ByteBuf尤其重要.对于DirectBuf,其内存不受VM垃圾回收控制只有在调用release导致计数为0时才会主动释放内存,而PooledByteBuf只有在release后才能被回收到池中以循环利用.
			 * 如果客户代码没有按引用计数规则使用这两种对象,将会导致内存泄露.
			 */
			ctx.fireChannelRead(request.retain()); // ByteBuf.retain 指针拷贝，copy是内容拷贝
		} else {
			System.out.println("http请求，读取Index页面并发送给客户端浏览器");
			if(HttpUtil.is100ContinueExpected(request)) {
				// 100 - Continue 初始的请求已经接受，客户应当继续发送请求的其余部分。（HTTP 1.1新） 
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
						HttpResponseStatus.CONTINUE);
				ctx.writeAndFlush(response);
			}
			// 读取默认的index.html
			try(RandomAccessFile file = new RandomAccessFile(indexFile, "r")){
				// 1. 设置Http响应头, 不能是FullHttpResponse
				HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
						HttpResponseStatus.OK);
				// text/html才会被浏览器解析成页面，text/plain不解析页面，只显示Html代码
				response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
				
				// 不加页面会一直转圈圈
				boolean keepAlive = HttpUtil.isKeepAlive(request);
				if(keepAlive) {
					response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
					response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				}
				ctx.write(response);
				// 2. 将html响应到客户端
				ctx.write(new ChunkedNioFile(file.getChannel()));
				// 3. The 'end of content' marker in chunked encoding.
				ChannelFuture cf = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
				
				if(!keepAlive) {
					cf.addListener(ChannelFutureListener.CLOSE);
				}
				
				file.close();
			}
			
		}
	}

}
