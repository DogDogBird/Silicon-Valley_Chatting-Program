package com.example.user.chatmessaging;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	static HashMap<String, String> userStatusList;
	
	static String data;
	static String[] statedata;
	
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
	            	else if(data.contains("StatusIs_"))
	            	{
	            		statedata = data.split(":::");
	            		System.out.println("SetUserStatus");
	            		setUserState();
	            		sendUserState();
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
						System.out.println("Current User ID: " + checked_user.get_ID());
						System.out.println("Current User PW: " + checked_user.get_PW());
						System.out.println("Current User Name: " + checked_user.get_Name());
						System.out.println("Current User partner ID: " + checked_user.get_partnerID());
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
	    				checked_user.set_PartnerID(list.get(i).get_partnerID());
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
					dos.writeUTF("LoginSuccessFull@!@!" + ":" + "LoginedUserID_" + checked_user.get_ID() + ":" + "LoginedUserPW_" + checked_user.get_PW() + ":" + "LoginedUserName_" + checked_user.get_Name() +  ":" + "LoginedUserPartnerID_" + checked_user.get_partnerID());
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
	        	String PartnerID = "";
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
					PartnerID = splited[3].replace("PartnerID_", "");
					System.out.println("PartnerID: " + PartnerID);
					
					for(int i=0;i<list.size();i++)
					{
						if(list.get(i).get_ID().equals(SignUpID))
						{
							System.out.println("ID Already exists");
							dos.writeUTF("ID Already Exists!@#!@#");
							dos.flush();
							return;
						}
					}
					dos.writeUTF("SignUpSuccessfull@!#!@#");
					dos.flush();
					file.UserFileWrite(SignUpID, SignUpPW, SignUpName,PartnerID);
				}
	        }
	        
	        public void setUserState() throws IOException
	        {
	        	System.out.println("Writing User State in Server");
	        	FileIO file = new FileIO();
	        
	        	String ID = "";
	        	String[] splited = statedata[0].split(":");
	        	if(data.contains("StatusIs_OFFLINE"))
            	{
	        		System.out.println("data contains Status os offline");
	        		file.UserStateFileWrite(splited[0], "OFFLINE");
	        		file.updateUserStateFile(splited[0],"OFFLINE");
            	}
	        	else if(data.contains("StatusIs_BUSY"))
            	{
	        		System.out.println("data contains Status os busy");
	        		file.UserStateFileWrite(splited[0], "BUSY");
	        		file.updateUserStateFile(splited[0],"BUSY");
            	}
	        	else if(data.contains("StatusIs_ONLINE"))
            	{
	        		System.out.println("data contains Status os online");
	        		file.UserStateFileWrite(splited[0], "ONLINE");
	        		file.updateUserStateFile(splited[0],"ONLINE");
            	}
	        }
	        public void sendUserState() throws IOException
	        {	        	
	        	System.out.println("Sending User State Date to Client");
	        	String PartnerID = statedata[1].replace("CheckUsersState_:", "");
	                	        	
	        	FileIO file = new FileIO();
	        	file.ReadUserState();
	        	
	        	Set set = userStatusList.entrySet();
				Iterator iterator = set.iterator();
				while(iterator.hasNext())
				{
					Map.Entry mentry = (Map.Entry)iterator.next();
					if(mentry.getKey().equals(PartnerID))
					{
						dos.writeUTF("State_" + mentry.getValue().toString());
						System.out.println("State_" + mentry.getValue().toString());
						dos.flush();
						return;
					}
				}
				dos.writeUTF("State_OFFLINE");
				System.out.println("State_OFFLINE");
				dos.flush();
				
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
	    public void setUserStateList(HashMap<String, String> userState)
		{
	    	userStatusList = userState;
		}	 
}
