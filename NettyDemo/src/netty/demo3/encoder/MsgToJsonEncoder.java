package netty.demo3.encoder;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.sf.json.JSONObject;
import netty.demo3.pojo.Message;

public class MsgToJsonEncoder extends MessageToMessageEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
//		ctx.channel().writeAndFlush(JSONObject.fromObject(msg).toString()); // ��flush��ִ����һ��handler
//		System.out.println("MsgToJsonEncoder : "+JSONObject.fromObject(msg).toString());
		System.out.println("cli:1");
		System.out.println("svr:4");
//		ctx.writeAndFlush(JSONObject.fromObject(msg).toString());   // encoder��д��ֻ��write�� fireChannelRead�Ƕ�
//		out.add(JSONObject.fromObject(msg).toString());	// out.add()���ᴥ��д�¼������뵽��һ��handler
		ctx.channel().writeAndFlush(JSONObject.fromObject(msg).toString());
	}

}
