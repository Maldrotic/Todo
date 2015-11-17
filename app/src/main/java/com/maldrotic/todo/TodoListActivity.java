package com.maldrotic.todo;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TodoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SORT_ORDER = "sort_order";
    private static final int TIME_SORT = 0;
    private static final int PRIORITY_SORT = 1;
    private static final int STATUS_SORT = 2;

    private ListView listView;
    private SimpleCursorAdapter adapter;

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
                intent.putExtra(CreateTodoActivity.TODO_ID, -1);
                startActivity(intent);
            }
        });

        String[] from = new String[] {TodoTable.COLUMN_TITLE};
        int[] to = new int[] {android.R.id.text1};

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.NO_SELECTION);

        listView = (ListView) findViewById(R.id.todoList);
        listView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CreateTodoActivity.class);
                Cursor cursor = (Cursor) adapter.getItem(position);
                intent.putExtra(CreateTodoActivity.TODO_ID, Integer.parseInt(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ID))));
                startActivity(intent);
            }
        });

        getLoaderManager().restartLoader(0, null, this);
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
        Bundle bundle;

        switch (id) {
            case R.id.action_settings:
                getContentResolver().delete(TodoContentProvider.CONTENT_URI, null, null);
                return true;
            case R.id.timeSort:
                bundle = new Bundle();
                bundle.putInt(SORT_ORDER, TIME_SORT);
                getLoaderManager().restartLoader(0, bundle, this);
                return true;
            case R.id.prioritySort:
                bundle = new Bundle();
                bundle.putInt(SORT_ORDER, PRIORITY_SORT);
                getLoaderManager().restartLoader(0, bundle, this);
                return true;
            case R.id.statusSort:
                bundle = new Bundle();
                bundle.putInt(SORT_ORDER, STATUS_SORT);
                getLoaderManager().restartLoader(0, bundle, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TITLE, TodoTable.COLUMN_TIME, TodoTable.COLUMN_PRIORITY, TodoTable.COLUMN_STATUS};
        String sortString = TodoTable.COLUMN_TIME;
        if (args != null) {
            int sortOrder = args.getInt(SORT_ORDER, TIME_SORT);
            if (sortOrder == TIME_SORT) {
                sortString = TodoTable.COLUMN_TIME;
            } else if (sortOrder == PRIORITY_SORT) {
                sortString = TodoTable.COLUMN_PRIORITY;
            } else {
                sortString = TodoTable.COLUMN_STATUS;
            }
        }
        CursorLoader cursorLoader = new CursorLoader(this, TodoContentProvider.CONTENT_URI, projection, null, null, sortString);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
