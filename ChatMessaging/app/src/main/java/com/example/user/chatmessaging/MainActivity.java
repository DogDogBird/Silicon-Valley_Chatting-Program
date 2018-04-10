package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    List<User> userList;
    EditText editTextID;
    EditText editTextPW;

    private String ip = "127.0.0.1";//IP
    public static int SERVERPORT = 7777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            Socket socket = new Socket(ip, SERVERPORT);
            System.out.println("서버에 연결되었습니다");
        }
        catch (ConnectException ce)
        {
            System.out.println("서버에 연결되지 않았습니다");
            ce.printStackTrace();}
        catch(Exception e){System.out.println("서버에 연결되지 않았습니다2");}

        userList = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            String name = data.getExtras().getString("name");
            Toast.makeText(this,name,Toast.LENGTH_LONG).show();
        }
    }

    public void onButton1Clicked(View v)
    {

        editTextID = (EditText) findViewById(R.id.editText1);
        editTextPW = (EditText) findViewById(R.id.editText2);
        if(editTextID.getText().toString().equals("kyubin") && editTextPW.getText().toString().equals("1234"))
        {
            Toast.makeText(this,"Login successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), FriendMenu.class);
            startActivityForResult(intent, REQUEST_CODE_MENU);
        }
        else
        {
            Toast.makeText(this,"Check the id or password please", Toast.LENGTH_LONG).show();
        }
    }
    public void onButton2Clicked(View v)
    {
            Intent intent = new Intent(getApplicationContext(), Signup.class);

            startActivity(intent);
    }
}
