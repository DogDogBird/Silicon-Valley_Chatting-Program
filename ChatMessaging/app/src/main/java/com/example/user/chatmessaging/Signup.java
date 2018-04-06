package com.example.user.chatmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Signup extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;

    EditText editTextID;
    EditText editTextPW;
    EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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

    public void onButtonSignUpClicked(View v)
    {
        //생성 되었으면 생성되었다고 토스트
        writeToFile("User.txt");
    }

    public void onButtonCancelClicked(View v)
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(intent,REQUEST_CODE_MENU);
    }

    private void writeToFile(String fileName)
    {
        try {
            editTextID = (EditText) findViewById(R.id.editID);
            editTextPW = (EditText) findViewById(R.id.editPW);
            editTextName = (EditText) findViewById(R.id.editName);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");

            outputStreamWriter.append(editTextID.getText().toString());
            outputStreamWriter.append(",");
            outputStreamWriter.append(editTextPW.getText().toString());
            outputStreamWriter.append(",");
            outputStreamWriter.append(editTextName.getText().toString());
            outputStreamWriter.append("\n");
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Toast.makeText(this,"Sign up Successfully",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivityForResult(intent,REQUEST_CODE_MENU);
        }
        catch(IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
