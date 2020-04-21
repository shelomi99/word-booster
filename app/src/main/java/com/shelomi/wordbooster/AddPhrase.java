package com.shelomi.wordbooster;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPhrase extends AppCompatActivity{
    private EditText addPhrase;
    private Button buttonSavePhrase;
    private SQLiteDatabaseHandler phraseDb;
    ListView listSavedPhrases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phase);

        phraseDb = new SQLiteDatabaseHandler(this);
        addPhrase = findViewById(R.id.add_phase);
        buttonSavePhrase = findViewById(R.id.button_save_phrase);
        listSavedPhrases = (ListView) findViewById(R.id.list_item);
        addPhaseToDataBase();

        // disabling the save button until the edittext view is empty
        addPhrase.addTextChangedListener(new EmptyTextWatcher() {
            @Override
            public void onEmpty(boolean empty) {
                if (empty) {
                    buttonSavePhrase.setEnabled(false);
                } else {
                    buttonSavePhrase.setEnabled(true);
                }
            }
        });
    }

    // adding the phrases to the database
    public void addPhaseToDataBase(){
        buttonSavePhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // getting the phrase through a cursor
                    Cursor cursor = phraseDb.getData();
                    boolean isInserted ;
                    String phrase = addPhrase.getText().toString().toLowerCase();
                    // validating the entered phrase
                    if (!validateInput(cursor,phrase) && phrase.length()!=0 ) {
                        String regex = "^[a-zA-Z ]*$";
                        //Compiling the regular expression
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(phrase);
                        if(matcher.matches()) {
                            isInserted = phraseDb.insertData(phrase);
                            if (isInserted) {
                                Toast.makeText(AddPhrase.this, "Phrase inserted", Toast.LENGTH_SHORT).show();
                                // emptying the textview after saving the word
                                addPhrase.setText("");
                            } else {
                                Toast.makeText(AddPhrase.this, "Phrase Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(AddPhrase.this, "Add a valid Phrase", Toast.LENGTH_SHORT).show();
                        }
                    }
                }finally {
                    phraseDb.close();
                }
            }
        });
    }

    // method to validate the  phrase
    private boolean validateInput(Cursor cursor, String newPhrase){
        boolean isAvaliable = false;
        if(cursor.getCount() == 0){
            isAvaliable = false;
        }else {
            while (cursor.moveToNext()){
                if (newPhrase.equals(cursor.getString(0))){
                    Toast.makeText(this,"Word already exists in the database",Toast.LENGTH_SHORT).show();
                    isAvaliable =true;
                }
            }
        }
        System.out.println(isAvaliable);
        return isAvaliable;
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
