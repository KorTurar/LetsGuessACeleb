package com.example.guessaceleb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.CallLog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;





public class WorkWithDbClass {
    //получаем переменную db,
    //узнаём есть ди таблицы,
    //создаём нужные таблицы,
    //сохраняем контакты если нет интерента для немедленной отправки,
    //сохраняем инфу что первые данные переданы,
    //вытаскиваем сохранённые контакты если есть,
    //сохраняем дату и время последнего звонка отправленного на сервер
    //вытаскиваем дату и время последнего звонка
    //сохраняем списки спецов полученные от сервера,
    //сохраняем тех спецов к которым обращались

    private Context context;
    private SQLiteDatabase db;
    private String dbName = "GUESSACELEB_DB";
    private String guessACelebResultsTable = "guessACelebResults";


    public WorkWithDbClass(Context context) {
        this.context = context;
        this.db = context.openOrCreateDatabase(dbName, context.MODE_PRIVATE,
                null);
    }

    public void makeTables(){
        String makeResultsTableQuery = " CREATE TABLE IF NOT EXISTS "+ guessACelebResultsTable + " ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "celebIndex INTEGER, "+
                "photoName Text(50), "+
                "flName Text(50), "+
                "scores INTEGER);";
        db.execSQL(makeResultsTableQuery);
    }

    public void recordResult(JSONObject result) throws JSONException {
        ContentValues newValues = new ContentValues();
        // Задайте значения для каждой строки.
        newValues.put("celebIndex", result.getInt("celebIndex"));
        newValues.put("photoName", result.getString("photoName"));
        newValues.put("flName", result.getString("flName"));
        newValues.put("scores", result.getInt("scores"));

        //[ ... Повторите для каждого столбца ... ]
        // Вставьте строку в вашу базу данных.
        db.insert(guessACelebResultsTable, null, newValues);
    }

    public ArrayList<JSONObject> getResults() throws JSONException {
        Cursor cursor = db.query(guessACelebResultsTable, new String[] {"celebIndex", "photoName", "flName", "scores"},null,null,null, null, null);
        //Log.d("gettingDataFromDb check", "inside getDialedPerfs");
        ArrayList<JSONObject> oldResults = new ArrayList<>();

        if (cursor!=null){

            for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() ) {
                int celebIndex = cursor.getInt(cursor.getColumnIndex("celebIndex"));
                String photoName = cursor.getString(cursor.getColumnIndex("photoName"));
                String flName = cursor.getString(cursor.getColumnIndex("flName"));
                int scores = cursor.getInt(cursor.getColumnIndex("scores"));


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("celebIndex", celebIndex);
                jsonObject.put("photoName", photoName );
                jsonObject.put("flName", flName);
                jsonObject.put("scores", scores);
                oldResults.add(jsonObject);
                Log.d("dbRead check", oldResults.toString());

            }
            cursor.close();
        }

        return oldResults;
    }

    public void deleteMyTables () {
        String dropQuery = "DROP TABLE IF EXISTS " + guessACelebResultsTable;
        db.execSQL(dropQuery);
    }

    public int getMaxIndex (){
        Cursor cursor = db.query(guessACelebResultsTable, new String [] {"MAX(celebIndex)"}, null, null, null, null, null);
        if (cursor!=null)
        {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex("MAX(celebIndex)"));
        }//"SELECT MAX(bytes) FROM ";
        return 0;
    }
}