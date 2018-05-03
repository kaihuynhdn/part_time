package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class PickLocationActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String[] strings;
    private ArrayList<String> arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addComponents();
        initialize();
        addEvents();
    }

    private void initialize() {
        arraylist = new ArrayList<>();
        strings = getResources().getStringArray(R.array.location);
        Collections.addAll(arraylist, strings);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraylist);
        listView.setAdapter(adapter);
    }

    private void addEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("location", arraylist.get(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void addComponents() {
        listView = findViewById(R.id.lv_location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_menu, menu);

        MenuItem item = menu.findItem(R.id.menuLocationSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Nhập từ khóa");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> list = new ArrayList<>();
                Collections.addAll(list, strings);
                s = s.toLowerCase(Locale.getDefault());
                arraylist.clear();
                if (s.length() == 0) {
                    arraylist.addAll(list);
                } else {
                    for (String string : list) {
                        if (string.toLowerCase(Locale.getDefault()).contains(s)) {
                            arraylist.add(string);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        magImage.setVisibility(View.GONE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
