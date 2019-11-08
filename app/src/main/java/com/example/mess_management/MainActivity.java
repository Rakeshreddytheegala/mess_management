package com.example.mess_management;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity
{
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public static final int SWIPE_THRESHOLD = 100;
    ConnectionClass connectionClass;
    EditText edtuserid,edtpass;
    Button btnlogin;
    ProgressBar pbbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.et_username);
        edtpass = (EditText) findViewById(R.id.et_password);
        btnlogin = (Button) findViewById(R.id.btn_Login);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin  doLogin = new DoLogin();
                doLogin.execute("");

            }
        });
    }

    private void OnSwipeRight() {
        Intent intent = new Intent(getApplicationContext(), scannerActivity.class);
        startActivity(intent);
    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                Toast.makeText(MainActivity.this,r,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), After_login.class);
                intent.putExtra("emailID",userid);
                startActivity(intent);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                if (userid.trim().equals("root") && password.trim().equals("scanner")) {

                    Intent intent = new Intent(getApplicationContext(), scannerActivity.class);
                    startActivity(intent);

                }
                else if (userid.trim().equals("root") && password.trim().equals("mess")){

                    Intent intent = new Intent(getApplicationContext(),messActivity.class);
                    startActivity(intent);
                }
                else if (userid.trim().equals("root") && password.trim().equals("payment")){

                    Intent intent = new Intent(getApplicationContext(),non_subs_pay.class);
                    startActivity(intent);
                }
                else {
                    try {
                        Connection con = connectionClass.CONN();
                        if (con == null) {
                            z = "Error in connection with SQL server";
                        } else {
                            String query = "select * from subscriber where email='" + userid.trim() + "' and passwd='" + password.trim() + "'";
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(query);

                            if (rs.next()) {
                                z = "Login successfull";
                                isSuccess = true;
                            } else {
                                z = "Invalid Credentials";
                                isSuccess = false;
                            }

                        }
                    } catch (Exception ex) {
                        isSuccess = false;
                        z = "Exceptions";
                    }
                }
            }
                return z;
        }
    }
}
