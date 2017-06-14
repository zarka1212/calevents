package com.enigm777.android.calendareventsapp.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.enigm777.android.calendareventsapp.EventContainerProvider;
import com.enigm777.android.calendareventsapp.R;
import com.enigm777.android.calendareventsapp.model.Event;
import com.enigm777.android.calendareventsapp.model.EventContainer;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by enigm777 on 13.06.2017.
 */

public class EventActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "EventActivity";

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private TimePicker mEventTimePicker;
    private Button mSaveEventButton;
    private Button mDeleteEventButton;
    private EventContainer mEventContainer;

    private Calendar mCurrentCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_layout);

        mTitleEditText = (EditText)findViewById(R.id.event_title_edit_text);
        mDescriptionEditText = (EditText) findViewById(R.id.event_description_edit_text);
        mLocationEditText = (EditText)findViewById(R.id.event_location_edit_text);
        mEventTimePicker = (TimePicker)findViewById(R.id.event_time_picker);
        mSaveEventButton = (Button)findViewById(R.id.event_save_button);
        mDeleteEventButton = (Button)findViewById(R.id.event_delete_button);

        mSaveEventButton.setOnClickListener(this);
        mDeleteEventButton.setOnClickListener(this);

        mCurrentCalendar = Calendar.getInstance();
        mCurrentCalendar.setTimeInMillis(getIntent().getLongExtra(CalendarActivity.CURRENT_DATE_EVENT_INTENT, System.currentTimeMillis()));

        mEventContainer = ((EventContainerProvider)getApplication()).getEventContainer();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_save_button:
                Log.e(TAG,"save event button clicked");
                mEventContainer.addEvent(constructEvent());
                finish();
                break;

        }
    }

    private Event constructEvent(){
        Event event = new Event();

        if(!TextUtils.isEmpty(mTitleEditText.getText())||
                !TextUtils.isEmpty(mDescriptionEditText.getText())||
                !TextUtils.isEmpty(mLocationEditText.getText())){
            event.setEventTitle(mTitleEditText.getText().toString().trim());
            event.setEventDescription(mDescriptionEditText.getText().toString().trim());
            event.setEventLocation(mDescriptionEditText.getText().toString().trim());
            mCurrentCalendar.set(Calendar.HOUR, mEventTimePicker.getCurrentHour());
            mCurrentCalendar.set(Calendar.MINUTE, mEventTimePicker.getCurrentMinute());
            event.setEventDate(mCurrentCalendar.getTimeInMillis());
        }
        return event;
    }
}
