package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FriendMenu extends AppCompatActivity {

    String ID;
    String PW;
    String Name;
    String Status;
    public static final int REQUEST_CODE_MENU2 = 102;

    Button userInfobutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_menu);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            ID = (String) extras.get("ID");
            PW = (String) extras.get("PW");
            Name = (String) extras.get("Name");
            Status = (String) extras.get("Status");
        }

        userInfobutton = (Button) findViewById(R.id.loginedUserInfo);
        userInfobutton.setText(Name);


        Button button = (Button) findViewById(R.id.backButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);

                System.out.print("Logout");
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            String name = data.getExtras().getString("name");
            Toast.makeText(this,name,Toast.LENGTH_LONG).show();
        }
    }

    public void onButtonFriend1Clicked(View v)
    {
        Intent intent = new Intent(getApplicationContext(), Chatting.class);
        startActivityForResult(intent,REQUEST_CODE_MENU2);
    }
}
