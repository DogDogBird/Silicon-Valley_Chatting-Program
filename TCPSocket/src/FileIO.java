

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;	
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileIO extends SocketServer{

	
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
	public static void UserFileWrite(String ID,String PW, String Name, String Status) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter("User.txt",true));
		
		pw.print(ID);
		pw.print(" ");
		pw.print(PW);
		pw.print(" ");
		pw.print(Name);
		pw.print(" ");
		pw.print("OFFLINE");
		pw.print("\n");
		
		pw.close();
	}
	public static void OFFLINEToONLINE(String ID) throws IOException
	{
		String oldFileName = "User.txt";
		String tmpFileName = "tmp_User.txt";
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try
		{
			br = new BufferedReader(new FileReader(oldFileName));
			bw = new BufferedWriter(new FileWriter(tmpFileName));
			String line;
			while((line = br.readLine()) != null)
			{
				if(line.contains(ID))
				{
					line = line.replace("OFFLINE", "ONLINE");
				}
				bw.write(line + "\n");
			}
		}
		catch (Exception e)
		{
			return;
		}
		finally
		{
			try
			{
				if(br!=null)
				{
					br.close();
				}
			}
			catch(IOException e)
			{
			}
		}
		File oldFile = new File(oldFileName);
		oldFile.delete();
		
		File newFile = new File(tmpFileName);
		newFile.renameTo(oldFile);
	}
	
	//For saving chatting
	public static void ChattingFileWrite(String Text, String SenderID, String ReceiverID) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter("Chatting.txt",true));
		
		pw.print(Text);
		pw.print(" ");
		pw.print(SenderID);
		pw.print(" ");
		pw.print(ReceiverID);
		pw.print("\n");
		
		pw.close();
	}
	
	//Read User -> Thread
	public static synchronized void UserFileRead() throws IOException
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
		public static synchronized void UserChattingRead() throws IOException
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
				//System.out.println("Text: " + Text);
				//System.out.println("SenderID: " + SenderID);
				//System.out.println("ReceiverID" + ReceiverID + "\n");
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
					while((line = br.readLine()) != null)
					{
						tempUser = new User();
						String[] columns = line.split(" ");
						String ID = columns[0];
						String PW = columns[1];
						String Name = columns[2];
						String Status = columns[3];
						tempUser.set_ID(ID);
						tempUser.set_PW(PW);
						tempUser.set_Name(Name);
						tempUser.set_Status(Status);
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
					SocketServer socketServer = new SocketServer();
					socketServer.setUserList(list);
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