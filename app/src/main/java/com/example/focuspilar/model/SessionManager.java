package com.example.focuspilar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo – único punto de acceso a los datos de sesión.
 * Actualmente gestiona el historial en memoria (ArrayList).
 *
 * TODOs pendientes para completar el proyecto:
 *   - Recibir Context en el constructor para instanciar SessionDbHelper.
 *   - Reemplazar el ArrayList por operaciones SQLite (insert/query).
 *   - Ejecutar las operaciones de BD en un Background Thread.
 *   - Agregar métodos getSessionsToday() y getSessionsThisWeek().
 *   - Envolver las operaciones en bloques try-catch (SQLiteException).
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class SessionManager {

    private final ArrayList<Session> sessionHistory;

    public SessionManager() {
        sessionHistory = new ArrayList<>();
    }

    /**
     * Inserta una sesión al inicio de la lista (más reciente primero).
     *
     * @param session Sesión a guardar
     */
    public void addSession(Session session) {
        sessionHistory.add(0, session);
    }

    /**
     * Devuelve una copia del historial completo de sesiones.
     *
     * @return Lista de sesiones registradas
     */
    public List<Session> getHistory() {
        return new ArrayList<>(sessionHistory);
    }
}
