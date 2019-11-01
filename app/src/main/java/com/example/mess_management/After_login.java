package com.example.mess_management;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class After_login extends AppCompatActivity {

    String emailID;
    String ID;
    String Waste;
    Double Cost = 0.0;

    ConnectionClass connectionClass;
    //String z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        emailID = getIntent().getExtras().getString("emailID");

        try {
            Connection con = connectionClass.CONN();
            if (con != null) {
                String query = "select * from subscriber where email='" + emailID + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while(rs.next()){
                    int numColumns = rs.getMetaData().getColumnCount();
                    ID = rs.getObject(2).toString();
                    Waste = rs.getObject(5).toString();
                    for ( int i = 1 ; i <= numColumns ; i++ ) {
                        Cost += Double.parseDouble(rs.getObject(i).toString());
                        //System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
                    }
                }

            }
        }
        catch (Exception ex)
        {
            Toast.makeText(After_login.this,"An Unexpected Error Occured",Toast.LENGTH_LONG);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
