package uqac.inf872.projet.imok.utils;

import android.content.Context;
import android.content.SharedPreferences;

import uqac.inf872.projet.imok.R;

public class Preference {

    private static Preference single_instance;

    private static Context sContext;
    private static SharedPreferences sSettings;

    public Preference(Context context) {
        sContext = context;
        sSettings = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public static Preference getInstance(Context context) {
        if ( single_instance == null )
            single_instance = new Preference(context);

        return single_instance;
    }

    private SharedPreferences.Editor getEditor() {
        return sSettings.edit();
    }

    public void apply() {
        getEditor().apply();
    }

    public void putBoolean(int key, boolean value) {
        getEditor().putBoolean(sContext.getString(key), value);
    }

    public boolean getBoolean(int key, boolean defValue) {
        return sSettings.getBoolean(sContext.getString(key), defValue);
    }

    public void putFloat(int key, float value) {
        getEditor().putFloat(sContext.getString(key), value);
    }

    public float getFloat(int key, float defValue) {
        return sSettings.getFloat(sContext.getString(key), defValue);
    }

    public void putLong(int key, long value) {
        getEditor().putLong(sContext.getString(key), value);
    }

    public long getLong(int key, long defValue) {
        return sSettings.getLong(sContext.getString(key), defValue);
    }

    public void putInt(int key, int value) {
        getEditor().putInt(sContext.getString(key), value);
    }

    public int getInt(int key, int defValue) {
        return sSettings.getInt(sContext.getString(key), defValue);
    }

    public void putString(int key, String value) {
        getEditor().putString(sContext.getString(key), value);
    }

    public String getString(int key, String defValue) {
        return sSettings.getString(sContext.getString(key), defValue);
    }
}
