package netty.demo10.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppContext implements ApplicationContextAware {

	public static final String TCP_SERVER = "tcpServer";
	
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
	
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.applicationContext = arg0;
	}

	public static Object getBean(String name){
		if(null == name){
			return null;
		}
		return applicationContext.getBean(name);
	}
}
