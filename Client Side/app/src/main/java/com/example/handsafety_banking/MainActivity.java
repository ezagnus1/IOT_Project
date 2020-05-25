package com.example.handsafety_banking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.getIntent;
import static android.os.Environment.getExternalStoragePublicDirectory;



public class MainActivity extends AppCompatActivity implements AsyncResponse {
    String pathtoFile,image=null;
    ImageView imageview2;
    EditText b_username,b_password;
    Button login;
    TextView check_error;
    MessageHandling asyncTask =new MessageHandling();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncTask.delegate = this;

        check_error = (TextView)findViewById(R.id.error_check);
        login = (Button)findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {

            String url = "jdbc:mysql://192.168.0.8:3306/iot";
            String username = "egezagnus";
            String password = "12345";
            @Override
            public void onClick(View v) {
                b_username = (EditText)findViewById(R.id.editText_username);
                b_password = (EditText)findViewById(R.id.editText_password);

                try {
                    String user=null,passw=null,safety=null;
                    int counter=0;
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(url,username,password);
                    String result = "Connection success";
                    Statement st = conn.createStatement();
                    String u = b_username.getText().toString();
                    String p = b_password.getText().toString();
                    ResultSet rs = st.executeQuery("select * from credentials");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while(rs.next()){
                        user= rs.getString(1);
                        passw = rs.getString(2);
                        safety = rs.getString(3);
                        image = rs.getString(4);
                        if(user.equals(u) && passw.equals(p)){
                            counter = counter+1;
                            break;
                        }
                    }

                    if(counter>0){
                        if(safety.equals("true")){
                            takePicture();
                        }
                        else{
                            Intent intent = new  Intent(MainActivity.this,approving_page.class);
                            startActivity(intent);
                        }
                    }
                    else{
                        check_error.setText("Credentials are wrong. Please try again!!");
                        check_error.setVisibility(View.VISIBLE);
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void takePicture(){
        Intent pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(pic,0);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        asyncTask.execute(image,temp);
    }

    public void processFinish(String output){
        Log.d("eben",output);
        if (output.equals("true")) {
            Intent intent = new Intent(MainActivity.this, approving_page.class);
            startActivity(intent);
        }
        else{
            check_error.setText("IMAGE CREDENTIAL IS WRONG!!!.");
            check_error.setVisibility(View.VISIBLE);
        }


    }


}



