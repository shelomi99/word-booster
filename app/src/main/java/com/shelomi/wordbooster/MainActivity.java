package com.shelomi.wordbooster;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private int duration = Toast.LENGTH_SHORT;
    private Toast toast;
    private Button buttonAddPhrase;
    private Button buttonDisplayPhrase;
    private Button buttonEditPhrase;
    private Button buttonSubscriptionPhrase;
    private Button buttonTranslatePhrase;
    private Button buttonTranslatedWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddPhrase = findViewById(R.id.button_add_phrase);
        buttonDisplayPhrase = findViewById(R.id.button_display_phrase);
        buttonEditPhrase = findViewById(R.id.button_edit_phrase);
        buttonSubscriptionPhrase = findViewById(R.id.button_subscription);
        buttonTranslatePhrase = findViewById(R.id.button_translate);
        buttonTranslatedWords = findViewById(R.id.button_translated_words);

        buttonAddPhrase.setOnClickListener(this);
        buttonDisplayPhrase.setOnClickListener(this);
        buttonEditPhrase.setOnClickListener(this);
        buttonSubscriptionPhrase.setOnClickListener(this);
        buttonTranslatePhrase.setOnClickListener(this);
        buttonTranslatedWords.setOnClickListener(this);
    }

    public void launchAddPhaseActivity(View view) {
        Log.d(LOG_TAG, "Add Phrase Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Add Phrase Button clicked!";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void launchDisplayPhaseActivity(View view) {
        Log.d(LOG_TAG, "Display Phrase Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Display Phrase Button clicked!";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void launchEditPhaseActivity(View view) {
        Log.d(LOG_TAG, "Edit Phrase Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Edit Phrase Button clicked!";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void launchLanguageSubscriptionActivity(View view) {
        Log.d(LOG_TAG, "Language Subscription Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Language Subscription Button clicked!";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void launchTranslateActivity(View view) {
        Log.d(LOG_TAG, "Translate Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Translate Button clicked!";
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public void launchTranslatedWordsActivity(View view) {
        Log.d(LOG_TAG, "Translated words Button clicked!");
        Context context = getApplicationContext();
        CharSequence text = "Translated words Button clicked!";
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // adding log tags when a button is clicked
    // starting each activity
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_phrase:
                launchAddPhaseActivity(view);
                Intent addPhaseIntent = new Intent(this, AddPhrase.class);
                startActivity(addPhaseIntent);
                break;
            case R.id.button_display_phrase:
                launchDisplayPhaseActivity(view);
                Intent displayPhaseIntent = new Intent(this, DisplayPhrase.class);
                startActivity(displayPhaseIntent);
                break;
            case R.id.button_edit_phrase:
                launchEditPhaseActivity(view);
                Intent editPhaseIntent = new Intent(this, EditPhrase.class);
                startActivity(editPhaseIntent);
                break;
            case R.id.button_subscription:
                launchLanguageSubscriptionActivity(view);
                Intent languageSubscriptionIntent = new Intent(this, LanguageSubscription.class);
                startActivity(languageSubscriptionIntent);
                break;
            case R.id.button_translate:
                launchTranslateActivity(view);
                Intent translatePhraseIntent = new Intent(this, TranslatePhrase.class);
                startActivity(translatePhraseIntent);
                break;
            case R.id.button_translated_words:
                launchTranslatedWordsActivity(view);
                Intent translatedWordsIntent = new Intent(this, TranslatedWords.class);
                startActivity(translatedWordsIntent);
                break;
        }



    }
}
