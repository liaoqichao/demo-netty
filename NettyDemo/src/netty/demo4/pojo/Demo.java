package netty.demo4.pojo;

import com.google.protobuf.InvalidProtocolBufferException;

import netty.demo4.pojo.PersonEntity.Person;

public class Demo {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		PersonEntity.Person.Builder builder  = PersonEntity.Person.newBuilder();
		builder.setId(1);								// ��д������
		/*
		 *  ��������������쳣��
		 *  Exception in thread "main" com.google.protobuf.UninitializedMessageException: Message missing required fields: name
		 */
		builder.setName("����");							// ��д������	
//		builder.setEmail("enjoy_the_game@163.com");		// ��д������
		
		Person person = builder.build();				// ��������
		
		byte[] bytes = person.toByteArray();			// ���л����ֽ�����,Person -> �ֽ�����
		for (byte b : bytes) {
			System.out.print(b);
		}
		System.out.println();
		
		Person p = Person.parseFrom(bytes);				// �ֽ����鷴���л��ɶ��� �ֽ����� -> Person
		System.out.println("id="+p.getId() + ", name="+p.getName()+", email="+p.getEmail());
		
		
		
	}
}
