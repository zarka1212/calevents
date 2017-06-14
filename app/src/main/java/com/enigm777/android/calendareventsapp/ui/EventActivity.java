package com.enigm777.android.calendareventsapp.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
    private static final int DEFAULT_INTENT_EXTRA_VALUE = -1;

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private TimePicker mEventTimePicker;
    private Button mSaveEventButton;
    private Button mDeleteEventButton;
    private EventContainer mEventContainer;

    private Calendar mCurrentCalendar;
    private int mActionCode;
    private Event mCurrentEvent;

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
        mActionCode = getIntent().getIntExtra(CalendarActivity.EVENT_INTENT, DEFAULT_INTENT_EXTRA_VALUE);
        switch (mActionCode){
            case CalendarActivity.ADD_EVENT_CODE:
                mCurrentCalendar.setTimeInMillis(getIntent().getLongExtra(CalendarActivity.CURRENT_DATE_EVENT_INTENT, 0));
                mDeleteEventButton.setEnabled(false);
                break;
            case CalendarActivity.EDIT_EVENT_CODE:
                mCurrentEvent = (Event)getIntent().getSerializableExtra(CalendarActivity.EVENT_INTENT_EXTRA);
                mCurrentCalendar.setTimeInMillis(mCurrentEvent.getEventDate());
                initEventFieldsToEdit(mCurrentEvent);
        }
        mEventContainer = ((EventContainerProvider)getApplication()).getEventContainer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_save_button:
                Log.e(TAG,"save event button clicked");
                Event newEvent = constructEvent();
                if(newEvent == null){
                    Toast.makeText(this, R.string.empty_fields_toast_message, Toast.LENGTH_LONG).show();
                    break;
                }
                if(mActionCode == CalendarActivity.ADD_EVENT_CODE) {
                    mEventContainer.addEvent(constructEvent());
                } else {
                    mEventContainer.updateEvent(constructEvent());
                }
                finish();
                break;
            case R.id.event_delete_button:
                Log.e(TAG, "delete event button clicked: event = " + mCurrentEvent.toString());
                if(mCurrentEvent != null){
                    mEventContainer.deleteEvent(mCurrentEvent);
                }
                finish();
                break;

        }
    }

    private Event constructEvent(){
        if(TextUtils.isEmpty(mTitleEditText.getText())){
            return null;
        } else {
            if(mCurrentEvent == null) {
                mCurrentEvent = new Event();
            }
            mCurrentEvent.setEventTitle(mTitleEditText.getText().toString().trim());
            mCurrentEvent.setEventDescription(mDescriptionEditText.getText().toString().trim());
            mCurrentEvent.setEventLocation(mDescriptionEditText.getText().toString().trim());
            mCurrentCalendar.set(Calendar.HOUR_OF_DAY, mEventTimePicker.getCurrentHour());
            mCurrentCalendar.set(Calendar.MINUTE, mEventTimePicker.getCurrentMinute());
            mCurrentEvent.setEventDate(mCurrentCalendar.getTimeInMillis());
        }
        return mCurrentEvent;
    }

    private void initEventFieldsToEdit(Event event){
        Log.e(TAG, "initEventFieldsToEdit()");
        mTitleEditText.setText(event.getEventTitle());
        mDescriptionEditText.setText(event.getEventDescription());
        mLocationEditText.setText(event.getEventLocation());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mEventTimePicker.setHour(mCurrentCalendar.get(Calendar.HOUR_OF_DAY));
            mEventTimePicker.setHour(mCurrentCalendar.get(Calendar.MINUTE));
        }
    }
}
