package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    List<User> userList;
    EditText editTextID;
    EditText editTextPW;

    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;

    SimpleSocket ssocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler mHandler = new Handler(Looper.getMainLooper())
        {
        };

        ssocket = new SimpleSocket(ip,SERVERPORT,mHandler);
        ssocket.start();
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
        //ssocket.sendStringtoServer("ID_" + editTextID + ":PW_" + editTextPW);

        if(editTextID.getText().toString().equals("kyubin") && editTextPW.getText().toString().equals("1234"))
        {
            Toast.makeText(this,"Login successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), FriendMenu.class);
            startActivityForResult(intent, REQUEST_CODE_MENU);
            ssocket.setIsLogin(true);
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
