package util;

/**
 * cmd调用protoc，通过.proto文件生成.java文件
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
	 * @author 廖启超
	 * @date 2018年2月9日 下午4:33:35
	 * @version
	 */
	public static void generateClass(String protoDir, String destDir, String protoFile){
		/*
		 * protoc.exe -I=proto的输入目录 --java_out=java类输出目录 proto的输入目录包括包括proto文件
		 * 如果目录不存在就会报错
		 * 
		 * CMD下输入
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
