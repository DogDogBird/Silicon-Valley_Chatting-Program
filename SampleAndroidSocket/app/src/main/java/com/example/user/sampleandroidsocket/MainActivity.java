package com.example.user.sampleandroidsocket;

import android.net.Network;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText input01;
    TextView textView;

    String recieveMessage;
    String sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button01 = (Button) findViewById(R.id.button);
        input01 = (EditText) findViewById(R.id.editText);

            button01.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //String addr = input01.getText().toString().trim();
                    String addr =  "61.255.4.166";
                    ConnectThread thread = new ConnectThread(addr);
                    thread.start();
                }
            });

    }

    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run()
        {
            try
            {
                int port = 5001;
                textView = findViewById(R.id.textView);

                Socket sock = new Socket(hostname,port);

                DataOutputStream outStream = new DataOutputStream(sock.getOutputStream());
                outStream.writeUTF("Hello AndroidTown on Android");
                outStream.flush();

                DataInputStream inStream = new DataInputStream(sock.getInputStream());
                String obj = (String) inStream.readUTF();

                Log.d("MainActivity", "서버에서 받은 메시지 : " + obj);
                textView.setText(obj);

                sock.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
