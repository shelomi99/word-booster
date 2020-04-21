package com.shelomi.wordbooster;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class DisplayPhrase extends AppCompatActivity {
    private SQLiteDatabaseHandler phraseDb;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> phraseList;
    private SearchView filterSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phase);
        filterSearch = findViewById(R.id.Phrase_filter);
        phraseDb = new SQLiteDatabaseHandler(this);
        try {
            Cursor cursor =phraseDb.getData();
            // getting data to the listview
            viewData(cursor);
        }finally {
            phraseDb.close();
        }

        // adding the filter search bar
        // reference - (SearchView + List View Demo) https://www.youtube.com/watch?v=OU9PEL4e7V0&t=436s
        filterSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                arrayAdapter.getFilter().filter(text);
                return false;
            }
        });
    }

    public void viewData(Cursor cursor) {
        phraseList = new ArrayList<>();

        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                phraseList.add(cursor.getString(0));
            }
            try {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,phraseList );
                listView = (ListView) findViewById(R.id.word_list_view);
                listView.setAdapter(arrayAdapter);
            }catch (Exception e){
                System.out.println("ERROR"+ e.getMessage());
            }

        }
    }
}
