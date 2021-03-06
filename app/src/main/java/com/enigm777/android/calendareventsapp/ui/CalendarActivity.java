package com.enigm777.android.calendareventsapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.enigm777.android.calendareventsapp.EventContainerProvider;
import com.enigm777.android.calendareventsapp.R;
import com.enigm777.android.calendareventsapp.model.Event;
import com.enigm777.android.calendareventsapp.model.EventContainer;
import com.enigm777.android.calendareventsapp.service.EventListLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalendarActivity";
    public static final int PERMISSION_REQUEST_CODE = 16;
    private static final int LOADER_ID = 777;
    private static final int FIRST_DAY_OF_WEEK = 2;

    public static final String EVENT_INTENT = "Operation";
    public static final String CURRENT_DATE_EVENT_INTENT = "CurrentDate";

    public static final int ADD_EVENT_CODE = 0;
    public static final int EDIT_EVENT_CODE = 1;
    public static final String EVENT_INTENT_EXTRA = "event";

    private RecyclerView mEventListRecyclerView;
    private CalendarView mCalendarView;
    private EventListAdapter mEventListAdapter;
    private EventContainer mEventContainer;
    private FloatingActionButton mAddEventButton;
    private Calendar mCurrentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mEventContainer = ((EventContainerProvider)getApplication()).getEventContainer();

        mCalendarView = (CalendarView)findViewById(R.id.calendar_view);
        mCalendarView.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);

        mEventListRecyclerView = (RecyclerView)findViewById(R.id.event_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mEventListRecyclerView.setLayoutManager(linearLayoutManager);
        mEventListRecyclerView.addItemDecoration(new DividerItemDecoration(CalendarActivity.this, DividerItemDecoration.VERTICAL));

        if(isPermissionDenied()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
        } else {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, new EventListLoaderCallbacks());
        }


        mCurrentCalendar = Calendar.getInstance();
        mEventListAdapter = new EventListAdapter(this);
        mEventListRecyclerView.setAdapter(mEventListAdapter);


        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.e(TAG, "onSelectedDayChange(): year = " + year + " month = " + month + " day = " + dayOfMonth);
                mCurrentCalendar.set(year, month, dayOfMonth);
                Log.e(TAG, "onSelectedDayChange(): mCurrentCalendar = " + mCurrentCalendar.toString());
                mEventListAdapter.setEventList(mEventContainer.getEventList(), year, month, dayOfMonth);
            }
        });


        mAddEventButton = (FloatingActionButton) findViewById(R.id.add_event_item);
        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "addEventButton pressed");
                Intent intent = new Intent(CalendarActivity.this, EventActivity.class);
                intent.putExtra(EVENT_INTENT, ADD_EVENT_CODE);
                intent.putExtra(CURRENT_DATE_EVENT_INTENT, mCurrentCalendar.getTimeInMillis());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mEventListRecyclerView.getChildLayoutPosition(v);
        Event event = mEventListAdapter.getEventFromPosition(itemPosition);
        Intent intent = new Intent(CalendarActivity.this, EventActivity.class);
        intent.putExtra(EVENT_INTENT, EDIT_EVENT_CODE);
        intent.putExtra(EVENT_INTENT_EXTRA, event);
        startActivity(intent);
        Log.e(TAG, "clicked on event, itemPosition = " + itemPosition + " event = " + event.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mEventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, new EventListLoaderCallbacks());
        }
    }

    private boolean isPermissionDenied(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED;
    }

    private class EventListLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Event>>{

        @Override
        public Loader<ArrayList<Event>> onCreateLoader(int id, Bundle args) {
            return new EventListLoader(CalendarActivity.this,(EventContainerProvider)getApplication());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Event>> loader, ArrayList<Event> data) {
            Log.e(TAG, "onLoadFinished(): loaded eventList size = " + data.size() + " current thread = " + Thread.currentThread());
            mEventListAdapter.setEventList(data, mCurrentCalendar.get(Calendar.YEAR), mCurrentCalendar.get(Calendar.MONTH),
                    mCurrentCalendar.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Event>> loader) {

        }
    }

}