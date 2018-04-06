package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Chatting extends AppCompatActivity {

    private String html = "";
    ProgressHandler mHandler;
    TextView textView;
    TextView textView1;

    private int value = 0;
    private boolean isRunning = true;

    private EditText EDITTEXT;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    // private String ip = "61.255.4.166";//IP
    public static final int SERVERPORT = 2222;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        EDITTEXT = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.chat1);

        String addr = "";
        try {
            addr = InetAddress.getLocalHost().toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        ConnectThread thread2 = new ConnectThread(addr);
        thread2.start();

        mHandler = new ProgressHandler();

        Button button = (Button) findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(EDITTEXT.getText().toString() + "(" + textView1.getText().toString() + ")");
                EDITTEXT.getText().clear();
            }
        });

    }



    public void onStart()
    {
        super.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(isRunning) {
                        Thread.sleep(1000);
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                    }
                }
                catch (Exception ex)
                {
                    Log.e("chatmessaging", "Exception in processing message.", ex);
                }
            }
        });
        isRunning = true;
        thread.start();
    }

    public void onStop()
    {
        super.onStop();

        isRunning = false;
    }
    /*
    public void onResume()
    {
        super.onResume();

        running = true;

        Thread thread = new BackgroundThread();
        thread.start();
    }
    */


    //click back button
    public void onButtonBackClicked(View v)
    {
        Intent intent =new Intent();
        intent.putExtra("name","out of chatting");
        setResult(RESULT_OK,intent);

        finish();
    }

    /*
    class BackgroundThread extends Thread{
        public void run()
        {
            while(running)
            {
                try
                {
                    Thread.sleep(1000);
                    chatMessage = EDITTEXT.getText().toString();
                }
                catch (InterruptedException ex)
                {
                    Log.e("chatmessaging", "Exception in thread",ex);
                }
            }
        }
    }
    */


    public class ProgressHandler extends Handler {

        public void handleMessage(Message msg)
        {
            EDITTEXT = (EditText) findViewById(R.id.editText);

            GregorianCalendar gcalendar = new GregorianCalendar();
            textView1 = (TextView) findViewById(R.id.chat2);
            textView1.setText(gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":"+ gcalendar.get(Calendar.SECOND));
        }
    }

    class ConnectThread extends Thread{
        String hostname;

        public ConnectThread(String addr)
        {
            hostname = addr;
        }
        public void run()
        {
            try
            {
                int port = 11001;
                Socket sock = new Socket(hostname,port);

                ObjectOutputStream outStream = new ObjectOutputStream(sock.getOutputStream());

                outStream.writeObject("Hello AndroidTown on Android");
                outStream.flush();

                ObjectInputStream inStream = new ObjectInputStream(sock.getInputStream());
                String obj = (String) inStream.readObject();

                Log.d("MainActivity", "message from Server : " + obj);

                sock.close();

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
