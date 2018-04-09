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
 
      // ������ ������ Ŭ���̾�Ʈ�� HashMap�� �����Ͽ� �����Ѵ�.
    HashMap clients;
	protected List<User> list;
	boolean isLogin = false;
	User checked_user = new User();
 
    public SocketServer() {
        clients = new HashMap();
        Collections.synchronizedMap(clients);    // ����ȭ ó��
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
   // Ŭ���̾�Ʈ�� ��Ƽä�ü����� �����ϸ� HashMap�� ����ǰ� ������ �����ϸ� hashMap���� �����Ѵ�. 
   // Ŭ���̾�Ʈ�� �����͸� �Է��ϸ�, ��Ƽä�ü����� HashMap�� ����� ��� Ŭ���̾�Ʈ���� �����͸� �����Ѵ�.
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
  
  // ��Ƽä�ü����� ServerReceiver������� Ŭ���̾�Ʈ�� �߰��� ������ �����Ǹ�
    // Ŭ���̾�Ʈ�� �Է��� ������ ���ӵ� ��� Ŭ���̾�Ʈ���� �����ϴ� ���� �Ѵ�.
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
 
   // �� �������� run()�� ���� Ŭ���̾�Ʈ�� ���� �߰��Ǿ��� �� Ŭ���̾�Ʈ�� �̸���
        // key�� Ŭ���̾�Ʈ�� ��½�Ʈ���� HashMap�� clients�� �����ؼ�
        // �ٸ� Ŭ���̾�Ʈ�� �Է��� �����͸� �����ϴµ� ����ϴ� ���� �� �� �ִ�.
        // ���� Ŭ���̾�Ʈ�� ����Ǿ� Ŭ���̾�Ʈ�� �Է½�Ʈ��(in)�� null�� �Ǹ� while���� ����������
        // clients�� ��Ͽ��� �ش� Ŭ���̾�Ʈ�� �����Ѵ�.
        public void run() {
            String name = "";
            try {
                name = in.readUTF();
                sendToAll("#" + name + "���� �����߽��ϴ�.");
 
                clients.put(name, out);
                System.out.println("���� ���������� ���� " + clients.size() + "�Դϴ�.");
 
                while (in != null) {
                    sendToAll(in.readUTF());
                }
            } catch (IOException ie) {
            } finally {
                sendToAll("#" + name + "���� �����߽��ϴ�.");
                clients.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "���� ������ �����Ͽ����ϴ�.");
                System.out.println("���� ���������� ���� " + clients.size() + "�Դϴ�.");
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