package com.example.focuspilar.model;

/**
 * Modelo de datos que representa una sesión registrada.
 * Contiene tipo, fecha, hora de inicio, duración y si fue completada.
 *
 * @Author pilar_sb_cc@ciencias.unam.mx
 */
public class Session {

    private String type;        // "Enfoque" o "Descanso"
    private String date;        // Formato: EEE, dd MMM yyyy
    private String startTime;   // Formato: hh:mm
    private int duration;       // 25, 5 o 15 minutos
    private boolean completed;  // true si la sesión terminó correctamente

    public Session(String type, String date, String startTime, int duration, boolean completed) {
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.completed = completed;
    }

    // --- Getters y Setters ---

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
