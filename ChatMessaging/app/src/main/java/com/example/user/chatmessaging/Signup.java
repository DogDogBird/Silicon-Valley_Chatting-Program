package com.example.user.chatmessaging;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Signup extends AppCompatActivity {

    EditText ID;
    EditText PW ;
    EditText Name;
    EditText PartnerID;

    Socket mSocket;
    DataOutputStream Dout;
    DataInputStream Din;

    MyAsyncTask myAsyncTask;

    static boolean isSignUp = false;

    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


    }

    public void onButtonSignUpClicked(View v)
    {
        ID = (EditText) findViewById(R.id.editID);
        PW = (EditText) findViewById(R.id.editPW);
        Name = (EditText) findViewById(R.id.editName);
        PartnerID = (EditText) findViewById(R.id.editPartner);

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

    }

    public void onButtonCancelClicked(View v)
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
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
            try {
                String line = Din.readUTF();
                if(line.contains("ID Already Exists!@#!@#"))
                {
                    isSignUp = false;
                }
                else
                {
                    isSignUp = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(isSignUp)
            {
                Toast.makeText(getApplicationContext(),"SignUp Done",Toast.LENGTH_LONG).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"ID already exists",Toast.LENGTH_LONG).show();
                ID.setText("");
                PW.setText("");
                Name.setText("");
                PartnerID.setText("");
            }
            isSignUp = false;
        }
    }

    private class Sender extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                if(ID.getText().toString().length()>=1 && PW.getText().toString().length()>=1 && Name.getText().toString().length() >=1)
                {
                    Dout.writeUTF("SignUpD_" + ID.getText().toString() + ":" + "SignUpPW_" + PW.getText().toString() +
                            ":" + "SignUpName_" + Name.getText().toString()+ ":" + "PartnerID_" + PartnerID.getText().toString());
                }
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

        }
    }
}
