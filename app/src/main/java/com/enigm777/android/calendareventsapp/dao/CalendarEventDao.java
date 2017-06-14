package com.enigm777.android.calendareventsapp.dao;

import android.Manifest;
import android.app.Application;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.enigm777.android.calendareventsapp.R;
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
    private static final String SHARED_PREF_FILE_NAME = "calendar_app_pref";
    private static final String SAVED_CALENDAR_ID = "local_calendar_id";

    private static final int QUERY_TOKEN = 777;
    private static final long DEFAULT_CALENDAR_ID = -1;
    private static final int DEFAULT_EVENT_DURATION = 3600000;
    private static final String[] EVENT_PROJECTION = new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART};

    private static final String[] CALENDAR_PROJECTION = new String[]{CalendarContract.Calendars._ID};

    private ContentResolver mContentResolver;
    private Context mContext;
    private long mCalendarId;
    private SharedPreferences mSharedPreferences;


    public CalendarEventDao(Context context) {
        super(context.getContentResolver());
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        mCalendarId = DEFAULT_CALENDAR_ID;
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void createEvent(Event event) throws SecurityException{
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

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if(uri == null){
            Log.e(TAG, "something went wrong while inserting event!!!");
        } else {
            Log.e(TAG, "successfully inserted new event" + uri.toString());
        }
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

    private ContentValues createContentValuesFromEvent(Event event){
        mCalendarId = getCalendarIdIfExists();
        if (mCalendarId == DEFAULT_CALENDAR_ID || mCalendarId == 0){
            mCalendarId = initCalendar();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, event.getEventTitle());
        contentValues.put(CalendarContract.Events.DESCRIPTION, event.getEventDescription());
        contentValues.put(CalendarContract.Events.EVENT_LOCATION, event.getEventLocation());
        contentValues.put(CalendarContract.Events.DTSTART, event.getEventDate());
        contentValues.put(CalendarContract.Events.CALENDAR_ID, mCalendarId);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);
        contentValues.put(CalendarContract.Events.DTEND, event.getEventDate()+DEFAULT_EVENT_DURATION);
        Log.e(TAG, "createContentValuesFromEvent(): " + contentValues.toString());
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

    private long initCalendar() throws SecurityException{
        long calendar_id;
        Uri syncUri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, mContext.getString(R.string.app_name))
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build();

        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.NAME, mContext.getString(R.string.local_calendar_name) );
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, "");
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, mContext.getString(R.string.app_name));
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, mContext.getString(R.string.app_name));
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        Uri newCalendar = mContentResolver.insert(syncUri, cv);
        calendar_id = ContentUris.parseId(newCalendar);
        Log.e(TAG, "created new local calendar with uri = " + newCalendar);
        return calendar_id;
    }

    private long getCalendarIdIfExists() throws SecurityException{
        long calendar_id = DEFAULT_CALENDAR_ID;
        if (mSharedPreferences.contains(SAVED_CALENDAR_ID)){
            calendar_id = mSharedPreferences.getLong(SAVED_CALENDAR_ID, DEFAULT_CALENDAR_ID);
        } else {
            Cursor cursor = mContentResolver.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                calendar_id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
                cursor.close();
            }
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(SAVED_CALENDAR_ID, calendar_id);
            editor.apply();
        }
        Log.e(TAG, "found calendar with id = " + calendar_id);
        return calendar_id;
    }
}
