package com.example.focuspilar.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.focuspilar.R;

import java.util.Locale;

/**
 * Vista – Pantalla de ajustes de la aplicación.
 * Gestiona cambios de tema y de idioma a través de SharedPreferences.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class PreferencesActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toolbar toolbar;

    /**
     * Aplica el idioma guardado en SharedPreferences antes de que la Activity
     * infle cualquier vista, garantizando que todos los strings se muestren
     * en el idioma correcto desde el primer frame.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String lang = prefs.getString(
                newBase.getString(R.string.lang_preference_key).trim(), "es");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        toolbar = findViewById(R.id.preferences_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.btn_preferences));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_content, new PreferencesFragment())
                .commit();
    }

    // =========================================================================
    // Ciclo de vida – registro del listener
    // =========================================================================

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // =========================================================================
    // Cambios en preferencias
    // =========================================================================

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;

        String langKey  = getString(R.string.lang_preference_key).trim();
        String themeKey = getString(R.string.theme_preference_key).trim();

        if (key.equals(langKey)) {
            String lang = sharedPreferences.getString(key, "es");
            applyLanguage(lang);
            recreate(); // Reinicia la Activity para aplicar el nuevo idioma

        } else if (key.equals(themeKey)) {
            String theme = sharedPreferences.getString(key, "light");
            applyTheme(theme);
            recreate(); // Reinicia la Activity para aplicar el nuevo tema visualmente
        }
    }

    /**
     * Aplica el idioma actualizando la Configuration y los recursos de la Activity.
     */
    @SuppressWarnings("deprecation")
    private void applyLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * Aplica el tema de forma global a toda la aplicación.
     */
    private void applyTheme(String theme) {
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
