package com.example.handsafety_banking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.UUID;


public class MessageHandling extends AsyncTask <String,Void,String> {
    Socket my_socket,my_socket2;
    DataOutputStream my_dos;
    PrintWriter my_pw;
    String in = "";
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... voids) {
        try {
            my_socket = new Socket("192.168.0.8",7800);
            my_socket2 = new Socket("192.168.0.8",7801);
            my_pw = new PrintWriter(my_socket.getOutputStream());
            my_pw.println(voids[0] + " ");
            my_pw.println(voids[1]);
            my_pw.flush();
            my_pw.close();
            my_socket.close();


            BufferedReader stdIn = new BufferedReader(new InputStreamReader(my_socket2.getInputStream()));
            in = stdIn.readLine();
            my_socket2.close();
            return in;

        } catch (Exception e) {

        }
        return null;
    }

    protected void onPostExecute(String  result) {
        delegate.processFinish(result);
    }

}
