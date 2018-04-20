package com.example.user.chatmessaging;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FriendMenu extends AppCompatActivity {

    String ID;
    String PW;
    String Name;
    String Status;
    String PartnerID;
    String PartnerStatus = "";
    public static final int REQUEST_CODE_MENU2 = 102;

    Button userInfobutton;
    Button PartnerNameButton;

    ImageButton myImage1;
    ImageView StatusImage;

    private String ip = "61.255.4.166";//IP
    public static int SERVERPORT = 7777;

    MyAsyncTask myAsyncTask;
    static DataOutputStream Dout;
    static DataInputStream Din;
    Socket mSocket;

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

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        userInfobutton = (Button) findViewById(R.id.loginedUserInfo);
        userInfobutton.setText(Name);
        PartnerNameButton = (Button) findViewById(R.id.PartnerName);
        PartnerNameButton.setText(PartnerID);

        myImage1 = (ImageButton) findViewById(R.id.myImage);
        String imgFileName = ID.toLowerCase();
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

        //


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
                line = Din.readUTF();
                if(line.contains("State_"))
                {
                    PartnerStatus = line.replace("State_","");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            StatusImage = (ImageView) findViewById(R.id.partnerStatus);
            String imgFileName2 = PartnerStatus.toLowerCase();
            int imgId2 = getResources().getIdentifier(imgFileName2, "drawable",getPackageName());

            System.out.println("Image ID: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ imgId2);
            if(imgId2 == 0)
            {
                imgId2 = getResources().getIdentifier("offline", "drawable",getPackageName());
                StatusImage.setImageResource(imgId2);
            }
            else
            {
                StatusImage.setImageResource(imgId2);
            }
        }
    }

    private class Sender extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try {
                Dout.writeUTF(ID + ":" + "StatusIs_BUSY:::" + "CheckUsersState_:" + PartnerID);
                Dout.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
