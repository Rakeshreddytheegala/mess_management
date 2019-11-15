package com.example.mess_management;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mysql.jdbc.ResultSetMetaData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class After_login extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView dateSelect,total_waste,total_due,carbView,protView,calView,fatView,pay_due;
    String emailID;
    String ID;
    String Waste;
    Double Cost = 0.0;
    Float Carb,Protien,Cal,Fat;
    int year;
    int month;
    int day;
    int count;
    DatePickerDialog.OnDateSetListener dateSetListener;

    String date="";
    String Date="";
    Spinner spinner;
    String c= "0";

    String query ;
    Statement stmt;
    Calendar cal;
    ResultSet rs;
    ResultSetMetaData rsmd;

    Map<String,Integer> costMap = new HashMap<>();

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

        connectionClass = new ConnectionClass();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.meal_select);
        spinner.setOnItemSelectedListener(After_login.this);

        total_waste = findViewById(R.id.waste_amt);
        total_due = findViewById(R.id.income_amt);
        carbView = findViewById(R.id.carb_amt);
        calView = findViewById(R.id.cal_amt);
        protView = findViewById(R.id.prot_amt);
        fatView = findViewById(R.id.fat_amt);
        pay_due = findViewById(R.id.pay_amt);

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        fetch_data();

        dateSelect = findViewById(R.id.date_select);
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                Calendar cal1 = Calendar.getInstance();
                cal1.set(year,month,1);

                DatePickerDialog dialog = new DatePickerDialog(
                        After_login.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day
                );

                dialog.getDatePicker().setMinDate(cal1.getTimeInMillis());
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i2, int i1, int i) {
                i1++;
                Date =  Integer.toString(i2);
                Date = i1+"-"+Date;
                if(i1<=9) Date = "0"+Date;
                Date = i+"-"+Date;
                if(i<=9) Date = "0"+ Date;
                date = c+"-"+Date;
                dateSelect.setText(Date);
                Log.d("date_picker",date);

                if(costMap.containsKey(date)) {
                    Log.d("costMap Search","successful "+date);
                    int cost = costMap.get(date);
                    Log.d("costMap Search","successful "+date+" "+Integer.toString(cost));
                    pay_due.setText("Rs. "+Integer.toString(cost));
                }

            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        c = Integer.toString(i);
        if(!Date.equals("")){
            date = c+"-"+Date;
            Log.d("date",date);
            if(costMap.containsKey(date)) {
                int cost = costMap.get(date);
                pay_due.setText("Rs. "+Integer.toString(cost));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fetch_data() {
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                Log.d("query","Hi1");
                Toast.makeText(After_login.this,"Couldn't Connect",Toast.LENGTH_SHORT).show();
            } else {
                query = "select name FROM student_info WHERE emailID = '" + emailID + "'";
                Log.d("Initial",query);
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);

                //if(rs.next())
                //    getActionBar().setTitle("Welcome "+rs.getString("name"));

                query = "select * from subscriber where email='" + emailID + "'";
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                rsmd = (ResultSetMetaData) rs.getMetaData();

                while(rs.next()){
                    int numColumns = rs.getMetaData().getColumnCount();
                    ID = rs.getObject(2).toString();
                    Waste = rs.getObject(10).toString();
                    Carb = Float.parseFloat(rs.getObject(5).toString());
                    Fat = Float.parseFloat(rs.getObject(6).toString());
                    Protien = Float.parseFloat(rs.getObject(7).toString());
                    Cal = Float.parseFloat(rs.getObject(8).toString());
                    int a;
                    count=0;
                    for ( int i = 11 ; i <= numColumns ; i++ ) {
                        a = Integer.parseInt(rs.getObject(i).toString());
                        if(a==0) count++;
                        Cost += a;
                        costMap.put(rs.getMetaData().getColumnName(i),a);
                        //System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
                    }
                }
                if((day-(count/3)>0)){
                    Carb = Carb / (day - (count / 3));
                    Fat = Fat / (day - (count / 3));
                    Protien = Protien / (day - (count / 3));
                    Cal = Cal / (day - (count / 3));

                    if(Cal < 1800 || Cal > 3200) {
                        calView.setTextColor(getResources().getColor(R.color.dark_red));
                        carbView.setTextColor(getResources().getColor(R.color.dark_red));
                        fatView.setTextColor(getResources().getColor(R.color.dark_red));
                        protView.setTextColor(getResources().getColor(R.color.dark_red));
                    }
                    else{
                        if (Carb < (45 * Cal) / 100 || Carb > (65 * Cal) / 100)
                            carbView.setTextColor(getResources().getColor(R.color.dark_red));

                        if (Fat < (20 * Cal) / 100 || Fat > (35 * Cal) / 100)
                            fatView.setTextColor(getResources().getColor(R.color.dark_red));

                        if (Protien < 40 || Protien > 64)
                            protView.setTextColor(getResources().getColor(R.color.dark_red));
                    }
                }

                Log.d("day",Integer.toString(day));
                Log.d("count",Integer.toString(count));


                total_waste.setText(Waste);
                total_due.setText(Double.toString(Cost));


                calView.setText(Float.toString(Cal));
                protView.setText(Float.toString(Protien));
                fatView.setText(Float.toString(Fat));
                carbView.setText(Float.toString(Carb));
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(After_login.this,"An Unexpected Error Occured",Toast.LENGTH_LONG).show();
        }
    }

}
