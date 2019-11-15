package com.example.mess_management;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.util.TypedValue;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class non_subs_pay extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection con;
    String currentDate;
    int currentHour;
    int i=0;
    int j=0;
    String date;
    String Cost;
    String ID="";
    String z;
    TableRow tr_head;
    TextView name_col;
    TextView cost_col;
    CheckBox pay_box;
    TableLayout t1;
    String clientId = MqttClient.generateClientId();
    String MQTTHOST = "tcp://172.16.116.136:1883";
    MqttAndroidClient client;
    ArrayList<String> id = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_subs_pay);

        t1 = (TableLayout) findViewById(R.id.table);

        connectionClass = new ConnectionClass();

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentHour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        if( currentHour>=7 && currentHour <=10)
            date = "1-"+currentDate;
        else if( currentHour>=12 && currentHour <=14)
            date = "2-"+currentDate;
        else if( currentHour>=21 && currentHour <=23)
            date = "3-"+currentDate;

        fetch_data();
        ConnectMQTT();
        SetDefinitions();
    }

    public void SetDefinitions(){
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(non_subs_pay.this,"Connection lost",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                ID = new String(mqttMessage.getPayload());
                fetch_data();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void fetch_data(){
        try {
            con = connectionClass.CONN();
            if (con == null) {
                Toast.makeText(non_subs_pay.this,"Error in connection with SQL server",Toast.LENGTH_SHORT).show();
            } else {
                String query;
                if(ID.equals(""))
                    query = "SELECT * FROM non_subscriber WHERE date = '1-30-10-2019' AND paid = 'NO'";
                else
                    query = "SELECT * FROM non_subscriber WHERE stud_id = '"+ID+"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                Toast.makeText(non_subs_pay.this,"Yo_Bro",Toast.LENGTH_SHORT).show();
                while(rs.next()){
                    ID = rs.getString("stud_id");
                    id.add(ID);
                    Cost = rs.getString("cost");
                    //Toast.makeText(non_subs_pay.this,ID+" "+Cost,Toast.LENGTH_SHORT).show();
                    ResultSet rs1 = con.createStatement().executeQuery("SELECT name FROM student_info WHERE stud_id = '"+ ID +"'");
                    while(rs1.next())
                        add_row(rs1.getString("name"),Cost);
                    ID = "";
                    i++;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(non_subs_pay.this,"Some Unexpected Error Occured",Toast.LENGTH_SHORT).show();
        }
    }

    public void add_row(String name,String cost){
        tr_head = new TableRow(this);
        tr_head.setId(i*4);
        tr_head.setBackgroundColor(Color.GRAY);        // part1
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        name_col = new TextView(this);
        name_col.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        name_col.setId((i*4)+1);
        name_col.setText(name);
        name_col.setTextColor(Color.WHITE);          // part2
        name_col.setPadding(3, 3, 3, 3);
        name_col.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        tr_head.addView(name_col);

        cost_col = new TextView(this);
        cost_col.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        cost_col.setId((i*4)+2);
        cost_col.setText(cost);
        cost_col.setTextColor(Color.WHITE);          // part2
        cost_col.setPadding(3, 3, 3, 3);
        cost_col.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        tr_head.addView(cost_col);

        pay_box = new CheckBox(this);
        pay_box.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        pay_box.setId((i*4)+3);
        pay_box.setText("");
        pay_box.setTextColor(Color.WHITE);          // part2
        pay_box.setPadding(3, 3, 3, 3);
        pay_box.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        tr_head.addView(pay_box);

        pay_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()){
                    int a = view.getId();
                    try {
                        con = connectionClass.CONN();
                        if (con == null) {
                            Toast.makeText(non_subs_pay.this,"Error in connection with SQL server",Toast.LENGTH_SHORT).show();
                        } else {
                            String query = "UPDATE non_subscriber SET paid = 'YES' WHERE stud_id = '"+id.get((a-3)/4)+"' AND date = '1-30-10-2019'";
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);
                            TableRow row = findViewById(a-3);
                            t1.removeView(row);
                        }
                    } catch (Exception ex) {
                        Toast.makeText(non_subs_pay.this,"Some Unexpected Error Occured",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        t1.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,                    //part4
                TableLayout.LayoutParams.MATCH_PARENT));
    }

    public void ConnectMQTT(){
        client = new MqttAndroidClient(this.getApplicationContext(),MQTTHOST, clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(non_subs_pay.this,"Connected",Toast.LENGTH_SHORT).show();
                    Subscribe();
                    // We are connected
                    //Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(non_subs_pay.this,"Something went wrong e.g. connection timeout or firewall problems",Toast.LENGTH_SHORT).show();
                    // Something went wrong e.g. connection timeout or firewall problems
                    //Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void Subscribe(){
        String topic = "payment";
        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(non_subs_pay.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

