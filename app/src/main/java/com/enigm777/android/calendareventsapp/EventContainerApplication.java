package com.enigm777.android.calendareventsapp;

import android.app.Application;

import com.enigm777.android.calendareventsapp.model.EventContainer;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class EventContainerApplication extends Application implements EventContainerProvider {
    private EventContainer mEventContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        mEventContainer = new EventContainer(getApplicationContext());
    }

    @Override
    public EventContainer getEventContainer() {
        return mEventContainer;
    }
}
