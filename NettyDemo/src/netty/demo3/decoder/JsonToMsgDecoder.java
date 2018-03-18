package netty.demo3.decoder;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.sf.json.JSONObject;
import netty.demo3.pojo.Message;

public class JsonToMsgDecoder extends MessageToMessageDecoder<String> {

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		Message message = (Message) JSONObject.toBean(JSONObject.fromObject(msg), Message.class); 
//		System.out.println("MessageToMessageDecoder : "+message.toString());
		out.add(message);
//		ctx.fireChannelRead(message);
		System.out.println("svr:2");
		System.out.println("cli:4");
	}

}
