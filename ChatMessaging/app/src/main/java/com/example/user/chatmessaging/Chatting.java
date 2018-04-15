package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    String tempString;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;

    MyAsyncTask myAsyncTask;
    static DataOutputStream Dout;
    static DataInputStream Din;
    Socket mSocket;

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

        mHandler = new ProgressHandler();



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

    public class ProgressHandler extends Handler {

        public void handleMessage(Message msg)
        {
            GregorianCalendar gcalendar = new GregorianCalendar();
            textView1 = (TextView) findViewById(R.id.chat2);
            textView1.setText(gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":"+ gcalendar.get(Calendar.SECOND));
        }
    }

    public void onSendButtonClicked(View v)
    {
        tempString = EDITTEXT.getText().toString();
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }
    //click back button
    public void onButtonBackClicked(View v)
    {
        Intent intent =new Intent();
        intent.putExtra("name","out of chatting");
        setResult(RESULT_OK,intent);

        finish();
    }

    private class MyAsyncTask extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute()
        {
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                mSocket = new Socket(ip,SERVERPORT);
                Dout = new DataOutputStream(mSocket.getOutputStream());
                Din = new DataInputStream(mSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("서버에 연결되었습니다");
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            final Sender messageSender = new Sender(); // Initialize chat sender
            // AsyncTask.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else {
                messageSender.execute();
            }
            Receiver receiver = new Receiver();
            receiver.execute();

        }
    }

    private class Receiver extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute()
        {
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            String line;

                line = "";

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_LONG).show();
        }
    }

    private class Sender extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                System.out.println(tempString);
                Dout.writeUTF("ChattingText_" + tempString);
                Dout.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            textView.setText(EDITTEXT.getText().toString() + "(" + textView1.getText().toString() + ")");
            EDITTEXT.getText().clear();
        }
    }


}
