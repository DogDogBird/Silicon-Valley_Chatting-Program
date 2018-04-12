package com.example.user.chatmessaging;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ServerService extends Service
{
    final static int SERVER_PORT = 7777;
    String serverIp = "61.255.4.166";

    protected List<User> list;
    boolean isLogin = false;

    IBinder mBinder;

    class MyBinder extends Binder
    {
        ServerService getService()
        {
            return ServerService.this;
        }
    }

    public IBinder onBind(Intent intent)
    {
        //use when I need to send data with service object and Acivity
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("test", "서비스의 onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("test", "서비스의 onStartCommand");

        try
        {
            Socket socket = new Socket(serverIp,SERVER_PORT);
            System.out.println("서버에 연결되었습니다");

        }
        catch(ConnectException ce)
        {
            ce.printStackTrace();
            System.out.println("서버에 연결되지 않았습니다");
        }
        catch(Exception e)
        {
            System.out.println("서버에 연결되지 않았습니다2");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy()
    {
        super.onDestroy();

        Log.d("test", "서비스의 onDestroy");
    }

}
