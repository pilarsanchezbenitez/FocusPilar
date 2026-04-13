package com.example.focuspilar.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.focuspilar.data.SessionDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Modelo – único punto de acceso a los datos de sesión.
 * Usa SQLiteOpenHelper para persistir el historial de forma local.
 *
 * Escritura  → siempre en un Background Thread (new Thread).
 * Lectura    → métodos síncronos; el llamador decide el hilo.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class SessionManager {

    /** Formato de fecha usado al guardar y comparar sesiones. */
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy";

    private final SessionDbHelper dbHelper;

    /**
     * @param context Contexto necesario para abrir/crear la BD SQLite.
     */
    public SessionManager(Context context) {
        dbHelper = new SessionDbHelper(context);
    }

    // =========================================================================
    // Escritura  (Background Thread)
    // =========================================================================

    /**
     * Inserta una sesión en la base de datos en un hilo secundario
     * para no bloquear la interfaz de usuario.
     *
     * @param session Sesión a persistir
     */
    public void addSession(Session session) {
        new Thread(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(SessionDbHelper.COL_TYPE,       session.getType());
                values.put(SessionDbHelper.COL_DATE,       session.getDate());
                values.put(SessionDbHelper.COL_START_TIME, session.getStartTime());
                values.put(SessionDbHelper.COL_DURATION,   session.getDuration());
                values.put(SessionDbHelper.COL_COMPLETED,  session.isCompleted() ? 1 : 0);

                db.insert(SessionDbHelper.TABLE_SESSIONS, null, values);

            } catch (SQLiteException e) {
                // Fallo silencioso: la sesión no se guarda pero la app sigue funcionando
                e.printStackTrace();
            }
        }).start();
    }

    // =========================================================================
    // Lectura  (el llamador debe ejecutar en background si lo requiere)
    // =========================================================================

    /**
     * Devuelve el historial completo ordenado del más reciente al más antiguo.
     */
    public List<Session> getHistory() {
        List<Session> sessions = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    SessionDbHelper.TABLE_SESSIONS,
                    null, null, null, null, null,
                    SessionDbHelper.COL_ID + " DESC"
            );
            while (cursor.moveToNext()) {
                sessions.add(cursorToSession(cursor));
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Devuelve las sesiones registradas hoy.
     */
    public List<Session> getSessionsToday() {
        List<Session> sessions = new ArrayList<>();
        String today = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    SessionDbHelper.TABLE_SESSIONS,
                    null,
                    SessionDbHelper.COL_DATE + " = ?",
                    new String[]{today},
                    null, null,
                    SessionDbHelper.COL_ID + " DESC"
            );
            while (cursor.moveToNext()) {
                sessions.add(cursorToSession(cursor));
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Devuelve las sesiones de la semana actual.
     * Obtiene todos los registros y filtra en Java por rango de fechas.
     */
    public List<Session> getSessionsThisWeek() {
        // Calcular el primer día de la semana actual (lunes o domingo según dispositivo)
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE,      0);
        weekStart.set(Calendar.SECOND,      0);
        weekStart.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf     = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        List<Session>    result  = new ArrayList<>();

        for (Session s : getHistory()) {
            try {
                Date sessionDate = sdf.parse(s.getDate().trim());
                if (sessionDate != null && !sessionDate.before(weekStart.getTime())) {
                    result.add(s);
                }
            } catch (ParseException e) {
                // Ignorar sesiones con fecha con formato inválido
            }
        }
        return result;
    }

    // =========================================================================
    // Utilidades privadas
    // =========================================================================

    /**
     * Convierte la fila actual de un Cursor en un objeto Session.
     */
    private Session cursorToSession(Cursor cursor) {
        String  type      = cursor.getString(cursor.getColumnIndexOrThrow(SessionDbHelper.COL_TYPE));
        String  date      = cursor.getString(cursor.getColumnIndexOrThrow(SessionDbHelper.COL_DATE));
        String  startTime = cursor.getString(cursor.getColumnIndexOrThrow(SessionDbHelper.COL_START_TIME));
        int     duration  = cursor.getInt   (cursor.getColumnIndexOrThrow(SessionDbHelper.COL_DURATION));
        boolean completed = cursor.getInt   (cursor.getColumnIndexOrThrow(SessionDbHelper.COL_COMPLETED)) == 1;

        return new Session(type, date, startTime, duration, completed);
    }
}
