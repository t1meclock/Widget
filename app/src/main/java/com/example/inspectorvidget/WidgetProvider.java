package com.example.inspectorvidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WidgetProvider extends AppWidgetProvider {

    static String futureJokeString = "";
    RemoteViews views;
    String action = "btnClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName componentName;
        views = new RemoteViews(context.getPackageName(), R.layout.widget);
        componentName = new ComponentName(context, WidgetProvider.class);
        new JokeLoader().execute();
        views.setOnClickPendingIntent(R.id.btnClick, getPendingSelfIntent(context, action));
        views.setTextViewText(R.id.txtJoke, futureJokeString);
        appWidgetManager.updateAppWidget(componentName, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (action.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName;
            views = new RemoteViews(context.getPackageName(), R.layout.widget);
            componentName = new ComponentName(context, WidgetProvider.class);
            new JokeLoader().execute();
            views.setTextViewText(R.id.txtJoke, futureJokeString);
            appWidgetManager.updateAppWidget(componentName, views);
        }
    }

    private class JokeLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            String jsonString = getJson("https://api.chucknorris.io/jokes/random");

            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                WidgetProvider.futureJokeString = jsonObject.getString("value");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        private String getJson(String link){
            String data = "";
            try{
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    data = r.readLine();
                    urlConnection.disconnect();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return data;
        }
    }
}