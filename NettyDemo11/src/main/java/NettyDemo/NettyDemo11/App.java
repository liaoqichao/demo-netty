package NettyDemo.NettyDemo11;

import NettyDemo.NettyDemo11.chatroom.WebChatServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
    	new WebChatServer(12345).start();
    }
}
