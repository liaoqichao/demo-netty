package util;

/**
 * cmd����protoc��ͨ��.proto�ļ�����.java�ļ�
 * @author LQC
 *
 */
public class ProtobufUtil {

	public static void main(String[] args) {
		// demo4
//		generateClass("E:/Eclipse/jee-neon workspace/NettyDemo/proto",
//				"E:/Eclipse/jee-neon workspace/NettyDemo/src/netty/demo4/pojo",
//				"E:/Eclipse/jee-neon workspace/NettyDemo/proto/person-entity.proto");
		
		// demo5
		generateClass("E:/Eclipse/jee-neon workspace/NettyDemo/proto",
				"E:/Eclipse/jee-neon workspace/NettyDemo/src/netty/demo7/pojo",
				"E:/Eclipse/jee-neon workspace/NettyDemo/proto/pkg-entity.proto");
	}
	
	/**
	 * 
	 * @param protoFile 
	 * @author ������
	 * @date 2018��2��9�� ����4:33:35
	 * @version
	 */
	public static void generateClass(String protoDir, String destDir, String protoFile){
		/*
		 * protoc.exe -I=proto������Ŀ¼ --java_out=java�����Ŀ¼ proto������Ŀ¼��������proto�ļ�
		 * ���Ŀ¼�����ھͻᱨ��
		 * 
		 * CMD������
		 * E:/protobuf/protobuf-master/src/protoc.exe -I="E:\Eclipse\jee-neon workspace\NettyDemo\proto" --java_out="E:\Eclipse\jee-neon workspace\NettyDemo\src\netty\demo4\pojo" "E:\Eclipse\jee-neon workspace\NettyDemo\proto/person-entity.proto"
		 */
		String cmd = "E:/protobuf/protobuf-master/src/protoc.exe -I="+"\""+protoDir+"\" "+"--java_out=\""+destDir+"\" "+"\""+protoFile+"\"";
		System.out.println(cmd);
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
