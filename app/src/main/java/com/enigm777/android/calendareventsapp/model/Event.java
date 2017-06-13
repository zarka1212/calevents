package com.enigm777.android.calendareventsapp.model;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class Event {

    private long mEventDate;
    private String mEventTitle;
    private String mEventDescription;
    private String mEventLocation;

    public long getEventId() {
        return mEventId;
    }

    public void setEventId(long eventId) {
        mEventId = eventId;
    }

    private long mEventId;

    public Event(){}

    public Event(long eventDate, String eventTitle, String eventDescription, String eventLocation) {
        mEventDate = eventDate;
        mEventTitle = eventTitle;
        mEventDescription = eventDescription;
        mEventLocation = eventLocation;
    }

    public long getEventDate() {
        return mEventDate;
    }

    public void setEventDate(long eventDate) {
        mEventDate = eventDate;
    }

    public String getEventTitle() {
        return mEventTitle;
    }

    public void setEventTitle(String eventTitle) {
        mEventTitle = eventTitle;
    }

    public String getEventDescription() {
        return mEventDescription;
    }

    public void setEventDescription(String eventDescription) {
        mEventDescription = eventDescription;
    }

    public String getEventLocation() {
        return mEventLocation;
    }

    public void setEventLocation(String eventLocation) {
        mEventLocation = eventLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (mEventDate != event.mEventDate) return false;
        if (mEventId != event.mEventId) return false;
        if (mEventTitle != null ? !mEventTitle.equals(event.mEventTitle) : event.mEventTitle != null)
            return false;
        if (mEventDescription != null ? !mEventDescription.equals(event.mEventDescription) : event.mEventDescription != null)
            return false;
        return mEventLocation != null ? mEventLocation.equals(event.mEventLocation) : event.mEventLocation == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (mEventDate ^ (mEventDate >>> 32));
        result = 31 * result + (mEventTitle != null ? mEventTitle.hashCode() : 0);
        result = 31 * result + (mEventDescription != null ? mEventDescription.hashCode() : 0);
        result = 31 * result + (mEventLocation != null ? mEventLocation.hashCode() : 0);
        result = 31 * result + (int) (mEventId ^ (mEventId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "mEventDate=" + mEventDate +
                ", mEventTitle='" + mEventTitle + '\'' +
                ", mEventDescription='" + mEventDescription + '\'' +
                ", mEventLocation='" + mEventLocation + '\'' +
                ", mEventId=" + mEventId +
                '}';
    }
}
