package netty.demo3.encoder;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.sf.json.JSONObject;
import netty.demo3.pojo.Message;

public class MsgToJsonEncoder extends MessageToMessageEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
//		ctx.channel().writeAndFlush(JSONObject.fromObject(msg).toString()); // 不flush不执行下一个handler
//		System.out.println("MsgToJsonEncoder : "+JSONObject.fromObject(msg).toString());
		System.out.println("cli:1");
		System.out.println("svr:4");
//		ctx.writeAndFlush(JSONObject.fromObject(msg).toString());   // encoder是写，只能write， fireChannelRead是读
//		out.add(JSONObject.fromObject(msg).toString());	// out.add()不会触发写事件，进入到下一个handler
		ctx.channel().writeAndFlush(JSONObject.fromObject(msg).toString());
	}

}
