package com.example.user.chatmessaging;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;	
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileIO  {

	
	public static void main(String[] args) 
	{
		ThreadStart();
	}
	
	public static void ThreadStart()
	{
		Thread t1 = new Thread(new ReadingUserThread(),"A");
		t1.start();		
	}
	//For sign Up
	public static void UserFileWrite(String ID,String PW, String Name, String PartnerID) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter("User.txt",true));
		
		pw.print(ID);
		pw.print(" ");
		pw.print(PW);
		pw.print(" ");
		pw.print(Name);
		pw.print(" ");
		pw.print(PartnerID);
		pw.print("\n");
		pw.close();
	}
	
	//For saving chatting
	public static void ChattingFileWrite(String SenderID, String ReceiverID, String Text, String timeStamp) throws IOException
	{		
		String filename = SenderID + ReceiverID + ".txt";
		String filename2 = ReceiverID + SenderID + ".txt";
		PrintWriter pw = new PrintWriter(new FileWriter(filename,true));
		PrintWriter pw2 = new PrintWriter(new FileWriter(filename2,true));	
		
		if(Text.length()>0)
		{
			pw.print(SenderID);
			pw.print("\t");
			pw.print(ReceiverID);
			pw.print("\t");
			pw.print(Text);
			pw.print("\t");
			pw.print(timeStamp);
			pw.print("\n");
			
			pw2.print(SenderID);
			pw2.print("\t");
			pw2.print(ReceiverID);
			pw2.print("\t");
			pw2.print(Text);
			pw2.print("\t");
			pw2.print(timeStamp);
			pw2.print("\n");
		
			System.out.println("file written");
			pw.close();
			pw2.close();
		}
	}
	
	//Read User -> Thread
	public void UserFileRead() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("User.txt"));
		User tempUser;
		String line = "";
		int count = 0;
		
		try
		{
			List<User> list = new ArrayList<User>();
			System.out.println("File Reading start");
			while((line = br.readLine()) != null)
			{
				tempUser = new User();
				String[] columns = line.split(" ");
				String ID = columns[0];
				String PW = columns[1];
				String Name = columns[2];
				String PartnerID = columns[3];
				tempUser.set_ID(ID);
				tempUser.set_PW(PW);
				tempUser.set_Name(Name);
				tempUser.set_PartnerID(PartnerID);
				if(count < 30)
				{
					list.add(tempUser);
					count++;
				}
				else
				{
					list.add(tempUser);
					count = 0;
					break;
				}
			}
			System.out.println("File Reading End");
			
			System.out.println("setUserList(list) start");
			ClientHandler ch = new ClientHandler();
			ch.setUserList(list);
			for(int i=0;i<list.size();i++)
			{
				System.out.println(list.get(i).get_ID());
			}
			System.out.println("setUserList(list) End");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//Read User -> Thread
		public static synchronized void UserChattingRead(String senderID,String receiverID) throws IOException
		{
			String Filename = senderID + receiverID + ".txt";
			
			System.out.println("Filename: " + Filename);
			
			if(Filename.length()>0)
			{
				BufferedReader br = new BufferedReader(new FileReader(Filename));
			
				ChattingMessage tempMsg;
				String line = "";
			
				try
				{
					List<ChattingMessage> msgList = new ArrayList<ChattingMessage>();
					
					while((line = br.readLine()) != null)
					{			
						tempMsg = new ChattingMessage();
						String[] columns = line.split("\t");
						String SenderID = columns[0];
						String ReceiverID = columns[1];
						String Text = columns[2];
						String TimeStamp = columns[3];
						tempMsg.setSenderID(SenderID);
						tempMsg.setReceiverID(ReceiverID);
						tempMsg.setMsg(Text);
						tempMsg.setTimeStamp(TimeStamp);
					
						msgList.add(tempMsg);					
					}
				
					for(int i=0;i<msgList.size();i++)
					{
						System.out.println(msgList.get(i).getSenderID() + ":" + msgList.get(i).getReceiverID() + ":" + msgList.get(i).getMsg());
					}
					
					ClientHandler ch = new ClientHandler();
					ch.setMsgList(msgList);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			else
				return;
		
		}
		
		public static void UserStateFileWrite(String ID,String State) throws IOException
		{
			PrintWriter pw = new PrintWriter(new FileWriter("UserState.txt",true));
			
			pw.print(ID);
			pw.print("\t");
			pw.print(State);
			pw.print("\n");
			pw.close();
		}
		
		public static void ReadUserState() throws FileNotFoundException
		{
			BufferedReader br = new BufferedReader(new FileReader("UserState.txt"));
			HashMap<String, String> tempUser = new HashMap<String, String>();
			String line = "";
			int count = 0;
			
			try
			{
				System.out.println("File Reading start");
				while((line = br.readLine()) != null)
				{
					String[] columns = line.split("\t");
					String ID = columns[0];
					String State = columns[1];
					if(count < 30)
					{
						tempUser.put(ID, State);
						count++;
					}
					else
					{
						tempUser.put(ID, State);
						count = 0;
						break;
					}
				}
				System.out.println("File Reading End");
				
				System.out.println("setUserList(list) start");
				ClientHandler ch = new ClientHandler();
				ch.setUserStateList(tempUser);
				
				Set set = tempUser.entrySet();
				Iterator iterator = set.iterator();
				while(iterator.hasNext())
				{
					Map.Entry mentry = (Map.Entry)iterator.next();
					System.out.println("key is: " + mentry.getKey() + " & Value is: " + mentry.getValue());
				}
				
				System.out.println("setUserList(list) End");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	
}

class ReadingUserThread extends FileIO implements Runnable 
{
	private static BufferedReader br = null;
	private User tempUser;
	
	static 
	{
		try
		{
			br = new BufferedReader(new FileReader("User.txt"));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		String line = null;
		int count = 0;
		while(true)
		{
			//System.out.println(Thread.currentThread.getName());
			synchronized(br)
			{
				try
				{
					List<User> list = new ArrayList<User>();
					System.out.println("File Reading start");
					while((line = br.readLine()) != null)
					{
						tempUser = new User();
						String[] columns = line.split(" ");
						String ID = columns[0];
						String PW = columns[1];
						String Name = columns[2];
						tempUser.set_ID(ID);
						tempUser.set_PW(PW);
						tempUser.set_Name(Name);
						if(count < 30)
						{
							list.add(tempUser);
							count++;
						}
						else
						{
							list.add(tempUser);
							count = 0;
							break;
						}
					}
					display(list);
					System.out.println("File Reading End");
					
					System.out.println("setUserList(list) start");
					ClientHandler ch = new ClientHandler();
					ch.setUserList(list);
					System.out.println("setUserList(list) End");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				Thread.sleep(1);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if(line == null)
			{
				break;
			}
		}
	}
	/*
	public Boolean CheckUser(String ID, String PW)
	{
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).equals(ID)&&list.get(i).equals(PW))
			{
				System.out.println("Checked");
				return true;
			}
		}
		System.out.println("nothing exists");
		return false;
	}
	*/
	
	public void display(List<User> list)
	{
		for(int i=0;i<list.size();i++)
		{
			System.out.println("ID: " + list.get(i).get_ID());
			System.out.println("PW: " + list.get(i).get_PW());
			System.out.println("Name: " + list.get(i).get_Name());
		}
		System.out.println(list.size());
	}
}

class MyThread implements Runnable {
	private ConcurrentLinkedQueue<String> queue;
	private String contents;

	public MyThread() {
	}

	public MyThread(ConcurrentLinkedQueue<String> queue, String contents) {
		this.queue = queue;
		this.contents = contents;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queue.add(contents);
	}
}

//login

//