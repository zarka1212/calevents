package com.enigm777.android.calendareventsapp.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enigm777.android.calendareventsapp.R;
import com.enigm777.android.calendareventsapp.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by enigm777 on 11.06.2017.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventItemViewHolder> {
    private static final String TAG = "EventListAdapter";

    private List<Event> mEventList;
    private View.OnClickListener mItemClickListener;

    public EventListAdapter(View.OnClickListener listener){
        mItemClickListener = listener;
        mEventList = new ArrayList<>();
        Log.e(TAG, "adapter created: eventList size = " + mEventList.size());
    }

    public void setEventList(ArrayList<Event> eventList, int year, int month, int dayOfMonth){
        mEventList.clear();
        mEventList.addAll(getCurrentDateEventList(eventList,year,month,dayOfMonth));
        notifyDataSetChanged();
        Log.e(TAG, "setEventList(), new eventList = " + mEventList.size());
    }


    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View eventItemView = inflater.inflate(R.layout.event_layout,null,false);
        eventItemView.setOnClickListener(mItemClickListener);
        return new EventItemViewHolder(eventItemView);
    }

    @Override
    public void onBindViewHolder(EventItemViewHolder holder, int position) {
        holder.mEventTitleTextView.setText(mEventList.get(position).getEventTitle());
        holder.mEventPositionTextView.setText(String.valueOf(position+1));
    }



    @Override
    public int getItemCount() {
        return mEventList.size();
    }


    static class EventItemViewHolder extends RecyclerView.ViewHolder{

        TextView mEventTitleTextView;
        TextView mEventPositionTextView;

        public EventItemViewHolder(View itemView) {
            super(itemView);
            mEventPositionTextView = (TextView)itemView.findViewById(R.id.event_item_position);
            mEventTitleTextView = (TextView)itemView.findViewById(R.id.event_title_text_view);
        }
    }


    private ArrayList<Event> getCurrentDateEventList(ArrayList<Event> eventList, int year, int month, int dayOfMonth){
        ArrayList<Event> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for(Event event : eventList){
            calendar.setTimeInMillis(event.getEventDate());
            if(calendar.get(Calendar.YEAR)==year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth){
                events.add(event);
            }
        }
        Log.e(TAG, "getCurrentDateEventList(): old eventList size = " + eventList.size() + " new size = " + events.size());
        return events;
    }

    public Event getEventFromPosition(int position){
        return mEventList.get(position);
    }
}
