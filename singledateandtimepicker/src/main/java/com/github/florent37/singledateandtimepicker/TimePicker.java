package com.github.florent37.singledateandtimepicker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;


import com.github.florent37.singledateandtimepicker.widget.WheelAmPmPicker;
import com.github.florent37.singledateandtimepicker.widget.WheelHourPicker;
import com.github.florent37.singledateandtimepicker.widget.WheelMinutePicker;
import com.github.florent37.singledateandtimepicker.widget.WheelPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimePicker extends ConstraintLayout {
    private static final int PM_HOUR_ADDITION = 12;

    private static final CharSequence FORMAT_24_HOUR = "EEE d MMM H:mm";
    private static final CharSequence FORMAT_12_HOUR = "EEE d MMM h:mm a";

    @NonNull
    private final WheelMinutePicker minutesPicker;
    @NonNull
    private final WheelHourPicker hoursPicker;
    @NonNull
    private final WheelAmPmPicker amPmPicker;
    @NonNull
    private final TextView timeSeparator;

    private List<WheelPicker> pickers = new ArrayList<>();

    private List<OnDateChangedListener> listeners = new ArrayList<>();

    @NonNull
    private Date defaultDate;

    private boolean isAmPm;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        defaultDate = new Date();
        isAmPm = !(DateFormat.is24HourFormat(context));

        inflate(context, R.layout.time_picker, this);

        hoursPicker = findViewById(R.id.hoursPicker);
        minutesPicker = findViewById(R.id.minutesPicker);
        amPmPicker = findViewById(R.id.amPmPicker);
        timeSeparator = findViewById(R.id.timeSeparator);

        pickers.addAll(Arrays.asList(
                hoursPicker,
                minutesPicker,
                amPmPicker
        ));

        minutesPicker.setStepMinutes(1);

        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        hoursPicker
                .setOnFinishedLoopListener(new WheelHourPicker.FinishedLoopListener() {
                    @Override
                    public void onFinishedLoop(WheelHourPicker picker) {
                    }
                })
                .setHourChangedListener(new WheelHourPicker.OnHourChangedListener() {
                    @Override
                    public void onHourChanged(WheelHourPicker picker, int hour) {
                        updateListener();
                    }
                });


        minutesPicker
                .setOnMinuteChangedListener(new WheelMinutePicker.OnMinuteChangedListener() {
                    @Override
                    public void onMinuteChanged(WheelMinutePicker picker, int minutes) {
                        updateListener();
                    }
                })
                .setOnFinishedLoopListener(new WheelMinutePicker.OnFinishedLoopListener() {
                    @Override
                    public void onFinishedLoop(WheelMinutePicker picker) {
                        hoursPicker.scrollTo(hoursPicker.getCurrentItemPosition() + 1);
                    }
                });

        amPmPicker
                .setAmPmListener(new WheelAmPmPicker.AmPmListener() {
                    @Override
                    public void onAmPmChanged(WheelAmPmPicker picker, boolean isAm) {
                        updateListener();
                    }
                });

        setDefaultDate(this.defaultDate); //update displayed date
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (WheelPicker picker : pickers) {
            picker.setEnabled(enabled);
        }
    }

    public void setTextColor(int textColor) {
        int aColor = getResources().getColor(textColor);
        timeSeparator.setTextColor(aColor);
        for (WheelPicker picker : pickers) {
            picker.setSelectedItemTextColor(aColor);
        }
    }

    public void setIsAmPm(boolean isAmPm) {
        this.isAmPm = isAmPm;

        amPmPicker.setVisibility(isAmPm ? VISIBLE : GONE);
        hoursPicker.setIsAmPm(isAmPm);
    }

    public boolean isAmPm() {
        return isAmPm;
    }

    public void setCustomLocale(Locale locale) {
        for (WheelPicker p : pickers) {
            p.setCustomLocale(locale);
            p.updateAdapter();
        }
    }

    public void addOnDateChangedListener(OnDateChangedListener listener) {
        this.listeners.add(listener);
    }

    public void removeOnDateChangedListener(OnDateChangedListener listener) {
        this.listeners.remove(listener);
    }

    public Date getDate() {
        int hour = hoursPicker.getCurrentHour();
        if (isAmPm && amPmPicker.isPm()) {
            hour += PM_HOUR_ADDITION;
        }
        final int minute = minutesPicker.getCurrentMinute();

        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTime();
    }


    public void setDefaultDate(Date date) {
        if (date != null) {
            this.defaultDate = date;

            for (WheelPicker picker : pickers) {
                picker.setDefaultDate(defaultDate);
            }
        }
    }

    public void selectDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }

        final Date date = calendar.getTime();
        for (WheelPicker picker : pickers) {
            picker.selectDate(date);
        }
    }

    private void updateListener() {
        final Date date = getDate();
        final CharSequence format = isAmPm ? FORMAT_12_HOUR : FORMAT_24_HOUR;
        final String displayed = DateFormat.format(format, date).toString();
        for (OnDateChangedListener listener : listeners) {
            listener.onDateChanged(displayed, date);
        }
    }

    private void init(Context context, AttributeSet attrs) {

        setIsAmPm(this.isAmPm);
        amPmPicker.setCyclic(false);
    }

    public interface OnDateChangedListener {
        void onDateChanged(String displayed, Date date);
    }
}
