package com.example.focuspilar.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.focuspilar.R;

/**
 * Fragment que aloja la pantalla de preferencias del usuario.
 * Carga la configuración definida en res/xml/preferences.xml.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
