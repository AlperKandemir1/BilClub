package com.example.firebaseupload.ChatClient;

import android.os.Message;
import com.example.firebaseupload.ChatActivity;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {
    //Properties
    static String user = "";
    static Socket socket;
    static int port;
    ServerListener listen;
    PrintWriter input;
    public Client(String userName, int port) {
        user = userName;
        this.port = port;
    }

    public Socket getSocket(){
        return socket;
    }

    public void execute(){
        Message msg = Message.obtain();
        try {
            //Client Properties
            Socket socket = new Socket("178.20.229.45", port);
            this.socket = socket;
            System.out.println("Connected to the server");
            input = new PrintWriter(socket.getOutputStream(), true);
            //new Send(socket,user).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
            msg.obj = "Server not found: " + ex.getMessage();
            ChatActivity.handler.sendMessage(msg);
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
            msg.obj = "I/O Error: " + ex.getMessage();
            ChatActivity.handler.sendMessage(msg);
        }

    }

    public void terminate(){
        try{
            socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public void sendMsg(String send){
        if(send != null) {
            input.println(send);
        }

    }


}
