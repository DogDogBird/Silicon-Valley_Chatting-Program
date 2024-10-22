package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;




public class Chatting extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    ProgressHandler mHandler;
    TextView timeTextView;

    private boolean isRunning = true;

    private EditText EDITTEXT;
    String tempString;


    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;

    MyAsyncTask myAsyncTask;
    static DataOutputStream Dout;
    static DataInputStream Din;
    static ObjectInputStream Oin;
    Socket mSocket;

    static String senderID;
    static String receiverID;
    static String timeStamp;
    static STATUS status;

    static boolean sendButtonClicked;
    static boolean refreshButtonClicked;
    static boolean BackButtonClicked;
    static boolean isFirst;

    static List<ChattingMessage> msgList;
    static LinearLayout root;
    TextView t[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        BackButtonClicked = false;
        isFirst = false;

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            senderID = (String) extras.get("SenderID");
            receiverID = (String) extras.get("ReceiverID");
        }
        status = STATUS.ONLINE;
        setTitle(receiverID);

        EDITTEXT = (EditText) findViewById(R.id.editText);
        timeTextView = (TextView) findViewById(R.id.timeText);

        sendButtonClicked = false;
        refreshButtonClicked = false;

        mHandler = new ProgressHandler();

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
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
            //gcalendar = new GregorianCalendar();
//          textView1.setText(gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":"+ gcalendar.get(Calendar.SECOND));
            timeTextView.setText(getTime());
        }
    }

    public void onSendButtonClicked(View v)
    {
        sendButtonClicked = true;
        tempString = EDITTEXT.getText().toString();
        timeStamp = getTime();
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_LONG).show();
    }

    public void onButtonRefreshClicked(View v)
    {
        refreshButtonClicked = true;
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_LONG).show();
    }
    //click back button
    public void onButtonBackClicked(View v)
    {
        BackButtonClicked = true;
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        Intent intent =new Intent();
        intent.putExtra("name","out of chatting");

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
            if(!sendButtonClicked)
            {
                String line = "";
                if (line.contains("ChattingList"))
                {
                    System.out.println("Chatting List@@@@@@@@@");
                }

                try
                {
                    try
                    {
                        if(!isFirst)
                        {
                            msgList = new ArrayList<ChattingMessage>();
                            Oin = new ObjectInputStream(mSocket.getInputStream());
                            msgList = (ArrayList<ChattingMessage>) Oin.readObject();
                            Oin.close();
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < msgList.size(); i++)
                    {
                        String printsenderID = msgList.get(i).getSenderID();
                        String printreceiverID = msgList.get(i).getReceiverID();
                        String printMessage = msgList.get(i).getMsg();

                        System.out.println("Sender: " + printsenderID + "/receiver: " + printreceiverID + "/Message: " + printMessage);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(!isFirst)
            {
                root = (LinearLayout) findViewById(R.id.rl);
                root.setVerticalGravity(Gravity.BOTTOM);
                t = new TextView[100];
                LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (refreshButtonClicked) {
                    getCurrentFocus().clearFocus();
                }
                if (!sendButtonClicked) {
                    for (int i = 0; i < msgList.size(); i++) {
                        t[i] = new TextView(getApplicationContext());
                        t[i].setLayoutParams(dim);
                        t[i].setText(msgList.get(i).getTimeStamp() + "\n" + msgList.get(i).getMsg());
                        t[i].setTextSize(20);
                        if (msgList.get(i).getSenderID().equals(senderID)) {
                            t[i].setGravity(Gravity.RIGHT);
                            t[i].setBackgroundResource(R.drawable.my_message_bg);
                        } else {
                            t[i].setGravity(Gravity.LEFT);
                            t[i].setBackgroundResource(R.drawable.opponent_message_bg);
                        }
                        t[i].setPadding(16, 16, 16, 16);
                        root.addView(t[i]);
                    }
                } else {
                    TextView tempTextView;
                    tempTextView = new TextView(getApplicationContext());
                    tempTextView.setText(timeStamp + "\n" + tempString);
                    tempTextView.setTextSize(20);
                    tempTextView.setGravity(Gravity.RIGHT);
                    tempTextView.setPadding(16, 16, 16, 16);
                    tempTextView.setBackgroundResource(R.drawable.my_message_bg);

                    root.addView(tempTextView);
                }
            }
            sendButtonClicked = false;
            refreshButtonClicked = false;

            if(!isFirst)
            {
                isFirst = true;

                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
            }
            else
            {
                isFirst = false;
            }

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
                if(sendButtonClicked && !isFirst)
                {
                    System.out.println("ChattingText_" + senderID + ":::" + receiverID + ":::" + tempString +":::"+timeStamp);
                    Dout.writeUTF("ChattingText_" + senderID + ":::" + receiverID + ":::" + tempString + ":::" + timeStamp);
                    Dout.flush();
                }
                else if(!sendButtonClicked && !isFirst)
                {
                    System.out.println("senderID_" + senderID + ":::" + "receiverID_" + receiverID);
                    Dout.writeUTF("senderID_" + senderID + ":::" + "receiverID_" + receiverID);
                    Dout.flush();
                }

                if(!BackButtonClicked && isFirst)
                {
                    Dout.writeUTF(senderID + ":" + "StatusIs_ONLINE:::" + "CheckUsersState_:" + receiverID);
                    System.out.println(senderID + ":" + "StatusIs_ONLINE:::" + "CheckUsersState_:" + receiverID);
                    Dout.flush();
                }

                else if(BackButtonClicked && isFirst)
                {
                    Dout.writeUTF(senderID + ":" + "StatusIs_BUSY:::" + "CheckUsersState_:" + receiverID);
                    System.out.println(senderID + ":" + "StatusIs_BUSY:::" + "CheckUsersState_:" + receiverID);
                    Dout.flush();
                }
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
            if(EDITTEXT.getText().toString().length() >0)
            {
                //textView.setText(senderID + ":" + EDITTEXT.getText().toString() + "(" + textView1.getText().toString() + ")" + receiverID);
                EDITTEXT.getText().clear();
            }
        }
    }
    private String getTime(){
        mNow = System.currentTimeMillis()+32400000;
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

}