package com.simon.vpohode;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {
    private static App instance = null;
    private SharedPreferences preferences;

    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Если в вашем приложении есть экран настроек, замените preferences на нужное имя файла
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    public static App getInstance(){
        if (instance == null)
            instance = new App();
        return instance;
    }

    public SharedPreferences getPreferences(){
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences;
    }
}