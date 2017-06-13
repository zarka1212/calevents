package com.enigm777.android.calendareventsapp.model;

import android.content.Context;
import android.util.Log;

import com.enigm777.android.calendareventsapp.dao.CalendarEventDao;
import com.enigm777.android.calendareventsapp.dao.EventDao;
import com.enigm777.android.calendareventsapp.service.OnEventListChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class EventContainer {
    private static final String TAG = "EventContainer";

    private ArrayList<Event> mEventList;
    private EventDao mEventDao;
    private List<OnEventListChangedListener> mOnEventListChangedListeners;

    public EventContainer(Context context){
        mEventDao = new CalendarEventDao(context);
        mOnEventListChangedListeners = new ArrayList<>();
        mEventList = new ArrayList<>();
    }

    public void addEvent(Event event){
        mEventDao.createEvent(event);
        for (OnEventListChangedListener listChangedListener : mOnEventListChangedListeners){
            listChangedListener.eventListChanged(this, mEventList);
        }
        Log.e(TAG, "addEvent(): event = " + event.toString());
    }

    public void deleteEvent(Event event){
        mEventDao.deleteEvent(event);
        for (OnEventListChangedListener listChangedListener : mOnEventListChangedListeners){
            listChangedListener.eventListChanged(this, mEventList);
        }
        Log.e(TAG, "deleteEvent(): event = " + event.toString());
    }

    public void updateEvent(Event event){
        mEventDao.updateEvent(event);
        for (OnEventListChangedListener listChangedListener : mOnEventListChangedListeners){
            listChangedListener.eventListChanged(this, mEventList);
        }
        Log.e(TAG, "updateEvent(): event = " + event.toString());
    }

    public ArrayList<Event> getEventList(){
        mEventList.clear();
        mEventList.addAll(mEventDao.retrieveEventList());
        Log.e(TAG, "getEventList()");
        return new ArrayList<>(mEventList);
    }

    public void addEventListChangedListener(OnEventListChangedListener listener){
        mOnEventListChangedListeners.add(listener);
    }

    public void removeEventListChangedListener(OnEventListChangedListener listener){
        mOnEventListChangedListeners.remove(listener);
    }

}
