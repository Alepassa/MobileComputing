package com.example.myapplicationtask;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    public static final String TOAST_ACTION = "TOAST_ACTION";

    private static TaskRepositoryDatabaseImpl repository;
    private static ExecutorService executor;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (repository == null) {
            Application application = (Application) context.getApplicationContext();
            repository = new TaskRepositoryDatabaseImpl(application);
            executor = Executors.newSingleThreadExecutor();

        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Intent per il servizio che fornisce i dati al widget
            Intent serviceIntent = new Intent(context, ExampleWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_task_list, serviceIntent);

            // Intent per gestire l'azione di clic sugli elementi del widget
            Intent toastIntent = new Intent(context, ExampleAppWidgetProvider.class);
            toastIntent.setAction(ExampleAppWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            views.setPendingIntentTemplate(R.id.widget_task_list, toastPendingIntent);

            // imposta la vista vuota nel caso in cui non ci siano dati
            views.setEmptyView(R.id.widget_task_list, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
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
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context, "Widget eliminato", Toast.LENGTH_SHORT).show();
    }
}