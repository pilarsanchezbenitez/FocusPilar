package com.example.focuspilar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Capa de datos – Gestiona la creación y migración de la base de datos SQLite.
 * Actúa como único punto de acceso a la BD dentro del paquete .data.
 *
 * Tabla: sessions
 *   _id        INTEGER  PRIMARY KEY AUTOINCREMENT
 *   type       TEXT     Tipo de sesión ("Enfoque", "Descanso", "Descanso Largo")
 *   date       TEXT     Fecha formateada  (EEE, dd MMM yyyy)
 *   start_time TEXT     Hora de inicio    (HH:mm)
 *   duration   INTEGER  Duración en minutos (25, 5, 15)
 *   completed  INTEGER  1 = completada, 0 = interrumpida
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class SessionDbHelper extends SQLiteOpenHelper {

    // ─── Información de la base de datos ──────────────────────────────────────
    public static final String DATABASE_NAME    = "focus_pilar.db";
    public static final int    DATABASE_VERSION = 1;

    // ─── Nombre de la tabla ───────────────────────────────────────────────────
    public static final String TABLE_SESSIONS = "sessions";

    // ─── Nombres de columnas ──────────────────────────────────────────────────
    public static final String COL_ID         = "_id";
    public static final String COL_TYPE       = "type";
    public static final String COL_DATE       = "date";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_DURATION   = "duration";
    public static final String COL_COMPLETED  = "completed";

    // ─── Sentencias SQL ───────────────────────────────────────────────────────
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_SESSIONS + " ("
            + COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TYPE       + " TEXT    NOT NULL, "
            + COL_DATE       + " TEXT    NOT NULL, "
            + COL_START_TIME + " TEXT    NOT NULL, "
            + COL_DURATION   + " INTEGER NOT NULL, "
            + COL_COMPLETED  + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_SESSIONS;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ─── Ciclo de vida de la BD ───────────────────────────────────────────────

    /**
     * Se ejecuta la primera vez que se abre la BD.
     * Crea la tabla sessions.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Se ejecuta cuando DATABASE_VERSION aumenta.
     * Descarta la tabla existente y la vuelve a crear.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
