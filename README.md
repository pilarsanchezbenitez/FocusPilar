## Programación de Dispositivos Móviles.
## Practica 2: Lógica y UI del Pomodoro Timer
## María del Pilar Sánchez Benítez
 

### Con el objetivo de hacer entendible el ciclo de vida y poner en practica lo aprendido para el diseño de la interfaz de una aplicación. Se desarrollo una aplicaci+ón basada en la tecnica pomodoro para productividad.


#### ¿Cuál fue el mayor reto al gestionar el CountDownTimer y cómo evitaste que se crearan múltiples instancias al presionar el botón repetidamente?

Cuando se presionara el botón iniciar no se tuvieran muchas veces  y a la par el conutDownTimer; esto se soluciona cancelado el temporizador  antes de crear uno nuevo.

#### ¿Por qué es preferible usar un LinearLayout con addView para los puntos de progreso en lugar de declarar 4 ImageViews estáticos en el XML?
Por que con el lineraLayout habría que mostrar u ocultar cada uno manualmente.

#### Si quisiéramos añadir una función para que el usuario personalice sus propios tiempos de enfoque, ¿qué parte de tu lógica actual tendría que cambiar y cómo lo abordarías?

Para poer realizar este cambio se debrán cambiar las constantes por variables, en la parte de la interfaz agregar un apartado para realizar la captura de los tiempos del usuario y esta información tomarla en el oncreate().

#### ¿Cómo harían para que el tiempo del temporizador se mantenga si el usuario minimiza la app?

Para que el temporizador siga corriendo aunque la app esté minimizada durante mucho tiempo
se usa un `ForegroundService`. Este servicio muestra una notificación persistente ("Timer
corriendo: 18:32") y contiene el `CountDownTimer`. La Activity se conecta al servicio
mediante `bindService()` para leer y mostrar el tiempo en pantalla.