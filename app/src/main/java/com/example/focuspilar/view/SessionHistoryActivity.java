package com.example.focuspilar.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focuspilar.R;
import com.example.focuspilar.model.Session;
import com.example.focuspilar.model.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista – Pantalla de historial de sesiones.
 * Muestra la lista de sesiones almacenadas y un empty state si no hay datos.
 *
 * TODOs pendientes:
 *   - Conectar SessionManager con SQLite para cargar datos reales.
 *   - Implementar setupFilterLogic() con ChipGroup (Hoy / Semana / Todo).
 *   - Usar getQuantityString() (plurals) para "1 sesión" vs "N sesiones".
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class SessionHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;

    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_session);

        sessionManager = new SessionManager();

        bindViews();
        setupToolbar();
        setupRecyclerView();
        setupFilterLogic();
        updateHistoryDisplay(sessionManager.getHistory());
    }

    // =========================================================================
    // Inicialización
    // =========================================================================

    private void bindViews() {
        toolbar       = findViewById(R.id.history_toolbar);
        tvResultCount = findViewById(R.id.tvResultCount);
        layoutEmpty   = findViewById(R.id.layoutEmpty);
        recyclerView  = findViewById(R.id.recyclerViewHistory);
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

    /**
     * TODO: Implementar filtrado por periodo usando ChipGroup.
     * - Declarar chipGroupFilter, chipFilterToday, chipFilterWeek, chipFilterAll.
     * - Agregar listener setOnCheckedStateChangeListener() al grupo.
     * - Según el chip activo, llamar al método correspondiente del SessionManager.
     * - Actualizar el adapter con adapter.notifyDataSetChanged().
     */
    private void setupFilterLogic() {
        // Pendiente de implementar
    }

    // =========================================================================
    // Actualización de UI
    // =========================================================================

    private void updateHistoryDisplay(List<Session> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvResultCount.setText("0 sesiones");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvResultCount.setText(sessions.size() + " sesiones");
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
