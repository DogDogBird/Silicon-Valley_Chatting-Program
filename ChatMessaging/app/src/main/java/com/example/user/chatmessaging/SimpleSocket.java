/*
package com.example.user.chatmessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.user.chatmessaging.User;

public class SimpleSocket extends Thread {
    static Socket  mSocket;

    static private BufferedReader buffRecv;
    static private BufferedWriter buffSend;

    private String  mAddr ;
    private int     mPort ;
    private boolean mConnected = false;
    private Handler mHandler = null;

    static String ID = "";
    static String PW = "";
    static String Name = "";
    static STATUS Status = STATUS.OFFLINE;
    static User loginedUser ;
    static boolean isLogin = false;
    static boolean isConnect = false;

    static String tempStr = "";


    static DataOutputStream Dout;
    static DataInputStream Din;

    static class MessageTypeClass
    {
        public static final int SIMSOCK_CONNECTED = 1;
        public static final int SIMSOCK_DATA = 2;
        public static final int SIMSOCK_DISCONNECTED = 3;
    };

    public enum MessageType { SIMSOCK_CONNECTED, SIMSOCK_DATA, SIMSOCK_DISCONNECTED };

    public SimpleSocket(String addr, int port, Handler handler)
    {
        mAddr = addr;
        mPort = port;
        mHandler = handler;
        connect(mAddr,mPort);
    }

    private void makeMessage(MessageType what, Object obj)
    {
        Message msg = Message.obtain();
        msg.what = what.ordinal();
        msg.obj  = obj;
        mHandler.sendMessage(msg);
    }

    private boolean connect (String addr, int port)
    {
        try
        {
            InetSocketAddress socketAddress  = new InetSocketAddress (InetAddress.getByName(addr), port);
            mSocket = new Socket();
            mSocket.connect(socketAddress, 5000);
            System.out.println("서버에 연결되었습니다");
            Thread receiver = new Thread(new ClientReceiver(mSocket));
            Thread sender = new Thread(new ClientSender(mSocket));
            sender.start();
            receiver.start();
            System.out.println("Dout Din 생성@@@@@@@@@@@@@@@@@@");
        }
        catch (IOException e)
        {
            System.out.println(e);
            e.printStackTrace();
            isConnect = false;
            return false;
        }
        isConnect = true;
        return true;
    }


    public void run() {
        if(!connect(mAddr,mPort)) {
            System.out.println("연결 실패@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            return; // connect failed
        }
        if(mSocket == null) {
            System.out.println("소켓 미아ㅏㅏㅏㅏㅏㅏㅏㅏㅏ@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            return;
        }


        try
        {
            buffRecv = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            buffSend = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mConnected = true;

        makeMessage(MessageType.SIMSOCK_CONNECTED, "");
        Log.d("SimpleSocket", "socket_thread loop started");

        String aLine = null;

        while(! Thread.interrupted() )
        {
            try
            {
                if(isLogin)
                {
                    buffSend.write("Chatting: "); //chatting의 editText, 송신자, 수신자 받아오기
                }

                aLine = buffRecv.readLine();
                    if(aLine != null)
                {
                    makeMessage(MessageType.SIMSOCK_DATA, aLine);
                }
                else break;

                //when logined

            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try
            {
                String data = buffRecv.readLine();

                if(data.contains("LoginSuccessFull@!@!"))
                {
                    System.out.println("이거 받으면 연결됨~~");
                }

                //getLoginedInfoFromServer(data);
            }
            catch (IOException e)
            {
            }
        }

        makeMessage(MessageType.SIMSOCK_DISCONNECTED, "");
        Log.d("SimpleSocket", "socket_thread loop terminated");

        try {

            buffRecv.close();
            buffSend.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mConnected = false;
    }

    synchronized public boolean isConnected(){
        return mConnected;
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

            if(splited[2].contains("LoginedUserPW_"))
            {
                PW = splited[2].replace("LoginedUserPW_", "");
                System.out.println("LoginedUserPW_: " + PW);
            }

            if(splited[3].contains("LoginedUserName_"))
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
            isLogin = false;
        }
    }
    public static User getLoginedUser()
    {
        return loginedUser;
    }

    public void sendString(String str)
    {
        PrintWriter pout = new PrintWriter(buffSend, true);
        pout.println(str);
    }

    public void setIsLogin(boolean login)
    {
        isLogin = login;
    }

    public Socket getmSocket()
    {
        return mSocket;
    }
    public static void GetMessageFromActivity(String str)
    {
        tempStr =  str;
    }

    static class ClientSender extends Thread
    {
        Socket socket;
        String name = "";

        ClientSender(Socket socket)
        {
            this.socket = socket;
            try
            {
                Dout = new DataOutputStream(socket.getOutputStream());
            }
            catch(Exception e){}
        }


        public void run()
        {
            while(!isLogin)
            {
                try {
                    Thread.sleep(100);
                    //Login();
                    isLogin = true;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            try
            {

                if(Dout != null)
                {
                    //out.writeUTF(name);
                }
                while(Dout!=null)
                {
                    if(isLogin)
                    {
                        System.out.println("Write text");
                        Dout.writeUTF(tempStr );
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
                Din = new DataInputStream(socket.getInputStream());

            }catch(IOException e){}
        }

        public void run()
        {
            while(Din !=null)
            {
                try
                {
                    String data = Din.readUTF();
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

    /*
    public static void Login ()
    {
        try {
            Scanner scan = new Scanner(System.in);
            String ID;
            String PW;
            System.out.println("ID: ");
            ID = scan.nextLine();
            System.out.println("PW: ");
            PW = scan.nextLine();
            out.writeUTF("ID_" + ID + ":PW_" + PW);
            out.flush();

        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            System.out.println("program failed in Login");
            e.printStackTrace();
        }
    }
    */

/*
}
*/