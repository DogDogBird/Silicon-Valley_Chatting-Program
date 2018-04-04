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


public class Chatting extends AppCompatActivity {

    private String html = "";
    ProgressHandler mHandler;
    TextView textView;
    String chatMessage;

    private int value = 0;
    private boolean running = true;

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
        mHandler = new ProgressHandler();

        Button button = (Button) findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EDITTEXT.getText().clear();
                textView.setText(chatMessage);
            }
        });

    }

    public void onStart()
    {
        super.onStart();

        running = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
        });
    }

    public void onResume()
    {
        super.onResume();

        running = true;

        Thread thread = new BackgroundThread();
        thread.start();
    }


    public void onButtonBackClicked(View v)
    {
        Intent intent =new Intent();
        intent.putExtra("name","out of chatting");
        setResult(RESULT_OK,intent);

        finish();
    }

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

    public class ProgressHandler extends Handler{

        public void handleMessage(Message msg)
        {

        }
    }
}
