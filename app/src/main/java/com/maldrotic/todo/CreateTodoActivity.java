package com.maldrotic.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CreateTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private AutoCompleteTextView title;
    private Spinner prioritySpinner;
    private TextView timeText;
    private Button timeButton;
    private TextView dateText;
    private TextView dateButton;
    private RadioGroup statusRadioGroup;
    private RadioButton notDoneRadioButton;
    private RadioButton doneRadioButton;
    private Button deleteButton;
    private Button createButton;
    private int status;

    public static final String TODO_ID = "todoId";
    private int todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get calendar info
        Calendar c = Calendar.getInstance();
        final int minute = c.get(Calendar.MINUTE);
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        // Get all of the UI elements
        title = (AutoCompleteTextView) findViewById(R.id.title);
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.simple_dropdown_item_1line, new String[] {});
        title.setAdapter(adapter);

        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.priority, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        // Set the time text to the current time
        timeText = (TextView) findViewById(R.id.timeText);
        timeText.setText(hour + ":" + minute);
        timeButton = (Button) findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(CreateTodoActivity.this, CreateTodoActivity.this, hour, minute, false);
                timePicker.show();
            }
        });

        // Set the date text to the current date
        dateText = (TextView) findViewById(R.id.dateText);
        dateText.setText(month + "-" + day + "-" + year);
        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(CreateTodoActivity.this, CreateTodoActivity.this, year, month, day);
                datePicker.show();
            }
        });

        // Set up the radio buttons
        status = TodoTable.NOT_DONE;
        notDoneRadioButton = (RadioButton) findViewById(R.id.notDoneRadioButton);
        notDoneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = TodoTable.NOT_DONE;
            }
        });
        doneRadioButton = (RadioButton) findViewById(R.id.doneRadioButton);
        doneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = TodoTable.DONE;
            }
        });
        statusRadioGroup = (RadioGroup) findViewById(R.id.statusRadioGroup);
        statusRadioGroup.check(R.id.notDoneRadioButton);

        // Setup the delete button, show if not a new entry
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(TodoContentProvider.CONTENT_URI, "_id=?", new String[]{todoId + ""});
                Toast.makeText(CreateTodoActivity.this, "Todo item was deleted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Set up the create button
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(TodoTable.COLUMN_TITLE, title.getText().toString());
                values.put(TodoTable.COLUMN_PRIORITY, prioritySpinner.getSelectedItemPosition());
                values.put(TodoTable.COLUMN_TIME, timeText.getText().toString());
                values.put(TodoTable.COLUMN_DATE, dateText.getText().toString());
                values.put(TodoTable.COLUMN_STATUS, status);
                if (todoId == -1) {
                    getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
                } else {
                    getContentResolver().update(TodoContentProvider.CONTENT_URI, values, "_id=?", new String[] {todoId+""});
                }
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(CreateTodoActivity.this, CreateTodoActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(CreateTodoActivity.this, 0, intent, 0);
                Notification notification = new Notification.Builder(CreateTodoActivity.this)
                        .setContentTitle("Todo: " + title.getText().toString())
                        .setContentText("Time to do your todo!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                nm.notify(0, notification);
                Toast.makeText(CreateTodoActivity.this, "Todo item was created.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Check if this is new entry, if not, populate fields
        todoId = getIntent().getIntExtra(TODO_ID, -1);
        if (todoId != -1) {
            String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TITLE, TodoTable.COLUMN_PRIORITY, TodoTable.COLUMN_STATUS, TodoTable.COLUMN_TIME, TodoTable.COLUMN_DATE};
            Cursor cursor = getContentResolver().query(TodoContentProvider.CONTENT_URI, projection, "_id=?", new String[] {todoId +""}, TodoTable.COLUMN_TITLE );
            cursor.moveToFirst();
            title.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TITLE)));
            prioritySpinner.setSelection(cursor.getInt(cursor.getColumnIndex(TodoTable.COLUMN_PRIORITY)));
            timeText.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TIME)));
            dateText.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_DATE)));
            statusRadioGroup.check(cursor.getInt(cursor.getColumnIndex(TodoTable.COLUMN_STATUS)) == TodoTable.NOT_DONE ? R.id.notDoneRadioButton : R.id.doneRadioButton);
            deleteButton.setVisibility(View.VISIBLE);
            cursor.close();
        }
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
