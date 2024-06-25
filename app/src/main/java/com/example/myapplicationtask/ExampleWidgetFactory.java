package com.example.myapplicationtask;


import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExampleWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "ExampleWidgetFactory";
    private Context context;
    private List<Task> taskList = new ArrayList<>();
    private TaskRepository repository;
    private String listName;
    private Handler mainHandler;
    private int appWidgetId;

    public ExampleWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Application application = (Application) context.getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());
        repository = new TaskRepositoryDatabaseImpl(application);
    }

    @Override
    public void onCreate() {
        // Inizializza i dati solo se la lista è vuota
        if (taskList.isEmpty()) {
            fetchData();
        }
    }

    @Override
    public void onDataSetChanged() {
        fetchData();
    }

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        Task task = taskList.get(position);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);
        views.setTextViewText(R.id.task_name, task.getShortName());
        views.setCompoundButtonChecked(R.id.task_checkbox, task.isDone());


        Bundle extras = new Bundle();
        extras.putInt(ExampleAppWidgetProvider.EXTRA_ITEM, position);
        extras.putInt(ExampleAppWidgetProvider.EXTRA_TASK_ID, task.getId());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.task_checkbox, fillInIntent);

        Log.d("ExampleWidgetFactory", "Task ID set in fillInIntent: " + task.getId());
        Log.d("ExampleWidgetFactory", "Extras: " + fillInIntent.getExtras());


        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void fetchData() {
        new Thread(() -> {
            try {
                int taskListId = ExampleAppWidgetProvider.getCurrentTaskListId();

                List<Task> tasks = repository.getTasksByTaskListIdSync(taskListId);
                listName = repository.getTaskListNameByIdSync(taskListId);
                Log.d(TAG, "List Name " + listName);

                if (!tasks.equals(taskList)) {
                    taskList.clear();
                    taskList.addAll(tasks);

                    mainHandler.post(() -> {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
                        for (int appWidgetId : appWidgetIds) {
                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                            views.setTextViewText(R.id.category_name, listName);
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_task_list);

                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "fetchData: Error fetching tasks", e);
            }
        }).start();
    }

}