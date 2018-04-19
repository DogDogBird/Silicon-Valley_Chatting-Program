package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    EditText editTextID;
    EditText editTextPW;

    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;

    MyAsyncTask myAsyncTask;
    static DataOutputStream Dout;
    static DataInputStream Din;
    Socket mSocket;

    static String ID = "";
    static String PW  = "";
    static String Name = "";

    static boolean isLogined = false;

    static User loginedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginedUser = new User();
    }

    public void onButton1Clicked(View v)
    {
            editTextID = (EditText) findViewById(R.id.editText1);
            editTextPW = (EditText) findViewById(R.id.editText2);
            ID = editTextID.getText().toString();
            PW = editTextPW.getText().toString();
            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            loginedUser.set_Status(STATUS.OFFLINE);
            isLogined = false;
            Toast.makeText(this,"Logout Successfully",Toast.LENGTH_LONG).show();
        }
    }

    public void onButton2Clicked(View v)
    {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
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
            try
            {
                if(ID.length()>=1)
                {
                    line = Din.readUTF();
                    if(line.contains("LoginSuccessFull@!@!"))
                    {
                        getLoginedInfoFromServer(line);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(isLogined)
            {
                Intent intent = new Intent(getApplicationContext(), FriendMenu.class);
                intent.putExtra("ID", ID);
                intent.putExtra("PW", PW);
                intent.putExtra("Name", Name);
                intent.putExtra("Status", STATUS.ONLINE.toString());
                Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Try again",Toast.LENGTH_LONG).show();
                editTextPW.setText("");
                editTextID.setText("");
            }
            try
            {
                mSocket.close();
                System.out.println("Socket closed");
            } catch (IOException e)
            {
                e.printStackTrace();
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
                if(ID.length()>=1)
                {
                    Dout.writeUTF("LoginID_" + ID + ":" + "LoginPW_" + PW);
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
        protected void onPostExecute(Void result) {
        }
    }

    public static void getLoginedInfoFromServer(String data) throws IOException
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
            isLogined = true;

        }

        //if not logined
        else if(data.contains("@#Check the ID or Password Please@#"))
        {
            System.out.println("Check the ID or Password Please");
            isLogined = false;
        }
    }
    public static User getLoginedUser()
    {
        return loginedUser;
    }
}
