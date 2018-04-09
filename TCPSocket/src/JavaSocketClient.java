

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class JavaSocketClient 
{
	
	static public Boolean isLogin = false;
	String CheckLogin = "";

	static String ID = "";
	static String PW = "";
	static String Name = "";
	static STATUS Status = STATUS.OFFLINE;

	public JavaSocketClient() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String args[])
	{
		try
		{
			String serverIp = "127.0.0.1";
			Socket socket = new Socket(serverIp,7777);
			System.out.println("������ ����Ǿ����ϴ�");
			Thread receiver = new Thread(new ClientReceiver(socket));
			Thread sender = new Thread(new ClientSender(socket));
			receiver.start();
			sender.start();
			
		}catch(ConnectException ce) {ce.printStackTrace();}
		catch(Exception e){}
	}
	
	static class ClientSender extends Thread
	{
		Socket socket;
		DataOutputStream out;
		String name;
		
		//ó���� ���̵�� ��й�ȣ�� �Է�
		//DataOutputStream�� ���̵� ��й�ȣ ���� ����
		//�������� ó�� �� true, false ��ȯ
		//false��� ���̻� ���� �Ұ�
		//true��� �ش� ���̵�� ä�ù� ����
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
			Login(out);
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
			
		}catch(IOException e){}
		}
		
	}
	
	static class ClientReceiver extends Thread
	{
		Socket socket;
		DataInputStream in;
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
					
					if(data.contains("�̰�"))
					{
						System.out.println("�̰� ������ �����~~!!");
					}
					//Check if login
					//if logined
					
					String[] splited = data.split(":");
					
					
					
					if(splited[0].contains("LoginSuccessFull@!@!"))
					{
						ID = splited[1].replace("LoginedUserID_", "");
						//System.out.println("LoginedUserID_: " + ID);
						PW = splited[2].replace("LoginedUserPW_", "");
						//System.out.println("LoginedUserPW_: " + PW);
						Name = splited[3].replace("LoginedUserName_", "");
						//System.out.println("LoginedUserName_: " + PW);
						
						isLogin = true;
						
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
					}
					
					//if not logined			
					else if(data.contains("@#Check the ID or Password Please@#"))
					{
						System.out.println("Check the ID or Password Please");
					}
				
				}catch(IOException e){}
			}
		}
	}
	public static void Login (DataOutputStream out)
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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("program failed in Login");
			e.printStackTrace();
		}
		
	}
}