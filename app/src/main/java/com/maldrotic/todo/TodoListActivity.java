package com.maldrotic.todo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoListActivity extends AppCompatActivity {

    public static final int CREATE_ITEM_REQUEST_ID = 1;

    public static String ITEM_TITLE = "item_title";
    public static String ITEM_TIME = "item_time";
    public static String ITEM_DATE = "item_date";

    private ArrayList<TodoItem> todoList;
    private ListView listView;
    private TodoListAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateTodoActivity.class);
                startActivityForResult(intent, CREATE_ITEM_REQUEST_ID);
            }
        });

        listView = (ListView) findViewById(R.id.todoList);
        todoList = new ArrayList<>();
        aa = new TodoListAdapter(this, todoList);
        listView.setAdapter(aa);
        TextView empty = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ITEM_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            String title = data.getStringExtra(ITEM_TITLE);
            String time = data.getStringExtra(ITEM_TIME);
            String date = data.getStringExtra(ITEM_DATE);
            todoList.add(new TodoItem(title, time, date));
            aa.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
