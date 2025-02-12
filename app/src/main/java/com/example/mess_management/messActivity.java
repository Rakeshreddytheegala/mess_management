package com.example.mess_management;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class messActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView dateSelect,avg_waste,avg_income,people,cons_counter1,cons_counter2,rating_1,rating_2,rating_3,rating_4,rating_5,avg_rating;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String date="";
    String Date="";
    Spinner spinner;
    String c= "0";
    SimpleDateFormat sdf;
    int[] ratingCount = new int[20];
    ConnectionClass connectionClass;
    int eaters=0;
    int income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        spinner = findViewById(R.id.meal_select);
        spinner.setOnItemSelectedListener(this);

        avg_income = findViewById(R.id.income_amt);
        avg_waste = findViewById(R.id.waste_amt);
        people = findViewById(R.id.people_amt);
        cons_counter1 = findViewById(R.id.counter1_amt);
        cons_counter2 = findViewById(R.id.counter2_amt);
        rating_1 = findViewById(R.id.rating_1);
        rating_2 = findViewById(R.id.rating_2);
        rating_3 = findViewById(R.id.rating_3);
        rating_4 = findViewById(R.id.rating_4);
        rating_5 = findViewById(R.id.rating_5);
        avg_rating = findViewById(R.id.rating_amt);

        connectionClass = new ConnectionClass();

        dateSelect = findViewById(R.id.date_select);
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                Calendar cal1 = Calendar.getInstance();
                cal1.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),1);

                DatePickerDialog dialog = new DatePickerDialog(
                        messActivity.this,
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
                fetch_data();
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        c = Integer.toString(i);
        if(!Date.equals("")){
            date = c+"-"+Date;
            Log.d("date",date);
            fetch_data();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fetch_data() {

        String query;
        Statement stmt;
        ResultSet rs;
        String temp;

        try {
            Connection con = connectionClass.CONN();
            Log.d("query","Hi");
            if (con == null) {
                Log.d("query","Hi1");
                Toast.makeText(messActivity.this,"Couldn't Connect",Toast.LENGTH_SHORT).show();
            } else {
                query = "SELECT `"+date+"` FROM subscriber WHERE NOT `"+date+"`='0'";
                Log.d("query",query);
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);

                eaters = 0;
                income = 0;
                while(rs.next()){
                    eaters++;
                    income+=Integer.parseInt(rs.getString(date));
                    Log.d("Income1",rs.getString(date)+"___1");
                }
                //Toast.makeText(messActivity.this,Integer.toString(eaters) + " " + Integer.toString(income),Toast.LENGTH_LONG).show();
                query = "SELECT cost FROM non_subscriber WHERE date = '"+date+"'";
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);

                while(rs.next()){
                    eaters++;
                    income+=Integer.parseInt(rs.getString("cost"));
                    Log.d("Income",rs.getString("cost"));
                }

                query = "SELECT waste,counter1,counter2 FROM management WHERE date = '"+date+"'";
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);

                if(rs.next()){

                    temp = rs.getString("counter1") + " grams";
                    cons_counter1.setText(temp);
                    temp = rs.getString("counter2") + " grams";
                    cons_counter2.setText(temp);
                    temp = rs.getString("waste") + " grams";
                    avg_waste.setText(temp);
                }
                temp = Integer.toString(eaters);
                people.setText(temp);
                temp = Integer.toString(income);
                avg_income.setText(temp);

                query = "SELECT review FROM rating where date = '"+ date +"'";
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                int i=0;
                Double ave=0.0;
                int rating;

                for (int j=0;j<5;j++)
                    ratingCount[j]=0;

                while(rs.next()){
                    i++;
                    rating = Integer.parseInt(rs.getString("review"));
                    ave = ((rating + ave*(i-1))*(1.0))/i;
                    ratingCount[rating-1]++;
                }

                if (ave < 2.5)
                    avg_rating.setTextColor(getResources().getColor(R.color.dark_red));
                else if (ave > 3.5)
                    avg_rating.setTextColor(getResources().getColor(R.color.green));

                avg_rating.setText(Double.toString(ave));
                rating_1.setText(Integer.toString(ratingCount[0]));
                rating_2.setText(Integer.toString(ratingCount[0]));
                rating_3.setText(Integer.toString(ratingCount[0]));
                rating_4.setText(Integer.toString(ratingCount[0]));
                rating_5.setText(Integer.toString(ratingCount[0]));



            }
        } catch (Exception ex) {
            Toast.makeText(messActivity.this,"Some Unexpected Error Occured",Toast.LENGTH_SHORT).show();
        }
    }
}
