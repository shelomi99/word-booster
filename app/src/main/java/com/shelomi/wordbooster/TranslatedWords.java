package com.shelomi.wordbooster;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import java.util.List;

public class TranslatedWords extends AppCompatActivity {
    private SQLiteDatabaseHandler phraseDb;
    private GridView gridView;
    private static String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translated_words);
        gridView = findViewById(R.id.grid_view);
        phraseDb = new SQLiteDatabaseHandler(this);

            SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(this);
            final List<String> selectedLanguages = db.getSelectedLanguages();
            ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,selectedLanguages);
            gridView = (GridView) findViewById(R.id.grid_view);
            gridView.setAdapter(languageAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectedLanguage = gridView.getItemAtPosition(position).toString();
                viewTranslatedWords();
            }
        });
    }
    public void viewTranslatedWords(){
        Cursor cursor = phraseDb.getTranslatedWords(selectedLanguage);
        if (cursor.getCount() == 0){
            showMessage("Translations in "+ selectedLanguage, "No Translated Words Yet");
        }else{
            StringBuffer buffer = new StringBuffer();
            while(cursor.moveToNext()){
                buffer.append(cursor.getString(0)+" ---> "+ cursor.getString(1)+"\n\n");
            }
            showMessage("Translations in "+ selectedLanguage+"\n", buffer.toString());
        }
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.global);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeOptionsMenu();
            }
        });
        builder.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        viewTranslatedWords();

    }
}
