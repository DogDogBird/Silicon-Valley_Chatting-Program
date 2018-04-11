

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class JavaSocketClient 
{
	static String ID = "";
	static String PW = "";
	static String Name = "";
	static STATUS Status = STATUS.OFFLINE;
	static User loginedUser;
	static boolean isLogin = false;

	static DataOutputStream out;
	static DataInputStream in;
	
	public JavaSocketClient() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String args[])
	{
		try
		{
			String serverIp = "127.0.0.1";
			Socket socket = new Socket(serverIp,7777);
			System.out.println("서버에 연결되었습니다");
			Thread receiver = new Thread(new ClientReceiver(socket));
			Thread sender = new Thread(new ClientSender(socket));
			sender.start();
			receiver.start();			
			
		}catch(ConnectException ce) {ce.printStackTrace();}
		catch(Exception e){}
	}
	
	static class ClientSender extends Thread
	{
		Socket socket;
		String name;
		
		//처음에 아이디랑 비밀번호를 입력
		//DataOutputStream에 아이디 비밀번호 정보 전달
		//서버에서 처리 후 true, false 반환
		//false라면 더이상 접근 불가
		//true라면 해당 아이디로 채팅방 입장
		ClientSender(Socket socket)
		{
			this.socket = socket;
			try
			{
				out = new DataOutputStream(socket.getOutputStream());
			}catch(Exception e){}
		}
	
	
		public void run()
		{		
			Scanner scanner = new Scanner(System.in);		
			while(!isLogin)
			{
				try {
					Login();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		
			try
			{
				if(out != null)
				{
					//out.writeUTF(name);				
				}
				while(out!=null)
				{
					if(isLogin)
					{
						System.out.println("Write text");
						out.writeUTF("[" + Name + "]" + scanner.nextLine());
					}
				}			
			}
			catch(IOException e)
			{				
			}
		}		
	}
	
	static class ClientReceiver extends Thread
	{
		Socket socket;
		ClientReceiver(Socket socket)
		{
			this.socket = socket;
			try
			{
				in = new DataInputStream(socket.getInputStream());
				
			}catch(IOException e){}
		}
	
		public void run()
		{
			while(in !=null)
			{
				try
				{
					String data = in.readUTF();
					//System.out.println(in.readUTF());
					
					if(data.contains("LoginSuccessFull@!@!"))
					{
						System.out.println("이거 받으면 연결됨~~");
					}
					//Check if login
					//if logined
									
					getLoginedInfoFromServer(data);
				
				}catch(IOException e){}
			}
		}
	}
	
	public static void Login () throws InterruptedException
	{
		try {
			Scanner scan = new Scanner(System.in);
			String ID;
			String PW;
			System.out.println("ID: ");
			ID =  scan.nextLine();
			System.out.println("PW: ");
			PW = scan.nextLine();
			out.writeUTF("ID_" + ID + ":PW_" + PW);
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("program failed in Login");
			e.printStackTrace();
		}		
	}
	
	public static void getLoginedInfoFromServer(String data)
	{
		String[] splited = data.split(":");
				
		if(splited[0].contains("LoginSuccessFull@!@!"))
		{
			ID = splited[1].replace("LoginedUserID_", "");
			//System.out.println("LoginedUserID_: " + ID);
			PW = splited[2].replace("LoginedUserPW_", "");
			//System.out.println("LoginedUserPW_: " + PW);
			Name = splited[3].replace("LoginedUserName_", "");
			//System.out.println("LoginedUserName_: " + PW);
					
			System.out.println("Login Successfully");
			if(splited[1].contains("LoginedUserID_"))
			{
				ID = splited[1].replace("LoginedUserID_", "");
				System.out.println("LoginedUserID_: " + ID);
			}
			else if(splited[2].contains("LoginedUserPW_"))
			{
				PW = splited[2].replace("LoginedUserPW_", "");
				System.out.println("LoginedUserPW_: " + PW);
			}
			else if(splited[3].contains("LoginedUserName_"))
			{
				Name = splited[3].replace("LoginedUserName_", "");
				System.out.println("LoginedUserName_: " + Name);
			}
			
			loginedUser.set_ID(ID);
			loginedUser.set_PW(PW);
			loginedUser.set_Name(Name);	
			loginedUser.set_Status(STATUS.ONLINE);	
			isLogin = true;
		}
		
		//if not logined			
		else if(data.contains("@#Check the ID or Password Please@#"))
		{
			System.out.println("Check the ID or Password Please");
		}
	}
	public static User getLoginedUser()
	{
		return loginedUser;
	}
}