package com.shelomi.wordbooster;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import static android.provider.BaseColumns._ID;
import static com.shelomi.wordbooster.Constants.CHOSEN_LANGUAGE;
import static com.shelomi.wordbooster.Constants.CHOSEN_PHRASE;
import static com.shelomi.wordbooster.Constants.CODE;
import static com.shelomi.wordbooster.Constants.LANGUAGE_NAME;
import static com.shelomi.wordbooster.Constants.LANGUAGE_POSITION;
import static com.shelomi.wordbooster.Constants.NAME;
import static com.shelomi.wordbooster.Constants.PHRASE;
import static com.shelomi.wordbooster.Constants.SELECTED_LANGUAGES;
import static com.shelomi.wordbooster.Constants.TABLE_LANGUAGES_NAME;
import static com.shelomi.wordbooster.Constants.TABLE_NAME;
import static com.shelomi.wordbooster.Constants.TRANSLATED_PHRASES;
import static com.shelomi.wordbooster.Constants.TRANSLATED_WORD;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = " phrases.db ";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabaseHandler ( Context context) {
        super (context , DATABASE_NAME , null , DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating a database to store the phrases added
        db. execSQL (" CREATE TABLE " + TABLE_NAME + " ("
                + PHRASE + " TEXT PRIMARY KEY NOT NULL );");

        // creating a database to store all the languages available
        db. execSQL (" CREATE TABLE " + TABLE_LANGUAGES_NAME + " ("
                + NAME + " TEXT PRIMARY KEY NOT NULL ," + CODE + " TEXT"+ ");");

        // creating a database to store all the subscribed languages
        db.execSQL(" CREATE TABLE " + SELECTED_LANGUAGES + " ("
                + LANGUAGE_NAME + " TEXT NOT NULL ,"+ LANGUAGE_POSITION + " INT NOT NULL"+" );");

        // create a table to store translated words and the language
        db.execSQL(" CREATE TABLE "+ TRANSLATED_PHRASES+" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + CHOSEN_LANGUAGE + " TEXT NOT NULL  , "
                + CHOSEN_PHRASE + " TEXT NOT NULL  , "
                + TRANSLATED_WORD + " TEXT NOT NULL );");

        // executing a rollback by using a transaction for inserting a large amount of data to the database
        // reference (add large amount of data in SQLite android)-https://stackoverflow.com/questions/27226105/add-large-amount-of-data-in-sqlite-android
        db.beginTransaction();
        try {
            insertLanguages(db);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exist
        db. execSQL (" DROP TABLE IF EXISTS " + TABLE_NAME );
        db. execSQL (" DROP TABLE IF EXISTS " + TABLE_LANGUAGES_NAME );
        db. execSQL (" DROP TABLE IF EXISTS " + SELECTED_LANGUAGES );
        db. execSQL (" DROP TABLE IF EXISTS " + TRANSLATED_PHRASES );
        // Create tables again
        onCreate (db );
    }


    // method to check if the new phrase is added to the database.
    public boolean insertData(String phrase){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHRASE,phrase);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // method to get all the phrases from the database.
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+ " ORDER BY "+PHRASE+" ASC ",null);
        return cursor;
    }

    // getting a certain phrase from the database.
    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,PHRASE);
        return cursor;
    }

    // updating the database
    public boolean updateData(String editedWord,String selectedWord){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] newPhrases = {selectedWord};
        contentValues.put("PHRASE",editedWord);
        db.update(TABLE_NAME,contentValues,"PHRASE = ?",newPhrases);
        return true;
    }

    // deleting the un-subscribed languages from the database.
    public void deleteLanguages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ SELECTED_LANGUAGES);

    }
    // getting the subscribed languages from the database to a list.
    public List<String> getSelectedLanguages(){
        List<String> selectedLanguages = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+SELECTED_LANGUAGES ,null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                selectedLanguages.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return selectedLanguages;
    }

    // getting the language code for the specific language.
    public String getSelectedLanguageCode(String languageName){
        String selectedCode= null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ CODE+" FROM "+TABLE_LANGUAGES_NAME+" WHERE "+NAME+" = "+ "'"+languageName+"'",null);
        if (cursor.moveToFirst()){
            do {
                selectedCode = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return selectedCode;
    }


    // inserting subscribed languages with their position to the database
    public void insertSelectedLanguages(ArrayList selectedLanguages,ArrayList selectedPositions){
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < selectedLanguages.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(LANGUAGE_NAME, (String) selectedLanguages.get(i));
            contentValues.put(LANGUAGE_POSITION, (Integer) selectedPositions.get(i));
            db.insert(SELECTED_LANGUAGES, null, contentValues);
        }
    }

    // getting the listview position of the language from the database
    public Cursor getSelectedLanguagePositions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SELECTED_LANGUAGES,null,null,null,null,null,LANGUAGE_POSITION);
        return cursor;
    }


    // method to get all the language from the database
    public Cursor getAllLanguages() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_LANGUAGES_NAME+" ORDER BY "+ NAME+ " ASC ",null);
        return cursor;
    }

    // method to include languages to the database
    public void insertLanguages(SQLiteDatabase db){
        String language[]={"Afrikaans", "Albanian", "Arabic", "Armenian", "Azerbaijani","Bashkir",
                "Basque", "Belarusian", "Bengali","Bosnian", "Bulgarian", "Catalan","Central Khmer", "Chinese (Simplified)",
                "Chinese (Traditional)", "Chuvash", "Croatian","Czech", "Danish", "Dutch","English", "Esperanto",
                "Estonian", "Finnish", "French","Georgian", "German", "Greek","Gujarati", "Haitian",
                "Hebrew", "Hindi", "Hungarian","Icelandic", "Indonesian", "Irish","Italian", "Japanese",
                "Kazakh", "Kirghiz", "Korean","Kurdish", "Latvian", "Lithuanian","Malay", "Malayalam",
                "Maltese", "Mongolian", "Norwegian Bokmal", "Norwegian Nynorsk", "Panjabi","Persian", "Polish",
                "Portuguese", "Pushto", "Romanian","Russian", "Serbian", "Slovakian","Slovenian", "Somali",
                "Spanish", "Swedish", "Tamil","Telugu", "Thai", "Turkish","Ukrainian", "Urdu", "Vietnamese"
        };
        String code[]={"af", "sq", "ar", "hy","az", "ba", "eu", "be","bn", "bs", "bg", "ca","km", "zh", "zh-TW", "cv",
                "hr", "cs", "da", "nl","en", "eo", "et", "fi","fr", "ka", "de", "el","gu", "ht", "he", "hi","hu", "is", "id", "ga",
                "it", "ja","kk", "ky", "ko", "ku","lv", "lt","ms", "ml", "mt", "mn","nb", "nn","pa", "fa", "pl", "pt",
                "ps", "ro", "ru", "sr","sk", "sl", "so", "es","sv", "ta", "te", "th","tr", "uk", "ur","vi"
        };
        ContentValues contentValues = new ContentValues();
        for(int i=0; i<language.length; i++){
            contentValues.put("NAME", language[i]);
            contentValues.put("CODE", code[i]);
            db.insert(TABLE_LANGUAGES_NAME, null, contentValues);

        }
    }

    // method to add translated phrases to the database.
    public boolean insertTranslatedLanguages(String selectedPhrase,String selectedLanguage, String TranslatedWord ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHOSEN_LANGUAGE,selectedLanguage);
        contentValues.put(CHOSEN_PHRASE,selectedPhrase);
        contentValues.put(TRANSLATED_WORD,TranslatedWord);
        long result = db.insert(TRANSLATED_PHRASES, null, contentValues);
        return result != -1;
    }

    public Cursor getTranslatedWords(String selectedLanguage){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ CHOSEN_PHRASE+" ,"+TRANSLATED_WORD+" FROM "+TRANSLATED_PHRASES+" WHERE "+CHOSEN_LANGUAGE+" = "+ "'"+selectedLanguage+"'",null);
        return cursor;
    }


}
