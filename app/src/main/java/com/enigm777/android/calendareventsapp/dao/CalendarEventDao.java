package com.enigm777.android.calendareventsapp.dao;

import android.Manifest;
import android.app.Application;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.enigm777.android.calendareventsapp.model.Event;
import com.enigm777.android.calendareventsapp.model.EventContainer;
import com.enigm777.android.calendareventsapp.ui.CalendarActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class CalendarEventDao extends AsyncQueryHandler implements EventDao {
    private static final String TAG = "CalendarEventDao";
    private static final int QUERY_TOKEN = 777;
    private static final int DEFAULT_CALENDAR_ID = 1;
    private static final int DEFAULT_EVENT_DURATION = 3600000;
    private static final String[] EVENT_PROJECTION = new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART};

    private ContentResolver mContentResolver;
    private Context mContext;


    public CalendarEventDao(Context context) {
        super(context.getContentResolver());
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    @Override
    public void createEvent(Event event) {
        Log.e(TAG, "createEvent(): event = " + event.toString());
        startInsert(QUERY_TOKEN, null, CalendarContract.Events.CONTENT_URI, createContentValuesFromEvent(event));
    }

    @Override
    public void deleteEvent(Event event) {
        Log.e(TAG, "deleteEvent(): event = " + event.toString());
        startDelete(QUERY_TOKEN, null, CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID + "=?", new String[]{String.valueOf(event.getEventId())});
    }

    @Override
    public void updateEvent(Event event) {
        Log.e(TAG, "updateEvent(): event = " + event.toString());
        startUpdate(QUERY_TOKEN, null, CalendarContract.Events.CONTENT_URI, createContentValuesFromEvent(event),
                CalendarContract.Events._ID + "=?", new String[]{String.valueOf(event.getEventId())});
    }

    @Override
    public List<Event> retrieveEventList() throws SecurityException{
        List<Event> events = new ArrayList<>();

            Cursor cursor = mContentResolver.query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    events.add(createEventFromCursor(cursor));
                    Log.e(TAG, createEventFromCursor(cursor).toString());
                    cursor.moveToNext();
                }
                cursor.close();
            }

        Log.e(TAG, "retrieveEventList(): size = " + events.size() + " currentTime: " + new Date().getTime() + " currentThread = " + Thread.currentThread());
        return events;
    }

    private ContentValues createContentValuesFromEvent(Event event){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, event.getEventTitle());
        contentValues.put(CalendarContract.Events.DESCRIPTION, event.getEventDescription());
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, event.getEventLocation());
        contentValues.put(CalendarContract.Events.DTSTART, event.getEventDate());
        contentValues.put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, String.valueOf(Calendar.getInstance().getTimeZone()));
        contentValues.put(CalendarContract.Events.DURATION, String.valueOf(DEFAULT_EVENT_DURATION));
        return contentValues;
    }

    private Event createEventFromCursor(Cursor cursor){
        Event event = new Event();
        event.setEventId(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
        event.setEventTitle(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
        event.setEventLocation(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
        event.setEventDescription(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
        event.setEventDate(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
        return event;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        Log.e(TAG, "successfully inserted new event");
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        Log.e(TAG, "successfully deleted event");
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        Log.e(TAG, "successfully updated event");
    }
}
