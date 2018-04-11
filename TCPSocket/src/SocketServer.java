import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class SocketServer {
    final static int SERVER_PORT = 7777;
 
      // 서버에 접속한 클라이언트를 HashMap에 저장하여 관리한다.
    HashMap clients;
	
	DataInputStream in;
    DataOutputStream out;
 
    public SocketServer() {
        clients = new HashMap();
        Collections.synchronizedMap(clients);    // 동기화 처리
    }
 
    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;
 
        try {
        	
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server Started");
 
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Login try from [" + socket.getInetAddress() + ":" + socket.getPort() + "]");
 
                ClientHandler thread = new ClientHandler(socket,in,out);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 

    void sendToAll(String msg) 
    {
    	Iterator it = clients.keySet().iterator();
    	 
    	while (it.hasNext()) 
    	{
    		try 
    	    {
    			DataOutputStream out = (DataOutputStream) clients.get(it.next());
    	        out.writeUTF(msg);
    	    } 
    	        catch (Exception e) 
    	        {
    	        }
    	}
    }

    public static void main(String[] args) 
    {
        new SocketServer().start();
    }
  
  // 멀티채팅서버의 ServerReceiver쓰레드는 클라이언트가 추가될 때마다 생성되며
    // 클라이언트의 입력을 서버에 접속된 모든 클라이언트에게 전송하는 일을 한다.
   
}
