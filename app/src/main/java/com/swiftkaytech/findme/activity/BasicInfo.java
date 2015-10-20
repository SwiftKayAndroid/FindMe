package com.swiftkaytech.findme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.utils.VarHolder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kevin Haines on 4/27/2015.
 */
public class BasicInfo extends Activity{

    //gui elements
    private TextView tvdate;        //holds users date of birth
    private EditText etfirstname;   //holds users first name
    private EditText etlastname;    //holds users last name
    private TextView genderet;      //holds users gender. shows alertDialog when clicked to select gender
    private Button btnnext;         //check for correct input in all fields. Sends user to Registration.java

    //String Values
    private String dob;             //users date of birth, retrieved from tvdate
    private String gender;          //users gender, retrieved from genderet
    private String firstname;       //users firstname, retrieved from etfirstname
    private String lastname;        //users lastname, retrieved from

    //date formatting
    private DateFormat dateFormat;
    private Calendar calendar;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar
        setContentView(R.layout.basicinfo);

        //initialize gui elements
        tvdate = (TextView) findViewById(R.id.tvregdate);
        etfirstname = (EditText) findViewById(R.id.etregfirstname);
        etlastname = (EditText) findViewById(R.id.etreglastname);
        btnnext = (Button) findViewById(R.id.btnbasicinfosubmit);
        genderet = (TextView) findViewById(R.id.etgenderselect);

        //select gender
        genderet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelectPoP();
            }
        });

        //select date of birth
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(); //update tvdate to show selected date
            }

        };

        //pop up the dialog box that contains the datepicker view
        tvdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(BasicInfo.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //check for proper values and move to Registration.java
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVariables(); //sets the proper values and moves to Registration.java
            }
        });
    }

    //update the text view containing the date
    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvdate.setText(sdf.format(myCalendar.getTime()));
    }

    //pop up an alert dialog to select the gender
    private void genderSelectPoP(){

        String message = "Select Gender";
        new AlertDialog.Builder(BasicInfo.this)
                .setMessage(message)
                .setNegativeButton("Male", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        gender = "Male";
                        genderet.setText(gender);
                    }
                })
                .setPositiveButton("Female", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        gender = "Female";
                        genderet.setText(gender);
                    }
                })
                .show();
    }

    //set the variables held in the VarHolder class
    public void setVariables(){
        VarHolder.firstname = firstname = etfirstname.getText().toString();
        VarHolder.lastname = lastname = etlastname.getText().toString();
        VarHolder.dob = dob = tvdate.getText().toString();
        VarHolder.gender = gender = genderet.getText().toString();

        if(VarHolder.firstname.equals("")){
            Toast.makeText(this,"Please enter firstname",Toast.LENGTH_LONG).show();
        }else if(VarHolder.lastname.equals("")){
            Toast.makeText(this,"Please enter lastname",Toast.LENGTH_LONG).show();
        }else if(VarHolder.dob.equals("")){
            Toast.makeText(this,"Please select Date of Birth",Toast.LENGTH_LONG).show();
        }else if(VarHolder.gender.equals("")){
            Toast.makeText(this,"Please select gender",Toast.LENGTH_LONG).show();
        }else {
            Intent i = new Intent("com.swiftkaytech.findme.REGISTRATION");
            startActivity(i);
        }
    }
}