package com.example.myapplicationtask;

import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SizeF;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.collection.ArrayMap;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    public static final String TOAST_ACTION = "TOAST_ACTION";
    private static final String ADD_TASK_ACTION = "ADD_TASK_ACTION";
    private static final String CATEGORY_CLICK_ACTION = "CATEGORY_CLICK_ACTION";
    private static final String CATEGORY_CLICK_ACTION_LEFT = "CATEGORY_CLICK_ACTION_LEFT";

    private static TaskRepositoryDatabaseImpl repository;
    private static ExecutorService executor;
    private static int currentTaskListId = 1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (repository == null) {
            Application application = (Application) context.getApplicationContext();
            repository = new TaskRepositoryDatabaseImpl(application);
            executor = Executors.newSingleThreadExecutor();
        }

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        RemoteViews smallView = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
        RemoteViews mediumView = new RemoteViews(context.getPackageName(), R.layout.widget_layout_medium);
        RemoteViews largeView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Map<SizeF, RemoteViews> viewMapping = new ArrayMap<>();
        viewMapping.put(new SizeF(40f, 40f), smallView);
        viewMapping.put(new SizeF(110f, 110f), mediumView);
        viewMapping.put(new SizeF(250f, 250f), largeView);

        RemoteViews remoteViews = null;
        for (SizeF size : viewMapping.keySet()) {
            if (minWidth <= size.getWidth() && minHeight <= size.getHeight()) {
                remoteViews = viewMapping.get(size);
                break;
            }
        }

        if (remoteViews == null) {
            remoteViews = mediumView; // Default to medium view if no match
        }

        Intent serviceIntent = new Intent(context, ExampleWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.widget_task_list, serviceIntent);

        Intent toastIntent = new Intent(context, ExampleAppWidgetProvider.class);
        toastIntent.setAction(ExampleAppWidgetProvider.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        remoteViews.setPendingIntentTemplate(R.id.widget_task_list, toastPendingIntent);

        Intent addTaskIntent = new Intent(context, ExampleAppWidgetProvider.class);
        addTaskIntent.setAction(ExampleAppWidgetProvider.ADD_TASK_ACTION);
        PendingIntent addTaskPendingIntent = PendingIntent.getBroadcast(context, 0, addTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.add_task_button, addTaskPendingIntent);

        Intent categoryIntent = new Intent(context, ExampleAppWidgetProvider.class);
        categoryIntent.setAction(ExampleAppWidgetProvider.CATEGORY_CLICK_ACTION);
        PendingIntent categoryPendingIntent = PendingIntent.getBroadcast(context, 0, categoryIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.next_category, categoryPendingIntent);

        Intent categoryIntentLeft = new Intent(context, ExampleAppWidgetProvider.class);
        categoryIntentLeft.setAction(ExampleAppWidgetProvider.CATEGORY_CLICK_ACTION_LEFT);
        PendingIntent categoryPendingIntentLeft = PendingIntent.getBroadcast(context, 0, categoryIntentLeft, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.last_category, categoryPendingIntentLeft);

        remoteViews.setEmptyView(R.id.widget_task_list, R.id.widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (repository == null) {
            Application application = (Application) context.getApplicationContext();
            repository = new TaskRepositoryDatabaseImpl(application);
        }
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }

        if (ADD_TASK_ACTION.equals(intent.getAction())) {
            Log.d("ExampleAppWidgetProvider", "Add Task Button Clicked");
            showAddTaskDialog(context);
        }

        if (CATEGORY_CLICK_ACTION.equals(intent.getAction())) {
            Log.d("ExampleAppWidgetProvider", "Category Name Clicked");
            currentTaskListId++;
            Log.d("ExampleAppWidgetProvider", "Updated currentTaskListId: " + currentTaskListId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_task_list);
        }

        if (CATEGORY_CLICK_ACTION_LEFT.equals(intent.getAction())) {
            Log.d("ExampleAppWidgetProvider", "Category Name Clicked");
            currentTaskListId--;
            Log.d("ExampleAppWidgetProvider", "Updated currentTaskListId: " + currentTaskListId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_task_list);
        }

        if (TOAST_ACTION.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);

            Log.d("ExampleAppWidgetProvider", "Action: " + intent.getAction());
            Log.d("ExampleAppWidgetProvider", "Widget ID: " + appWidgetId);
            Log.d("ExampleAppWidgetProvider", "Received task ID: " + taskId);
            Log.d("ExampleAppWidgetProvider", "Intent extras: " + intent.getExtras());
            if (taskId != -1 && repository != null) {
                executor.execute(() -> {
                    Task task = repository.getTaskById(taskId);
                    if (task != null) {
                        task.setDone(!task.isDone());
                        repository.update(task);
                    }
                });
            } else {
                Log.e("ExampleAppWidgetProvider", "Task ID or repository is null.");
            }
        }
    }

    private void showAddTaskDialog(Context context) {
        Log.d("ExampleAppWidgetProvider", "Opening Add Task Dialog");
        Intent dialogIntent = new Intent(context, TaskDetail.class);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("taskListId", currentTaskListId);
        context.startActivity(dialogIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context, "Widget eliminato", Toast.LENGTH_SHORT).show();
    }

    public static int getCurrentTaskListId() {
        return currentTaskListId;
    }
}
