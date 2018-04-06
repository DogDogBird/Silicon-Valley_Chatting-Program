package org.androidtown.socket;

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
		
		try
		{
			int portNumber = 5001;
			aServerSocket = new ServerSocket(portNumber);
			System.out.println("Starting Java Socket Server ...");
			System.out.println("Listening at port " + portNumber + " ...");
		
			while(true)//클라이언트 연결 대기
			{
				socket = aServerSocket.accept();//소켓 객체 참조
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
						String data = inStream.readUTF();
						System.out.printf("Server Received %s\n", data );
					}
					catch(Exception ex)
					{
						System.out.println(socket.getInetAddress().toString() + " went out");
						break;
					}
				}
				
				aServerSocket.close();
			}																																																					
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
