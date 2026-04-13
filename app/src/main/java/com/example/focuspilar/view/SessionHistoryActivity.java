package com.example.focuspilar.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focuspilar.R;
import com.example.focuspilar.model.Session;
import com.example.focuspilar.model.SessionManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Vista – Pantalla de historial de sesiones.
 * Muestra la lista de sesiones almacenadas y un empty state si no hay datos.
 * Permite filtrar por: Hoy / Esta semana / Todo.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class SessionHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;
    private ChipGroup chipGroupFilter;

    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

    /**
     * Aplica el idioma guardado antes de inflar cualquier vista.
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
        setContentView(R.layout.activity_history_session);

        // SessionManager con Context para acceder a la BD SQLite
        sessionManager = new SessionManager(this);

        bindViews();
        setupToolbar();
        setupRecyclerView();
        setupFilterLogic();

        // Cargar datos en un Background Thread para no bloquear la UI
        loadSessions(R.id.chipFilterAll);
    }

    // =========================================================================
    // Inicialización
    // =========================================================================

    private void bindViews() {
        toolbar         = findViewById(R.id.history_toolbar);
        tvResultCount   = findViewById(R.id.tvResultCount);
        layoutEmpty     = findViewById(R.id.layoutEmpty);
        recyclerView    = findViewById(R.id.recyclerViewHistory);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_history));
        }
    }

    private void setupRecyclerView() {
        adapter = new SessionHistoryAdapter(new ArrayList<>(), getResources());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // =========================================================================
    // Filtros (Hoy / Semana / Todo)
    // =========================================================================

    /**
     * Configura el listener del ChipGroup para filtrar sesiones por periodo.
     * Según el chip seleccionado, delega la consulta al SessionManager.
     */
    private void setupFilterLogic() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            // Lanzar la consulta en background según el chip seleccionado
            loadSessions(checkedIds.get(0));
        });
    }

    /**
     * Ejecuta la consulta al SessionManager en un hilo secundario y
     * actualiza la UI en el hilo principal al terminar.
     *
     * @param chipId ID del chip activo (chipFilterToday / Week / All)
     */
    private void loadSessions(int chipId) {
        new Thread(() -> {
            List<Session> result;

            if (chipId == R.id.chipFilterToday) {
                result = sessionManager.getSessionsToday();
            } else if (chipId == R.id.chipFilterWeek) {
                result = sessionManager.getSessionsThisWeek();
            } else {
                result = sessionManager.getHistory();
            }

            // Volver al hilo principal para actualizar la UI
            final List<Session> sessions = result;
            runOnUiThread(() -> updateHistoryDisplay(sessions));
        }).start();
    }

    // =========================================================================
    // Actualización de UI
    // =========================================================================

    /**
     * Muestra la lista de sesiones o el empty state según si hay datos.
     * Usa plurals para mostrar "1 sesión" o "N sesiones" correctamente.
     *
     * @param sessions Lista de sesiones a mostrar
     */
    private void updateHistoryDisplay(List<Session> sessions) {
        int count = (sessions == null) ? 0 : sessions.size();

        // Plurals: "1 session" vs "2 sessions" / "1 sesión" vs "2 sesiones"
        String countText = getResources()
                .getQuantityString(R.plurals.session_count_plural, count, count);
        tvResultCount.setText(countText);

        if (count == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new SessionHistoryAdapter(sessions, getResources());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
