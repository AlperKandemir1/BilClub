package com.example.firebaseupload;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebaseupload.ChatClient.Client;
import com.example.firebaseupload.ChatClient.ServerListener;

import com.google.firebase.auth.FirebaseAuth;


public class ChatActivity extends AppCompatActivity {

    String firebaseUser;
    //Properties

    static final int PORT = 6666;
    public static Handler handler;
    boolean started = false;
    int start = 0;
    Client client1;
    EditText messageInput;
    Button sendTxt;
    TextView message;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        messageInput = findViewById(R.id.message_input);
        sendTxt = findViewById(R.id.send_text);
        message = findViewById(R.id.message_view);
        message.setMovementMethod(new ScrollingMovementMethod());
        message.setText("Initializing...");
        message.setTextColor(Color.BLACK);
        message.setTextSize(18);
        client1 = new Client(name,PORT);
        client1.execute();
        buttonListener();
        new ServerListener(client1.getSocket()).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                String messageText = (String) msg.obj;
                message.append("\n" + messageText);
            }
        };
        client1.sendMsg(name);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        client1.terminate();
    }

    @Override
    protected void onStop(){
        super.onStop();
        client1.terminate();
    }


    public void buttonListener(){
        sendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                messageInput.setText("");
                if(!message.matches("")) {
                    client1.sendMsg(message);
                }
            }
        });
    }

    public String getUser(){
        return name;
    }

    public void setUser(String name){
        this.name = name;
    }


}
