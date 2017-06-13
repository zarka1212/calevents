package com.enigm777.android.calendareventsapp.service;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.enigm777.android.calendareventsapp.EventContainerProvider;
import com.enigm777.android.calendareventsapp.model.Event;
import com.enigm777.android.calendareventsapp.model.EventContainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class EventListLoader extends AsyncTaskLoader<ArrayList<Event>> implements OnEventListChangedListener {
    private static final String TAG = "EventListLoader";

    private EventContainer mEventContainer;
    private List<Event> mEventListCache;

    public EventListLoader(Context context, EventContainerProvider provider) {
        super(context);
        mEventContainer = provider.getEventContainer();
        mEventContainer.addEventListChangedListener(this);
        Log.e(TAG, "eventListLoader created");
    }

    @Override
    public ArrayList<Event> loadInBackground() {
        return mEventContainer.getEventList();
    }

    @Override
    protected void onStartLoading() {
        Log.e(TAG, "onStartLoading()");
        super.onStartLoading();
        if(mEventListCache == null || takeContentChanged()){
            forceLoad();
            Log.e(TAG, "forceLoad()");
        }
    }

    @Override
    public void deliverResult(ArrayList<Event> data) {
        super.deliverResult(data);
        mEventListCache = data;
    }

    @Override
    protected void onReset() {
        super.onReset();
        mEventContainer.removeEventListChangedListener(this);
    }

    @Override
    public void eventListChanged(EventContainer sender, List<Event> eventList) {
        Log.e(TAG, "eventListChanged()");
        onContentChanged();
    }
}
