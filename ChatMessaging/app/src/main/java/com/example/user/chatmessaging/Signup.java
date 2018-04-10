package com.example.user.chatmessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Signup extends AppCompatActivity {

    EditText etID;
    EditText editTextPW ;
    EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


    }

    public void onButtonSignUpClicked(View v)
    {
        etID = (EditText) findViewById(R.id.editID);
        editTextPW = (EditText) findViewById(R.id.editPW);
        editTextName = (EditText) findViewById(R.id.editName);
        String data = etID.getText().toString();
        try
        {
            FileOutputStream fos = openFileOutput("User.txt", Context.MODE_APPEND);
            PrintWriter out = new PrintWriter(fos);
            out.println(data);
            out.close();

            Toast.makeText(this,"file saved",Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onButtonCancelClicked(View v)
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    private void writeToFile(String fileName)
    {

    }
}
