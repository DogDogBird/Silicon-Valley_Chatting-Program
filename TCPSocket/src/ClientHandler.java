import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class ClientHandler extends Thread
{
	DataInputStream dis;
	DataOutputStream dos;
	Socket s;
	
	Boolean isLogin = false;
	String CheckLogin = "";
	
	static List<User> list;
	static User checked_user;
	
	
	
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
	{
    	System.out.println("ClientHandler created");
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		
		FileIO file = new FileIO();
		try {
			file.UserFileRead();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ServerReceiver sr = new ServerReceiver(s);
		sr.start();
	}
	
	public ClientHandler()
	{
		System.out.println("new List created");
		checked_user = new User();
	}
	
	 class ServerReceiver extends Thread 
	 {
		 
		 
		 Socket socket;
	        public ServerReceiver(Socket socket) {
	        	System.out.println("new ServerReceived created");
	            this.socket = socket;
	            try 
	            {          	
	            	//FileIO fileThread = new FileIO();
	            	//fileThread.ThreadStart();
	                dis = new DataInputStream(socket.getInputStream());
	                dos = new DataOutputStream(socket.getOutputStream());
	               
	            } 
	            catch (IOException ie) 
	            {
	            }
	        }
	 
	        public void run()
	        {
	            String name = "";
	            
	            try 
	            {
	            	//
	            	getUserInfoFromClient();
	            	            	                
	            }
	            catch (IOException ie) 
	            {
	            	
	            } 
	            finally {
	            }
	        }
	        
	        public User CheckUser(String ID, String PW) throws IOException
	    	{
	        	System.out.println(list.size());
	    		System.out.println("Checking User....  " + list.size());
	    		
	    		for(int i=0;i<list.size();i++)
	    		{
	    			System.out.println("Doing Something...." + i);
	    			if(list.get(i).get_ID().equals(ID) && list.get(i).get_PW().equals(PW))
	    			{
	    				System.out.println(list.get(i) + "  " + ID);				
	    				checked_user.set_Status(STATUS.ONLINE);//isLogin = true;
	    				return list.get(i);
	    			}
	    		}
	    		
	    		System.out.println("nothing exists");
	    		return null;
	    	}
	        
	        public void getUserInfoFromClient() throws IOException
	        {
	        	System.out.println("getUserInfoFromClient Start from handler");
	        	String ID = "";
	        	String PW = "";
				String data = dis.readUTF();
				System.out.println("data" + data);
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
					sendLoginedUserInfoToClient();
				}
	        }
	        
	        public void sendLoginedUserInfoToClient() throws IOException
	        {
	        	
	        	if(checked_user.get_status().equals(STATUS.ONLINE))
				{
					dos.writeUTF("LoginSuccessFull@!@!" + ":" + "LoginedUserID_" + checked_user.get_ID() + ":" + "LoginedUserPW_" + checked_user.get_PW() + ":" + "LoginedUserName_" + checked_user.get_Name());
					System.out.println("logined");
				}
				else
				{
					dos.writeUTF("@#Check the ID or Password Please@#");
				}	
	        }
	    }
	    public void setUserList(List<User> userList)
		{
			list = userList;
		}	    
}
