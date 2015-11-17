package com.maldrotic.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private EditText title;
    private Spinner prioritySpinner;
    private TextView timeText;
    private Button timeButton;
    private TextView dateText;
    private TextView dateButton;
    private Button deleteButton;
    private Button createButton;

    private int id;

    public static final String CAN_DELETE = "can_delete";
    private boolean canDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        canDelete = getIntent().getBooleanExtra(CAN_DELETE, false);

        if (canDelete) {
            String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TIME, TodoTable.COLUMN_PRIORITY, TodoTable.COLUMN_STATUS, TodoTable.COLUMN_TIME, TodoTable.COLUMN_DATE};
            Cursor cursor = getContentResolver().query(TodoContentProvider.CONTENT_URI, projection, TodoTable.COLUMN_TITLE, new String[] {title.getText().toString()}, TodoTable.COLUMN_TITLE );
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ID)));
            title.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TITLE)));
            timeText.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TIME)));
            dateText.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_DATE)));
        }

        title = (EditText) findViewById(R.id.title);

        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.priority, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        timeText = (TextView) findViewById(R.id.timeText);
        timeButton = (Button) findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(CreateTodoActivity.this, CreateTodoActivity.this, hour, minute, false);
                timePicker.show();
            }
        });

        dateText = (TextView) findViewById(R.id.dateText);
        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(CreateTodoActivity.this, CreateTodoActivity.this, year, month, day);
                datePicker.show();
            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(TodoContentProvider.CONTENT_URI, "_id=?", new String[]{"" + id + ""});
            }
        });
        if (!canDelete) {
            deleteButton.setVisibility(View.GONE);
        }

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(TodoTable.COLUMN_TITLE, title.getText().toString());
                values.put(TodoTable.COLUMN_PRIORITY, prioritySpinner.toString());
                values.put(TodoTable.COLUMN_TIME, timeText.getText().toString());
                values.put(TodoTable.COLUMN_DATE, dateText.getText().toString());
                values.put(TodoTable.COLUMN_STATUS, false);
                getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = monthOfYear+"-"+dayOfMonth+"-"+year;
        dateText.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay+":"+minute;
        timeText.setText(time);
    }
}
