package com.enigm777.android.calendareventsapp.dao;

import com.enigm777.android.calendareventsapp.model.Event;

import java.util.Date;
import java.util.List;

/**
 * Created by enigm777 on 11.06.2017.
 */

public interface EventDao {

    void createEvent(Event event);
    void deleteEvent(Event event);
    void updateEvent(Event event);
    List<Event> retrieveEventList();
}
