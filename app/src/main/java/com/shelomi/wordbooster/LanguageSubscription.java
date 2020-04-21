package com.shelomi.wordbooster;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class LanguageSubscription extends AppCompatActivity {
    private static final String LOG_TAG = LanguageSubscription.class.getSimpleName();
    private SQLiteDatabaseHandler phraseDb;
    private List<String> phraseList;
    private List<Integer> positionList;
    private ArrayList<Integer> positions;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private Button updateButton;
    private ArrayList<String> selectedLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);

        listView = (ListView) findViewById(R.id.language_list);
        updateButton = (Button) findViewById(R.id.button_update);
        phraseDb = new SQLiteDatabaseHandler(this);
        try {
            Cursor cursor =phraseDb.getAllLanguages();
            // getting all the languages from the database to a listview
            viewData(cursor);
            Cursor cursor1 =phraseDb.getSelectedLanguagePositions();
            // get the subscribed languages and checking all the selected items the listview
            checkSelectedItems(cursor1);
            // notifying any changes of the listview
            arrayAdapter.setNotifyOnChange(true);
        }finally {
            phraseDb.close();
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // deleting the unchecked languages from the database
                phraseDb.deleteLanguages();
                int len = listView.getCount();
                // getting the checked languages from the listview to a sparseboolean array
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                selectedLanguages = new ArrayList<>();
                positions = new ArrayList<>();
                for (int position = 0; position < len; position++) {
                    if (checked.get(position)) {
                        String item = phraseList.get(position);
                        // adding the language name to an arraylist
                        selectedLanguages.add(item);
                        // adding the position of the checked language s to an arraylist
                        positions.add(position);
                        System.out.println(checked.indexOfValue(true));
                    }
                }
                System.out.println(selectedLanguages);
                System.out.println(positions);
                // inserting subcribed languages with position to the database
                phraseDb.insertSelectedLanguages(selectedLanguages,positions);
                Log.v(LOG_TAG,"Subscribed languages Updated");
                Toast.makeText(LanguageSubscription.this, "Subscribed languages Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // method to show all the languages to the listview
    public void viewData(Cursor cursor) {
        phraseList = new ArrayList<>();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                phraseList.add(cursor.getString(0));
            }
            try {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,phraseList );
                listView.setAdapter(arrayAdapter);
            }catch (Exception e){
                System.out.println("ERROR"+ e.getMessage());
            }
        }
    }

    // method to tick all the already selected languages in the listview
    private void checkSelectedItems(Cursor cursor) {
        positionList = new ArrayList<>();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No Languages are subscribed yet", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()) {
                positionList.add(cursor.getInt(1));
            }
            System.out.println(positionList);
            for (int j= 0; j<positionList.size();j++){
                listView.setItemChecked(positionList.get(j),true);
            }
            arrayAdapter.setNotifyOnChange(true);
            }
        }

    }


