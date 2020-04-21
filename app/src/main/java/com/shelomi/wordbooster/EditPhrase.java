package com.shelomi.wordbooster;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class EditPhrase extends  AppCompatActivity {
    private SQLiteDatabaseHandler phraseDb;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> phraseList;
    private static String selectedText;
    private EditText editPhrase;
    private SearchView filterSearch;
    private Button editButton, saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phase);
        editPhrase = findViewById(R.id.edit_phrase);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        filterSearch = findViewById(R.id.Search_filter);
        phraseDb = new SQLiteDatabaseHandler(this);
        try {
            Cursor cursor =phraseDb.getAllData();
            viewData(cursor);
        }finally {
            phraseDb.close();
        }
        // adding the filter search bar
        filterSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filtering the items on the adapter
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });


        // disabling the save button until the edittext view is empty
        editPhrase.addTextChangedListener(new EmptyTextWatcher() {
            @Override
            public void onEmpty(boolean empty) {
                if (empty) {
                    saveButton.setEnabled(false);
                } else {
                    saveButton.setEnabled(true);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting the position of the phrase selected from the listview
                selectedText = listView.getItemAtPosition(position).toString();
            }
        });

        // adding the selected word to the editphrase textview when edit button is clicked
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhrase.setText(selectedText);
            }
        });
        // saving edited word when edit button is clicked
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = phraseDb.getData();
                boolean isEdited ;
                String phrase = editPhrase.getText().toString() ;
                // checking if the word is updated
                isEdited = phraseDb.updateData(phrase,selectedText);
                // if the word is updated
                if (isEdited) {
                    Toast.makeText(EditPhrase.this, "Word Updated", Toast.LENGTH_SHORT).show();
                    // setting edit text to empty after saving the word
                    editPhrase.setText("");
                }else {
                    Toast.makeText(EditPhrase.this, "Word not updated", Toast.LENGTH_SHORT).show();
                }
                // refreshing the listview to display any changes in phrases
                viewData(cursor);
            }
        });
    }

    // method to view all the phrases through a listview.
    public void viewData(Cursor cursor) {
        phraseList = new ArrayList<>();

        // if the listview is empty
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                phraseList.add(cursor.getString(0));
            }
            try {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,phraseList );
                listView = (ListView) findViewById(R.id.word_edit_list_view);
                listView.setAdapter(arrayAdapter);

            }catch (Exception e){
                System.out.println("ERROR"+ e.getMessage());
            }

        }
    }
    private abstract class EmptyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        // assumes text is initially empty
        private boolean isEmpty = true;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                isEmpty = true;
                onEmpty(true);
            } else if (isEmpty) {
                isEmpty = false;
                onEmpty(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        public abstract void onEmpty(boolean empty);
    }



}
