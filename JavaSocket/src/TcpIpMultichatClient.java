import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class TcpIpMultichatClient 
{
	
	static Boolean isLogin = false;
	String CheckLogin = "";

	public TcpIpMultichatClient() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String args[])
	{
		if(args.length!=1)
		{
			System.out.print("USAGE: java TcpIpMultichatClient 대화명");
			System.exit(0);
		}
		try
		{
			String serverIp = "127.0.0.1";
			Socket socket = new Socket(serverIp,5001);
			System.out.println("서버에 연결되었습니다");
			Thread receiver = new Thread(new ClientReceiver(socket));
			Thread sender = new Thread(new ClientSender(socket,args[0]));
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
		
		//처음에 아이디랑 비밀번호를 입력
		//DataOutputStream에 아이디 비밀번호 정보 전달
		//서버에서 처리 후 true, false 반환
		//false라면 더이상 접근 불가
		//true라면 해당 아이디로 채팅방 입장
		ClientSender(Socket socket, String name)
		{
			this.socket = socket;
			try
			{
				out = new DataOutputStream(socket.getOutputStream());
				this.name = name;
			}catch(Exception e){}
		}
	
	
		public void run()
		{

		
		Scanner scanner = new Scanner(System.in);
		
		Login(out);
		
		
		try
		{
			if(out != null)
			{
				out.writeUTF(name);
				
			}
			while(out!=null)
			{
				out.writeUTF("["+name+"]"+scanner.nextLine());
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
					System.out.println(in.readUTF());
					
					//Check if login
					if(in.readUTF().contains("LoginSuccessFull@!@!"))
					{
						isLogin = true;
						System.out.println("Login Successfully");
					}
					else
					{
						System.out.println("Login failed");
					}
					
						
				}catch(IOException e){}
			}
		}
	}
	public static void Login (DataOutputStream out)
	{
		
		try {
			Scanner scan = new Scanner(System.in);
			System.out.println("ID: ");
			out.writeUTF("ID_" + scan.nextLine());
			System.out.println("PW: ");
			out.writeUTF("PW_" + scan.nextLine());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("program failed in Login");
			e.printStackTrace();
		}
		
	}
}