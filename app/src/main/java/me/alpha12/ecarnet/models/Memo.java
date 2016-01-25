package me.alpha12.ecarnet.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.alpha12.ecarnet.database.DatabaseManager;

/**
 * Created by guilhem on 13/01/2016.
 */
public class Memo {

    private int id;
    private String title;
    private Calendar dateNote;
    private Calendar limitDate;
    private Calendar modifDate;
    private int kilometers;
    private int isNotifSet;
    private int isArchived;
    private int carId;

    /* Contructors */

    public Memo(int id, String title, Calendar limit, Calendar date, Calendar modif, int distance, boolean notif, boolean archived, int idCar) {
        this.id = id;
        this.title = title;
        this.dateNote = date;
        this.limitDate = limit;
        this.modifDate = modif;
        this.kilometers = distance;
        if(notif)
            this.isNotifSet = 1;
        else this.isNotifSet = 0;
        if(archived)
            this.isArchived = 1;
        else this.isArchived = 0;
        this.carId = idCar;
    }

    public void setAll(int id, String title, Calendar limit, Calendar date, Calendar modif, int distance, boolean notif, boolean archived, int idCar){
        this.id = id;
        this.title = title;
        this.dateNote = date;
        this.limitDate = limit;
        this.modifDate = modif;
        this.kilometers = distance;
        if(notif)
            this.isNotifSet = 1;
        else this.isNotifSet = 0;
        if(archived)
            this.isArchived = 1;
        else this.isArchived = 0;
        this.carId = idCar;
    }

    public Memo(Cursor cursor) {
        this.id         = DatabaseManager.extractInt(cursor, DBMemo.C_ID);
        this.carId      = DatabaseManager.extractInt(cursor, DBMemo.C_CAR_ID);
        this.title      = DatabaseManager.extractString(cursor, DBMemo.C_TITLE);
        this.kilometers = DatabaseManager.extractInt(cursor, DBMemo.C_KILOMETERS);
        this.dateNote   = DatabaseManager.extractCalendar(cursor, DBMemo.C_DATE_CREATED);
        this.modifDate =  DatabaseManager.extractCalendar(cursor, DBMemo.C_LAST_MODIFICATION);
        this.limitDate  = DatabaseManager.extractCalendar(cursor, DBMemo.C_DATE_LIMIT_SET);
        this.isNotifSet = DatabaseManager.extractInt(cursor, DBMemo.C_NOTIFICATION);
        this.isArchived = DatabaseManager.extractInt(cursor, DBMemo.C_ARCHIVED);
    }

    public void persist(boolean update) {
        ContentValues newValues = new ContentValues();

        if (this.id > 0)
            newValues.put(DBMemo.C_ID, this.id);
        else
            update = false;

        newValues.put(DBMemo.C_TITLE, this.title);

        if (this.dateNote != null)
            newValues.put(DBMemo.C_DATE_CREATED, this.dateNote.getTimeInMillis());

        if (this.limitDate != null)
            newValues.put(DBMemo.C_DATE_LIMIT_SET, this.limitDate.getTimeInMillis());

        if(this.modifDate != null)
            newValues.put(DBMemo.C_LAST_MODIFICATION, this.modifDate.getTimeInMillis());

        newValues.put(DBMemo.C_KILOMETERS, this.kilometers);
        newValues.put(DBMemo.C_NOTIFICATION, this.isNotifSet);
        newValues.put(DBMemo.C_ARCHIVED, this.isArchived());
        newValues.put(DBMemo.C_CAR_ID, this.carId);


        if (update){
            DatabaseManager.getCurrentDatabase().update(DBMemo.TABLE_NAME, newValues, DBMemo.C_ID+"="+this.id, null);
        } else {
            long insertedId = DatabaseManager.getCurrentDatabase().insert(DBMemo.TABLE_NAME, null, newValues);

            if (this.id <= 0)
                this.id = (int) insertedId;
        }
    }


    public static ArrayList<Memo> findAllByCar(int carId) {
        ArrayList<Memo> result = new ArrayList<>();
        Cursor cursor = DatabaseManager.getCurrentDatabase().rawQuery(
                "SELECT * FROM "+DBMemo.TABLE_NAME+" WHERE "+DBMemo.C_CAR_ID+" = " + carId,
                null
        );
        while(cursor.moveToNext()) {
            result.add(new Memo(cursor));
        }
        return result;
    }





    public static Memo getLastNote(int carId) {
        Cursor cursor = DatabaseManager.getCurrentDatabase().rawQuery("SELECT * FROM " + DBMemo.TABLE_NAME +
                " WHERE car_id = " + carId + " ORDER BY " + DBMemo.C_DATE_LIMIT_SET +", " +DBMemo.C_KILOMETERS + " DESC LIMIT 1", null);

        if (cursor.moveToNext()) {
            return new Memo(cursor);
        }
        return null;
    }

    public static Memo findMemoById(int memoId) {
        Cursor cursor = DatabaseManager.getCurrentDatabase().rawQuery("SELECT * FROM " + DBMemo.TABLE_NAME +
                " WHERE " +DBMemo.C_ID+" = " + memoId, null);

        if (cursor.moveToNext()) {
            return new Memo(cursor);
        }
        return null;
    }



    public static abstract class DBMemo implements BaseColumns {
        public static final String TABLE_NAME = "memos";
        public static final String C_ID = "id";
        public static final String C_TITLE = "title";
        public static final String C_DATE_LIMIT_SET = "date_limit";
        public static final String C_DATE_CREATED = "creation_date";
        public static final String C_LAST_MODIFICATION = "mod_date";
        public static final String C_KILOMETERS = "kilometers";
        public static final String C_NOTIFICATION = "notification";
        public static final String C_ARCHIVED = "archived";
        public static final String C_CAR_ID = "car_id";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + C_TITLE + " TEXT NOT NULL,"
                        + C_DATE_LIMIT_SET + " NUMERIC,"
                        + C_DATE_CREATED + " NUMERIC NOT NULL,"
                        + C_LAST_MODIFICATION + " NUMERIC,"
                        + C_KILOMETERS + " INTEGER,"
                        + C_NOTIFICATION + " INTEGER NOT NULL,"
                        + C_ARCHIVED + " INTEGER NOT NULL,"
                        + C_CAR_ID + " INTEGER NOT NULL"
                        + ");";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDateNote() {
        return dateNote;
    }

    public void setDateNote(Calendar dateNote) {
        this.dateNote = dateNote;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public int isNotifSet() {
        return isNotifSet;
    }

    public void setIsNotifSet(boolean isNotifSet) {
        if(isNotifSet)
        this.isNotifSet = 1;
        else this.isNotifSet = 0;
    }

    public Calendar getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(Calendar limitDate) {
        this.limitDate = limitDate;
    }


    public int getCarId() {
        return carId;
    }

    public int isArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        if(isArchived)
        this.isArchived = 1;
        else this.isArchived = 0;
    }

    public Calendar getModifDate() {
        return modifDate;
    }
}
