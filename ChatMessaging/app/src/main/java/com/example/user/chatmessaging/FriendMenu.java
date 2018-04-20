package com.example.user.chatmessaging;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class FriendMenu extends AppCompatActivity {

    String ID;
    String PW;
    String Name;
    String Status;
    String PartnerID;
    public static final int REQUEST_CODE_MENU2 = 102;

    Button userInfobutton;
    Button PartnerNameButton;

    ImageButton myImage1;
    BitmapDrawable bitmap;

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
            PartnerID = (String) extras.get("PartnerID");
        }

        userInfobutton = (Button) findViewById(R.id.loginedUserInfo);
        userInfobutton.setText(Name);
        PartnerNameButton = (Button) findViewById(R.id.PartnerName);
        PartnerNameButton.setText(PartnerID);

        myImage1 = (ImageButton) findViewById(R.id.myImage);
        String imgFileName = ID.toLowerCase();
        String PACKAGENAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(imgFileName, "drawable",this.getPackageName());
        System.out.println(imgId);
        if(imgId!=0)
        {
            myImage1.setImageResource(imgId);
        }
        else
        {
            imgFileName = "person";
            imgId = getResources().getIdentifier(imgFileName, "drawable",this.getPackageName());
            myImage1.setImageResource(imgId);
        }

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
        intent.putExtra("SenderID",ID);
        intent.putExtra("ReceiverID",PartnerID);
        intent.putExtra("Status",STATUS.ONLINE.toString());
        startActivityForResult(intent,REQUEST_CODE_MENU2);
    }
}
