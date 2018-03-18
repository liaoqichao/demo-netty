package netty.demo4.pojo;

import com.google.protobuf.InvalidProtocolBufferException;

import netty.demo4.pojo.PersonEntity.Person;

public class Demo {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		PersonEntity.Person.Builder builder  = PersonEntity.Person.newBuilder();
		builder.setId(1);								// 填写必填项
		/*
		 *  如果不填名称抛异常：
		 *  Exception in thread "main" com.google.protobuf.UninitializedMessageException: Message missing required fields: name
		 */
		builder.setName("张三");							// 填写必填项	
//		builder.setEmail("enjoy_the_game@163.com");		// 填写可填项
		
		Person person = builder.build();				// 创建对象
		
		byte[] bytes = person.toByteArray();			// 序列化成字节数组,Person -> 字节数组
		for (byte b : bytes) {
			System.out.print(b);
		}
		System.out.println();
		
		Person p = Person.parseFrom(bytes);				// 字节数组反序列化成对象， 字节数组 -> Person
		System.out.println("id="+p.getId() + ", name="+p.getName()+", email="+p.getEmail());
		
		
		
	}
}
