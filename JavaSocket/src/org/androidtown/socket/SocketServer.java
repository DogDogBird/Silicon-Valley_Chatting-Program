package org.androidtown.socket;

import java.util.List;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer 
{
	
	public static void main(final String[] args)
	{
		ServerSocket aServerSocket = null; //객체 생성
		Socket socket = null;
		DataInputStream inStream = null;
		DataOutputStream outStream = null;
		
		FileIO user = new FileIO();
		//user.UserFileWrite(ID, PW, Name);
		FileIO.ThreadStart();
		List<User> userList = user.getUserList();
		//printAllUsers();
		
		try
		{
			int portNumber = 5001;
			aServerSocket = new ServerSocket(portNumber);
			System.out.println("Starting Java Socket Server ...");
			System.out.println("Listening at port " + portNumber + " ...");
			
		
			while(true)//클라이언트 연결 대기
			{
				socket = aServerSocket.accept();//소켓 객체 참조
				socket.isInputShutdown();
				InetAddress clientHost = socket.getLocalAddress();
				int clientPort = socket.getPort();
				System.out.println("A client connected. : " + socket.getInetAddress().toString() + ", port : " + clientPort);
			
				inStream = new DataInputStream(socket.getInputStream());
				outStream = new DataOutputStream(socket.getOutputStream());

				outStream.writeUTF("이거 받으면 연결된거!");
				
				outStream.flush();
				
				while(true)
				{
					try
					{
						
						String ID = "";
						String PW = "";
						String data = inStream.readUTF();
						if(data.contains("ID_"))
						{
							ID = data.replace("ID_", "");
							System.out.println("ID: " + ID);
						}
						else if(data.contains("PW_"))
						{
							PW = data.replace("PW_", "");
							System.out.println("PW: " + PW);
						}
						else if(CheckUser(ID,PW,userList))
						{
							outStream.writeUTF("LoginSuccessFull@!@!");
							System.out.printf("Server Received %s\n", data );
						}
					}
					catch(Exception ex)
					{
						System.out.println(socket.getInetAddress().toString() + " went out");
						break;
					}
				}
				
			}																																																					
		}
		catch(Exception ex)
		{
			System.out.println("Error in main exception");
			ex.printStackTrace();
		}
	}
	public static Boolean CheckUser(String ID, String PW, List<User> userList)
	{
		for(int i=0;i<userList.size();i++)
		{
			if(userList.get(i).equals(ID)&&userList.get(i).equals(PW))
			{
				System.out.println("Checked");
				return true;
			}
		}
		System.out.println("nothing exists");
		return false;
	}

}
