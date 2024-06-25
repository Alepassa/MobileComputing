package com.example.myapplicationtask;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskRepositoryDatabaseImpl implements TaskRepository {
    private TaskDao taskDao;
    private TaskListDao taskListDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<TaskList>> allTaskLists;
    private ExecutorService executorService;
    private Context context;

    public TaskRepositoryDatabaseImpl(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        taskListDao = db.taskListDao();
        allTasks = taskDao.getAllTasks();
        allTaskLists = taskListDao.getAllTaskLists();
        executorService = Executors.newFixedThreadPool(2);
        this.context = application.getApplicationContext();
    }

    @Override
    public void insert(Task task) {
        executorService.execute(() -> {
            TaskList taskList = taskListDao.getTaskListById(task.getTaskListId());
            if (taskList != null) {
                Log.d("TaskRepository", "Inserting task: " + task.getShortName() + " into TaskList ID: " + task.getTaskListId());
                taskDao.insert(task);
                notifyWidgetDataChanged();

            } else {
                Log.e("TaskRepository", "TaskList with id " + task.getTaskListId() + " does not exist. Cannot insert task.");
            }
        });
    }

    @Override
    public void update(Task task) {
        executorService.execute(() -> {
            TaskList taskList = taskListDao.getTaskListById(task.getTaskListId());
            if (taskList != null) {
                Log.d("TaskRepository", "Updating task: " + task.getShortName() + " in TaskList ID: " + task.getTaskListId());
                taskDao.update(task);
                notifyWidgetDataChanged();
            } else {
                Log.e("TaskRepository", "TaskList with id " + task.getTaskListId() + " does not exist. Cannot update task.");
            }
        });
    }
    @Override
    public void delete(Task task) {
        executorService.execute(() -> {
            taskDao.delete(task);
            notifyWidgetDataChanged();
        });
    }

    @Override
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    @Override
    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    @Override
    public void deleteCompletedTasks() {
        executorService.execute(taskDao::deleteCompletedTasks);
    }

    @Override
    public LiveData<List<TaskList>> getTaskLists() {
        return allTaskLists;
    }

    @Override
    public LiveData<List<Task>> loadTasks(String taskListName) {
        TaskList taskList = taskListDao.getTaskListByName(taskListName);
        if (taskList != null) {
            return taskDao.getTasksByTaskListId(taskList.getId());
        }
        return new MutableLiveData<>(new ArrayList<>()); // Return empty LiveData if taskList is null
    }

    @Override
    public void insertTaskList(TaskList taskList) {
        executorService.execute(() -> {
            taskListDao.insert(taskList);
            Log.d("TaskRepository", "Inserted TaskList: " + taskList.getName());
        });
    }

    @Override
    public LiveData<List<TaskList>> getAllTaskLists() {
        return allTaskLists;
    }

    @Override
    public LiveData<List<Task>> getTasksByTaskListId(int taskListId) {
        return taskDao.getTasksByTaskListId(taskListId);
    }

    @Override
    public void deleteCompletedTasksByTaskListId(int taskListId) {
        executorService.execute(() -> taskDao.deleteCompletedTasksByTaskListId(taskListId));
    }
    @Override
    public void deleteTaskListById(int taskListId) {
        executorService.execute(() -> taskListDao.deleteTaskListById(taskListId));
    }

    @Override
    public void deleteTaskById(int taskId) {
        Task taskToDelete = taskDao.getTaskById(taskId);
        if (taskToDelete != null) {
            taskDao.delete(taskToDelete);
        }
    }

    public String getTaskListNameByIdSync(int taskListId) {
        return taskListDao.getTaskListNameByIdSync(taskListId);
    }
    @Override
    public List<Task> getTasksByTaskListIdSync(int taskListId) {
        return taskDao.getTasksByTaskListIdSync(taskListId);
    }

    private void notifyWidgetDataChanged() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_task_list);
    }
}


/*Aggiornamento dei dati della raccolta: chiama AppWidgetManager.notifyAppWidgetViewDataChanged
 per invalidare i dati di una vista raccolta nel widget. Questo attiva RemoteViewsFactory.onDataSetChanged.
  Nel frattempo, i dati precedenti vengono visualizzati nel widget.
  Con questo metodo puoi eseguire in sicurezza attività costose in modo sincrono. */

//non è una buona soluzione in quanto possiamo solo aggiungere / cancellare le task ma non possiamo modificarle
//tocca quindi fare un aggiornamento completo.