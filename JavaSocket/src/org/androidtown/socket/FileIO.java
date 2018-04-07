package org.androidtown.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;	
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileIO {
	public static void main(String[] args) throws IOException
	{
		
	}
	
	public static void ThreadStart()
	{
		Thread t1 = new Thread(new ReadingUserThread(),"A");
		t1.start();
	}
	//For sign Up
	public static void UserFileWrite(String ID,String PW, String Name) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter("User.txt",true));
		
		pw.print(ID);
		pw.print(" ");
		pw.print(PW);
		pw.print(" ");
		pw.print(Name);
		pw.print("\n");
		
		pw.close();
	}
	
	//For saving chatting
	public static void ChattingFileWrite(String Text, String SenderID, String ReceiverID) throws IOException
	{
PrintWriter pw = new PrintWriter(new FileWriter("User.txt",true));
		
		pw.print(Text);
		pw.print(" ");
		pw.print(SenderID);
		pw.print(" ");
		pw.print(ReceiverID);
		pw.print("\n");
		
		pw.close();
	}
	
	//Read User -> Thread
	public static void UserFileRead() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("User.txt"));
		while(true)
		{
			String line = br.readLine();
			if(line ==null)
			{
				break;
			}
			String[] columns = line.split(" ");
			String ID = columns[0];
			String PW = columns[1];
			String Name = columns[2];
			System.out.println("ID: " + ID);
			System.out.println("PW: " + PW);
			System.out.println("Name: " + Name + "\n");
		}
	}
	
	//Read User -> Thread
		public static void UserChattingRead() throws IOException
		{
			BufferedReader br = new BufferedReader(new FileReader("Chatting.txt"));
			while(true)
			{
				String line = br.readLine();
				if(line ==null)
				{
					break;
				}
				String[] columns = line.split(" ");
				String Text = columns[0];
				String SenderID = columns[1];
				String ReceiverID = columns[2];
				System.out.println("Text: " + Text);
				System.out.println("SenderID: " + SenderID);
				System.out.println("ReceiverID" + ReceiverID + "\n");
			}
		}
	//Read Chatting
}

class ReadingUserThread implements Runnable
{
	private static BufferedReader br = null;
	private List<User> list;
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
			tempUser = new User();
			//System.out.println(Thread.currentThread.getName());
			this.list = new ArrayList<User>();
			synchronized(br)
			{
				try
				{
					while((line = br.readLine()) != null)
					{
						String[] columns = line.split(" ");
						String ID = columns[0];
						String PW = columns[1];
						String Name = columns[2];
						tempUser.set_ID(ID);
						tempUser.set_PW(PW);
						tempUser.set_Name(Name);
						if(count < 15)
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
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				Thread.sleep(1);
				display(this.list);
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