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
	protected List<User> list;
	boolean isLogin = false;
	User checked_user = new User();
 
    public SocketServer() {
        clients = new HashMap();
        Collections.synchronizedMap(clients);    // 동기화 처리
    }
 
    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;
 
        try {
        	FileIO fileThread = new FileIO();
        	fileThread.ThreadStart();
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server Started");
 
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Login from [" + socket.getInetAddress() + ":" + socket.getPort() + "]");
 
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    void sendToAll(String msg) {
   // 클라이언트가 멀티채팅서버에 접속하면 HashMap에 저장되고 접속을 해제하면 hashMap에서 제거한다. 
   // 클라이언트가 데이터를 입력하면, 멀티채팅서버는 HashMap에 저장된 모든 클라이언트에게 데이터를 전송한다.
        Iterator it = clients.keySet().iterator();
 
        while (it.hasNext()) {
            try {
                DataOutputStream out = (DataOutputStream) clients.get(it.next());
                out.writeUTF(msg);
            } catch (Exception e) {
            }
        }
    }
 
    public static void main(String[] args) {
        new SocketServer().start();
    }
  
  // 멀티채팅서버의 ServerReceiver쓰레드는 클라이언트가 추가될 때마다 생성되며
    // 클라이언트의 입력을 서버에 접속된 모든 클라이언트에게 전송하는 일을 한다.
    class ServerReceiver extends Thread {
		Socket socket;
        DataInputStream in;
        DataOutputStream out;
 
        public ServerReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ie) {
            }
        }
 
   // 이 쓰레드의 run()을 보면 클라이언트가 새로 추가되었을 때 클라이언트의 이름을
        // key로 클라이언트의 출력스트림을 HashMap인 clients에 저장해서
        // 다른 클라이언트가 입력한 데이터를 전송하는데 사용하는 것을 알 수 있다.
        // 만일 클라이언트가 종료되어 클라이언트의 입력스트림(in)이 null이 되면 while문을 빠져나가서
        // clients의 목록에서 해당 클라이언트를 제거한다.
        public void run() {
            String name = "";
            try {
                name = in.readUTF();
                sendToAll("#" + name + "님이 입장했습니다.");
 
                clients.put(name, out);
                System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
 
                while (in != null) {
                    sendToAll(in.readUTF());
                }
            } catch (IOException ie) {
            } finally {
                sendToAll("#" + name + "님이 퇴장했습니다.");
                clients.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
                System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
            }
        }
        
        public User CheckUser(String ID, String PW) throws IOException
    	{
    		System.out.println("Checking User....  " + list.size());
    		
    		for(int i=0;i<list.size();i++)
    		{
    			System.out.println("Doing Something...." + i);
    			if(list.get(i).get_ID().equals(ID) && list.get(i).get_PW().equals(PW))
    			{
    				System.out.println(list.get(i) + "  " + ID);
    				FileIO.OFFLINEToONLINE(ID);
    				//isLogin = true;
    				return list.get(i);
    			}
    		}
    		
    		System.out.println("nothing exists");
    		return null;
    	}
        
        public void getUserInfoFromClient() throws IOException
        {
        	String ID = "";
        	String PW = "";
			String data = in.readUTF();
        	if(data.contains("ID_"))
			{
				String[] splited = data.split(":");
				ID = splited[0].replace("ID_", "");
				System.out.println("ID: " + ID);
				PW = splited[1].replace("PW_", "");
				System.out.println("PW: " + PW);
			}
			
        	//compare
			if(ID.length()>1 && PW.length()>1)
			{
				checked_user = CheckUser(ID,PW);
			}
        }
        
        public void sendLoginedUserInfoToClient() throws IOException
        {
        	if(checked_user.get_status().equals("ONLINE"))
			{
				out.writeUTF("LoginSuccessFull@!@!" + ":" + "LoginedUserID_" + checked_user.get_ID() + ":" + "LoginedUserPW_" + checked_user.get_PW() + ":" + "LoginedUserName_" + checked_user.get_Name());
				System.out.println("logined");
			}
			else
			{
				out.writeUTF("@#Check the ID or Password Please@#");
			}	
        }
    }
    public void setUserList(List<User> userList)
	{
		list = userList;
	}

}