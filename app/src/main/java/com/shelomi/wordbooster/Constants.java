package com.shelomi.wordbooster;
import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = " PHRASES " ;
    // Columns in the phrases database
    public static final String PHRASE = "PHRASE";

    public static final String TABLE_LANGUAGES_NAME = " LANGUAGES " ;
    // Columns in the languages database
    public static final String NAME = "NAME";
    public static final String CODE = "CODE";

    public static final String SELECTED_LANGUAGES = " SELECTEDLANGUAGE " ;
    // Columns in the selected_languages database
    public static final String LANGUAGE_NAME = "NAME";
    public static final String LANGUAGE_POSITION = "POSITION";

    public static final String TRANSLATED_PHRASES = " TRANSLATIONS " ;
    // Columns in the translations database
    public static final String CHOSEN_LANGUAGE = "LANGUAGE";
    public static final String CHOSEN_PHRASE = "ENGLISH_WORD";
    public static final String TRANSLATED_WORD = "TRANSLATED_WORD";

}