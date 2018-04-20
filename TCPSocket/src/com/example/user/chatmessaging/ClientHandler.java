package com.example.user.chatmessaging;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class ClientHandler extends Thread
{
	DataInputStream dis;
	DataOutputStream dos;
	ObjectOutputStream oos;
	Socket s;
	
	Boolean isLogin = false;
	String CheckLogin = "";
	
	static List<User> list;
	static List<ChattingMessage> msgList;
	static User checked_user;
	
	static String data;
	
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
	            	data = dis.readUTF();
	            	System.out.println("data   " + data);
	            	if(data.contains("LoginID_"))
	            	{
	            		getUserInfoFromClient();
	            	}
	            	else if(data.contains("ChattingText_"))
	            	{
	            		System.out.println("get Chatting Data");
	            		getChattingData();    
	            	}
	            	else if(data.contains("SignUpD_"))
	            	{
	            		System.out.println("get Sign up Data");
	            		SignUp();
	            	}
	            	else if(data.contains("senderID_"))
	            	{
	            		System.out.println("start SendChattingData");
	            		SendChattingData();
	            	}
	            	else if(data.contains("Received well"))
	            	{
	            		System.out.println("Received well from client");
	            	}
	            }
	            catch (IOException ie) 
	            {
	            	
	            } 
	            finally {
	            }
	        }
	        public void getUserInfoFromClient() throws IOException
	        {
	        	System.out.println("getUserInfoFromClient Start from handler");
	        	//System.out.println("Current User: " + checked_user.get_Status());
	        	
	        	String ID = "";
	        	String PW = "";
				System.out.println("data: " + data);
	        	if(data.contains("LoginID_"))
				{
					String[] splited = data.split(":");
					ID = splited[0].replace("LoginID_", "");
					System.out.println("ID: " + ID);
					PW = splited[1].replace("LoginPW_", "");
					System.out.println("PW: " + PW);
					
					checked_user = CheckUser(ID,PW);
					if(checked_user != null)
					{
						System.out.println("Current User: " + checked_user.get_ID());
						System.out.println("Current User: " + checked_user.get_PW());
						System.out.println("Current User: " + checked_user.get_Name());
					}
					else if(checked_user == null)
					{
						socket.close();
						return;
					}
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
	    				checked_user.set_ID(ID);
	    				checked_user.set_PW(PW);
	    				checked_user.set_Name(list.get(i).get_Name());
	    				checked_user.set_Status(STATUS.ONLINE);//isLogin = true;
	    				sendLoginedUserInfoToClient();
	    				return list.get(i);
	    			}
	    		}	        	    		
	    		System.out.println("nothing exists");
	    		return null;
	    	}
	        	             
	        public void sendLoginedUserInfoToClient() throws IOException
	        {
	        	
	        	if(checked_user.get_status() == (STATUS.ONLINE))
				{
					dos.writeUTF("LoginSuccessFull@!@!" + ":" + "LoginedUserID_" + checked_user.get_ID() + ":" + "LoginedUserPW_" + checked_user.get_PW() + ":" + "LoginedUserName_" + checked_user.get_Name());
					System.out.println("logined");
					dos.flush();
				}
				else
				{
					dos.writeUTF("@#Check the ID or Password Please@#");
		        	dos.flush();
				}	
	        }
	        
	        public void getChattingData() throws IOException
	        {
	        	String chatting = "";
	        	String senderID = "";
	        	String receiverID = "";
	        	String timeStamp = "";
	        	FileIO file = new FileIO();
	        	if(data.contains("ChattingText_"))
				{
	        		String [] splited = data.split(":::");
	        		senderID = splited[0].replace("ChattingText_", "");
	        		receiverID = splited[1];
	        		chatting = splited[2];
	        		timeStamp = splited[3];
	        		
	        		file.ChattingFileWrite(senderID, receiverID, chatting, timeStamp);
	        		
	        		System.out.println(senderID + ": " + chatting + " to " + receiverID);
				}
	        }
	        
	        public void SendChattingData()
	        {
	        	String senderID = "";
	        	String receiverID = "";
	        	String filename = "";
	        	String timeStamp = "";
	        	String text = "";
	        	FileIO file = new FileIO();
	        	System.out.println(data);
	        	if(data.contains("senderID_"))
	        	{
	        		String [] splited = data.split(":::");
	        		senderID = splited[0].replace("senderID_", "");
	        		receiverID = splited[1].replace("receiverID_", "");
	        		filename = senderID + receiverID + ".txt";
	        	}
	        	try 
	        	{
	        		file.ChattingFileWrite(senderID, receiverID, text,timeStamp); //if there is no file on the folder
					file.UserChattingRead(senderID, receiverID);//Read file
		        	//dos.writeUTF("ChattingList_");
				} 
	        	catch (IOException e) 
	        	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				
				for(int i=0;i<msgList.size();i++)
				{
					String printsenderID = msgList.get(i).getSenderID();
					String printreceiverID = msgList.get(i).getReceiverID();
					String printMessage = msgList.get(i).getMsg();
					String printTimeStamp = msgList.get(i).getTimeStamp();
					
					System.out.println("Sender: " + printsenderID + "/receiver: " + printreceiverID + "/Message: " + printMessage + " " + printTimeStamp);
				}
					try 
					{
		                oos = new ObjectOutputStream(socket.getOutputStream());	 
						oos.writeObject(msgList);
						oos.flush();
						oos.close();
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
	        
	        public void SignUp() throws IOException
	        {
	        	String SignUpID = "";
	        	String SignUpPW = "";
	        	String SignUpName = "";
	        	FileIO file = new FileIO();
	        	if(data.contains("SignUpD_"))
				{
	        		String[] splited = data.split(":");
	        		SignUpID = splited[0].replace("SignUpD_", "");
	        		System.out.println("SignUpID: " + SignUpID);
	        		SignUpPW = splited[1].replace("SignUpPW_", "");
					System.out.println("SignUpPW: " + SignUpPW);
					SignUpName = splited[2].replace("SignUpName_", "");
					System.out.println("SignUpName: " + SignUpName);
					file.UserFileWrite(SignUpID, SignUpPW, SignUpName);
				}
	        }
	    }
	    public void setUserList(List<User> userList)
		{
			list = userList;
		}	 
	    public void setMsgList(List<ChattingMessage> _msgList)
		{
	    	msgList = _msgList;
		}	 
}
