package com.enigm777.android.calendareventsapp.service;

import com.enigm777.android.calendareventsapp.model.Event;
import com.enigm777.android.calendareventsapp.model.EventContainer;

import java.util.List;

/**
 * Created by enigm777 on 11.06.2017.
 */

public interface OnEventListChangedListener {
    void eventListChanged(EventContainer sender, List<Event> eventList);
}
