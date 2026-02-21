package com.example.focuspilar;

import java.util.ArrayList;
import java.util.List;
/**
 * @author  <a href=pilar_sb_cc@ciencias.unam.mx> Maria del Pilar Sánchez Benítez </a>
 *
 */

/*
* Definimos un objeto Task; con los atributos de:
* idTask : Identificador de una tarea
* task: Nombre o titulo de una tarea
* descripcion: Descripcion de la tarea
* estatus: booleano que representa  el estado de una tarea:
*           1 si la tarea esta inhabilitada
*           0 si la tarea esta habilitada
* */
class Task {
    private final int idTask;
    private String task;
    private String descripcion;
    private boolean estatus;

    /*Constructor*/
    public Task(int idTask, String task, String descripcion, boolean estatus) {
        this.idTask = idTask;
        this.task = task;
        this.descripcion = descripcion;
        this.estatus = estatus;
    }

    /* Getter & Setters */

    public int getIdTask() {
        return idTask;
    }
    public String getTask() {
        return task;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public boolean getEstatus(){
     return  estatus;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString(){
        return "Tarea: "+task+"|Descripción: "+ descripcion;
    }
}

public class TaskManager {

    private List<Task> listTask;
    private int countTask = 1;

    public TaskManager() {
        this.listTask = new ArrayList<Task>();
    }

    /*Create*/

    public void addTask (String task, String descripcion){
        listTask.add(new Task(countTask++,task,descripcion,false));
    }
    /*Read*/
    public void getTasks(){
        List<Task> taskActive = new ArrayList<>();
        for (Task t : listTask) {
            if (!t.getEstatus()) taskActive.add(t);
        }
        if (taskActive.isEmpty()) {
            System.out.println("No hay tareas activas");
        } else {
            System.out.println(taskActive);
        }
    }
    /*Update*/
     public void updateTask(int idTask, String task, String taskDes){
         for (Task t : listTask){
             if(t.getIdTask() == idTask){
                 t.setTask(task);
                 t.setDescripcion(taskDes);
                 System.out.println("Tarea actulizada");
                 return;
             }
         }
         System.out.println("No se encontró la tarea en la lista");
     }
     /*Delete*/
    public void deleteLogicTask(int idTask){
        for (Task t : listTask) {
            if (!t.getEstatus()){
                t.setEstatus(true);
                System.out.println("Tarea Inhabilitada");
                return;
            };
        }
        System.out.println("No se encontró la tarea en la lista");
    }
    public void deleteTask(int idTask){
        listTask.removeIf(task -> task.getIdTask() == idTask);
    }

}
