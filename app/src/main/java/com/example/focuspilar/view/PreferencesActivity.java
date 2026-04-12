package com.example.focuspilar.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.focuspilar.R;

/**
 * Vista – Pantalla de ajustes de la aplicación.
 * Gestiona cambios de tema y de idioma a través de SharedPreferences.
 *
 * TODO pendiente:
 *   - Completar applyLanguage() con la configuración de Locale.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class PreferencesActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toolbar toolbar;

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

        // Reemplaza el contenedor con el Fragment de preferencias
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

        if (key.equals(getString(R.string.lang_preference_key))) {
            String lang = sharedPreferences.getString(key, "es");
            applyLanguage(lang);
            recreate();

        } else if (key.equals(getString(R.string.theme_preference_key))) {
            String theme = sharedPreferences.getString(key, "light");
            applyTheme(theme);
        }
    }

    /**
     * TODO: Implementar cambio de idioma.
     * Pasos:
     *  1. Crear un Locale con el código recibido.
     *  2. Llamar Locale.setDefault(locale).
     *  3. Obtener la Configuration actual con getResources().getConfiguration().
     *  4. Actualizar con configuration.setLocale(locale).
     *  5. Llamar createConfigurationContext(configuration).
     *  6. La Activity se re-crea sola gracias al recreate() en onSharedPreferenceChanged.
     */
    private void applyLanguage(String lang) {
        // Pendiente de implementar
    }

    /**
     * Aplica el tema seleccionado por el usuario (claro / oscuro / sistema).
     *
     * @param theme Valor de la preferencia: "light", "dark" o cualquier otro valor.
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
