package com.shelomi.wordbooster;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import java.util.ArrayList;
import java.util.List;

public class TranslatePhrase extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    private static final String LOG_TAG = TranslatePhrase.class.getSimpleName();
    private SQLiteDatabaseHandler phraseDb;
    private List<String> phraseList;
    private ArrayAdapter<String> arrayAdapter;
    private String selectedPhrase;
    private int selectedItemPosition;
    private static String selectedLanguage,selectedLanguageCode,translation;
    private ListView listView;
    private Spinner languageSpinner;
    private Button translateButton;
    private TextView translatedPhrase;
    private LanguageTranslator translationService;
    private TextToSpeech textService;
    private ImageView speakerImage,imageView;
    private SearchView filterSearch;
    private StreamPlayer player = new StreamPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_phase);
        languageSpinner = findViewById(R.id.language_spinner);
        translateButton = findViewById(R.id.translate_button);
        translatedPhrase = findViewById(R.id.translated_Phrase);
        speakerImage = findViewById(R.id.microphoneButton);
        filterSearch = findViewById(R.id.Phrase_filter);
        imageView = findViewById(R.id.imageView6);
        phraseDb = new SQLiteDatabaseHandler(this);
        try {
            Cursor cursor =phraseDb.getData();
            // displaying saved phrases in a list view
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
            public boolean onQueryTextChange(String text) {
                arrayAdapter.getFilter().filter(text);
                return false;
            }
        });

        loadSpinnerData();
        translationService = initLanguageTranslatorService();
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPhrase = listView.getItemAtPosition(position).toString();
                selectedItemPosition = position;
                translateButton.setEnabled(true);
            }
        });
        // translating the phrase
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateGivenPhrase();
            }
        });
        textService = initTextToSpeechService();
        // converting the text to speech
        speakerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String translatedWord = (String) translatedPhrase.getText();
                try {
                    new SynthesisTask().execute(translatedWord);
                }catch (Exception e){
                    System.out.println("ERROR"+ e.getMessage());
                }

            }
        });

    }
    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.text_speech_apikey));
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(getString(R.string.text_speech_url));
        return service;
    }

    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.language_translator_apikey));
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
        service.setServiceUrl(getString(R.string.language_translator_url));
        return service;

    }

    private void TranslateGivenPhrase() {
        selectedLanguage = languageSpinner.getSelectedItem().toString();
        selectedLanguageCode = phraseDb.getSelectedLanguageCode(selectedLanguage);
        Log.d(LOG_TAG,"selected phrase is "+selectedPhrase);
        Log.d(LOG_TAG,"selected language is "+selectedLanguage);
        Log.d(LOG_TAG,"code of the selected language is "+selectedLanguageCode);
        if(selectedPhrase != null){
            new TranslationTask().execute(selectedPhrase);
        }else{
            Log.d(LOG_TAG, "Select a Phrase");
        }
    }

    // adding selected languages to the spinner from the database
    // reference - (Android Populating Spinner data from SQLite Database ) https://www.androidhive.info/2012/06/android-populating-spinner-data-from-sqlite-database/
    private void loadSpinnerData() {
        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(this);
        List<String> selectedLanguages = db.getSelectedLanguages();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selectedLanguages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        if (languageSpinner != null) {
            languageSpinner.setAdapter(dataAdapter);
        }
    }

    // method to show phrases in the listview
    public void viewData(Cursor cursor) {
        phraseList = new ArrayList<>();

        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                phraseList.add(cursor.getString(0));
            }
            try {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,phraseList );
                listView = (ListView) findViewById(R.id.word_translate_list_view);
                listView.setAdapter(arrayAdapter);
            }catch (Exception e){
                System.out.println("ERROR"+ e.getMessage());
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String spinnerLabel = parent.getItemAtPosition(position).toString();
        Log.d(LOG_TAG, " Chosen language is " + spinnerLabel);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(LOG_TAG, "No language selected");
    }

    // class which translates the chosen phrase
    private class TranslationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(Language.ENGLISH)
                        // setting the target language from the spinner
                        .target(selectedLanguageCode)
                        .build();
                TranslationResult result = translationService.translate(translateOptions).execute().getResult();
                String translation = result.getTranslations().get(0).getTranslation();
                return translation;
            }catch (Exception e){
                Log.d(LOG_TAG,"Cannot translate to the selected language");
                return "Error";
            }
        }
        @Override
        protected void onPostExecute(String translatedWord) {
            super.onPostExecute(translatedWord);
            boolean isInserted ;
            // setting the translated word to the textview
            System.out.println(selectedLanguage);
            System.out.println(selectedPhrase);
            translation = translatedWord;
            System.out.println(translation);
            isInserted = phraseDb.insertTranslatedLanguages(selectedPhrase,selectedLanguage,translation);
            if (isInserted){
                Toast.makeText(TranslatePhrase.this, "Translated Phrase inserted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(TranslatePhrase.this, "Phrase not inserted", Toast.LENGTH_SHORT).show();
            }
            translatedPhrase.setText(translation);
            imageView.setVisibility(View.VISIBLE);
            translatedPhrase.setVisibility(View.VISIBLE);
        }
    }

    //class witch converts a phrase to speech
    private class SynthesisTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(params[0])
                    .voice(SynthesizeOptions.Voice.ES_ES_ENRIQUEV3VOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                    .build(); player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());
            return "Did synthesize";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TranslatedWord",translation );
        outState.putInt("SelectedPhrasePosition",selectedItemPosition);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        translation = savedInstanceState.getString("TranslatedWord",translation);
        selectedItemPosition = savedInstanceState.getInt("SelectedPhrasePosition",selectedItemPosition);
        translatedPhrase.setText(translation);
        translatedPhrase.setVisibility(View.VISIBLE);
        listView.setItemChecked(selectedItemPosition,true);
        translateButton.setEnabled(true);
    }


}
