package com.example.focuspilar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import com.example.focuspilar.view.PreferencesActivity;
import com.example.focuspilar.view.SessionHistoryActivity;
import android.content.res.ColorStateList;

import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.view.View;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * @Author pilar_sb_cc@ciencias.unam.mx
 * */

public class MainActivity extends AppCompatActivity {


    // Estados del temporizador y sesion.
    enum TimerState {IDLE, RUNNING, PAUSED}
    enum SessionMode {FOCUS, BREAK, REST}

    // Constantes de tiempo en milisegundos.
    private static final long FOCUS_DURATION_MS = 25 * 60 * 1000L;
    private static final long BREAK_DURATION_MS = 5 * 60 * 1000L;
    private static final long REST_DURATION_MS = 15 * 60 * 1000L;
    private static final int SESSIONS_BEFORE_REST = 4;

    // Elementos de la IU.

    private ImageButton btnStats, btnSettings, btnReset, btnSkip;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;
    // TODO: delcarar el texto que indica el estado de la sesion.
    private TextView tvTimerDisplay;
    // TODO: delcarar el texto que indica cuantas sesiones han sido completadas.
    private MaterialButton btnStartStop;
    // TODO: delcarar los botones de reinicio y salto de una sesion.
    private LinearLayout sessionDotsContainer;
    // TODO: declarar el texto para la frase motivadora.


    private TextView        tvSessionStatus;   // estado de la sesión
    private TextView        tvSessionCount;    // contador de sesiones
    private TextView        tvMotivation;      // frase motivacional


    // Elementos para el funcionamiento del temporizador.
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.IDLE;
    private SessionMode currentMode = SessionMode.FOCUS;
    private long timeLeftMillis = FOCUS_DURATION_MS;
    private int focusSessionsCompleted = 0;

    /**
     * PUNTO EXTRA
     * Para la persistencia
     * */
    private static final String KEY_TIME_LEFT          = "timeLeftMillis";
    private static final String KEY_TIMER_STATE        = "timerState";
    private static final String KEY_SESSION_MODE       = "sessionMode";
    private static final String KEY_FOCUS_COMPLETED    = "focusSessionsCompleted";

    private static final String[] MOTIVATIONAL_PHRASES = {
            "¡Buen trabajo!",
            "Un respiro",
            "SIGUE ASI !!!",
            "¡sigues avanzando!",
            "Descansa !!",
            "Lo estás haciendo genial :)",
            "Pausa"
    };


    /**
     *
     * Inicializa la actividad junto con todos sus componentes
     * @param savedInstanceState Estado guardado de la aplicación
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflamos nuestra vista.
        setContentView(R.layout.activity_main);
        // Inicializamos los elementos de la IU.
        bindViews();
        // Asignamos los escuchas.
        setupClickListeners();
        // Actualizamos la IU.

        setupChipNavigation();   // Extra: Navegación por Chips

        // Extra: Persistencia — restaurar estado si existe
        if (savedInstanceState != null) {
            timeLeftMillis         = savedInstanceState.getLong(KEY_TIME_LEFT, FOCUS_DURATION_MS);
            timerState             = TimerState.values()[savedInstanceState.getInt(KEY_TIMER_STATE, 0)];
            currentMode            = SessionMode.values()[savedInstanceState.getInt(KEY_SESSION_MODE, 0)];
            focusSessionsCompleted = savedInstanceState.getInt(KEY_FOCUS_COMPLETED, 0);

            // Si estaba corriendo antes de rotar/minimizar, lo retomamos en PAUSED
            if (timerState == TimerState.RUNNING) {
                timerState = TimerState.PAUSED;
                btnStartStop.setText("Reanudar");
            }
        }

        updateTimerDisplay(timeLeftMillis);
        updateSessionDots();
        updateSessionStatusText();
        updateSessionCountText();
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * Extra: Persistencia — guarda el estado antes de que la Activity sea destruida
     * (rotación de pantalla, minimizar la app, etc.)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Si el timer está corriendo, pausamos para guardar el tiempo exacto
        if (timerState == TimerState.RUNNING) {
            cancelTimer();
            timerState = TimerState.RUNNING; // lo marcamos como RUNNING para restaurarlo
        }
        outState.putLong(KEY_TIME_LEFT,       timeLeftMillis);
        outState.putInt(KEY_TIMER_STATE,      timerState.ordinal());
        outState.putInt(KEY_SESSION_MODE,     currentMode.ordinal());
        outState.putInt(KEY_FOCUS_COMPLETED,  focusSessionsCompleted);
    }
    /**
     * Destruye la actividad.
     * Se cancela el temporizador para evitar fugas de memoria y liberar recursos
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }


    /**
     * Vincula las variables con los identificadores definidos en la interfaz
     */
    private void bindViews() {
        btnStats              = findViewById(R.id.btnStats);
        btnSettings           = findViewById(R.id.btnSettings);
        chipGroupMode         = findViewById(R.id.chipGroupMode);
        chipFocus             = findViewById(R.id.chipFocus);
        chipBreak             = findViewById(R.id.chipBreak);
        chipRest              = findViewById(R.id.chipRest);
        tvTimerDisplay        = findViewById(R.id.tvTimerDisplay);
        tvSessionStatus       = findViewById(R.id.tvSessionStatus);
        tvSessionCount        = findViewById(R.id.tvSessionCount);
        tvMotivation          = findViewById(R.id.tvMotivation);
        btnStartStop          = findViewById(R.id.btnStartStop);
        sessionDotsContainer  = findViewById(R.id.sessionDotsContainer);
        btnReset              = findViewById(R.id.btnReset);
        btnSkip               = findViewById(R.id.btnSkip);
    }

    /**
     * Asigna los clics a los botones definidos en la interfaz
     */
    private void setupClickListeners() {
        // Asignamos un escucha al boton que controla nuestro temporizador.
        btnStartStop.setOnClickListener(v -> {
            // Se ha seleccionado la opcion para comenzar/pausar el temporizador.
            // Llamamos a los metodos correspondientes segun el estado del temporizador.
            if (timerState == TimerState.RUNNING) pauseTimer();
            else startTimer();
        });

        // Botones de reiniciar y saltar
        btnReset.setOnClickListener(v -> resetTimer());
        btnSkip.setOnClickListener(v -> skipToNextSession());

        btnStats.setOnClickListener(v ->
                startActivity(new Intent(this, SessionHistoryActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, PreferencesActivity.class)));
    }

    /**
     * Extra: Navegación por Chips
     * Al tocar un chip se le avisa al usuario y, si confirma, cambia de modo inmediatamente.
     */
    private void setupChipNavigation() {
        chipFocus.setOnClickListener(v -> requestModeChange(SessionMode.FOCUS));
        chipBreak.setOnClickListener(v -> requestModeChange(SessionMode.BREAK));
        chipRest.setOnClickListener(v  -> requestModeChange(SessionMode.REST));
    }
    private void requestModeChange(SessionMode targetMode) {
        if (targetMode == currentMode) return; // ya está en ese modo

        new AlertDialog.Builder(this)
                .setTitle("Cambiar modo")
                .setMessage("¿Deseas cambiar al modo " + modeLabel(targetMode) + "? El progreso actual se perderá.")
                .setPositiveButton("Sí, cambiar", (dialog, which) -> {
                    cancelTimer();
                    timerState   = TimerState.IDLE;
                    currentMode  = targetMode;
                    resetModeTime();
                    btnStartStop.setText("Comenzar");
                    updateSessionStatusText();
                    hideMotivation();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Devolver la selección visual al chip actual
                    selectChipForMode(currentMode);
                })
                .show();
    }

    /**
     * Inicia el temporizador con el tiempo restante actual.
     * Se actualiza la interfaz y se cambia de estado
     */
    private void startTimer() {
        // Actualizamos el estado del temporizador.
        timerState = TimerState.RUNNING;
        // Asignamos una texto mas adecuado al boton que controla nuestro temporizador.
        btnStartStop.setText("Pausar");


        // Creamos e inicializamos un contador.
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {

            /**
             * Actualiza la vista del temporizador
             * @param millisUntilFinished Milisegundos restantes
             */
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);
            }

            /**
             * Notifica que el tiempo se ha terminado
             */
            @Override
            public void onFinish() {
                onSessionFinished();
            }
        }.start();
    }

    /**
     * Se detiene el temporizador guardando el tiempo restante.
     * Se cambia de estado y se actualiza el boton
     */
    private void pauseTimer() {
        // Detenemos nuestro contador.
        if (countDownTimer != null) countDownTimer.cancel();
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.PAUSED;
        // Actualizamos el texto del boton que controla el temporizador.
        btnStartStop.setText("Reanudar");
    }

    /**
     * Gestiona el cambio en el modo de la sesión.
     * Se generan los puntos dependiendo del número de sesiones.
     * Se notifica el usuario el fin de una sesión
     */
    private void onSessionFinished() {
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.IDLE;

        // Actualizamos el estado de la sesion por su sucesora.
        if (currentMode == SessionMode.FOCUS) {
            focusSessionsCompleted++;
            if (focusSessionsCompleted >= SESSIONS_BEFORE_REST) {
                focusSessionsCompleted = 0;
                currentMode = SessionMode.REST;
            } else {
                currentMode = SessionMode.BREAK;
            }

            // Extra: Frases motivacionales al iniciar un descanso
            showMotivationalPhrase();
        } else {
            currentMode = SessionMode.FOCUS;
        }

        // Actualizamos los puntos al cambiar de estado
        updateSessionDots();

        // Mostramos un mensaje sencillo al finalizar cada sesion.
        Toast.makeText(this, "¡Sesión terminada!", Toast.LENGTH_SHORT).show();

        // solicitamos al servicio del sistema que genere una vibracion simple
        // para notificar al usuario que la sesion a terminado.

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            // Vibra por 500 milisegundos
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        // Actualizamos el temporizador y el texto del boton que lo controla.
        resetModeTime();
        btnStartStop.setText("Comenzar");
    }

    private void cancelTimer(){
        //Si el temporizador esta activo:
        if(countDownTimer != null){
            //detenmos el tiempo
            countDownTimer.cancel();
            //anulamos el temporizador
            countDownTimer = null;
        }
    }

    private void resetTimer() {

        cancelTimer();
        timerState =TimerState.IDLE;

        // Reiniciar el texto del botón
        resetModeTime();
        btnStartStop.setText("COMENZAR");
    }

    /**
     * Se fuerza la finalización de una sesión y salta a la siguiente
     */
    private void skipToNextSession() {

        cancelTimer();
        onSessionFinished();

    }


    private void resetModeTime() {
        switch (currentMode) {
            case FOCUS: timeLeftMillis = FOCUS_DURATION_MS; break;
            case BREAK: timeLeftMillis = BREAK_DURATION_MS; break;
            case REST:  timeLeftMillis = REST_DURATION_MS;  break;
        }
        updateTimerDisplay(timeLeftMillis);
    }

    // =========================================================================
    // Actualización de la UI
    // =========================================================================

    private void updateTimerDisplay(long millis) {
        selectChipForMode(currentMode);
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        tvTimerDisplay.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateSessionStatusText() {
        if (tvSessionStatus == null) return;
        tvSessionStatus.setText(modeLabel(currentMode));
    }

    private void updateSessionCountText() {
        if (tvSessionCount == null) return;
        tvSessionCount.setText("Sesiones completadas: " + focusSessionsCompleted + " / " + SESSIONS_BEFORE_REST);
    }

    /** Extra: Frases motivacionales */
    private void showMotivationalPhrase() {
        if (tvMotivation == null) return;
        int idx = (int) (Math.random() * MOTIVATIONAL_PHRASES.length);
        tvMotivation.setText(MOTIVATIONAL_PHRASES[idx]);
        tvMotivation.setVisibility(View.VISIBLE);
    }

    private void hideMotivation() {
        if (tvMotivation != null) tvMotivation.setVisibility(View.GONE);
    }

    // =========================================================================
    // Puntos de sesión
    // =========================================================================

    private void updateSessionDots() {
        sessionDotsContainer.removeAllViews();
        for (int i = 0; i < focusSessionsCompleted; i++) addDot();
    }

    private void addDot() {
        View dot = new View(this);
        int dotSize = (int) (10 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        dot.setLayoutParams(params);
        dot.setBackground(getDrawable(R.drawable.dot_session_completed));
        sessionDotsContainer.addView(dot);
    }

    // =========================================================================
    // Chips
    // =========================================================================

    private void selectChipForMode(SessionMode mode) {
        int chipId;
        Chip activeChip;
        switch (mode) {
            case BREAK:
                chipId     = R.id.chipBreak;
                activeChip = chipBreak;
                break;
            case REST:
                chipId     = R.id.chipRest;
                activeChip = chipRest;
                break;
            default:
                chipId     = R.id.chipFocus;
                activeChip = chipFocus;
                break;
        }
        chipGroupMode.check(chipId);
        highlightChip(activeChip);
    }

    private void highlightChip(Chip activeChip) {
        float density  = getResources().getDisplayMetrics().density;
        Chip[] allChips = { chipFocus, chipBreak, chipRest };
        for (Chip chip : allChips) chip.setChipStrokeWidth(0);

        activeChip.setChipStrokeWidth(2 * density);
        int colorAccent = ContextCompat.getColor(this, R.color.color_border_accent);
        activeChip.setChipStrokeColor(ColorStateList.valueOf(colorAccent));
    }

    // =========================================================================
    // Vibración
    // =========================================================================

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    // =========================================================================
    // Utilidades
    // =========================================================================

    private String modeLabel(SessionMode mode) {
        switch (mode) {
            case BREAK: return "Descanso Corto";
            case REST:  return "Descanso Largo";
            default:    return "Enfoque";
        }
    }
}