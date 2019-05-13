package com.github.florent37.sample.singledateandtimepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class SingleDatePickerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_date_picker_activity_main);

        final TimePicker singleDateAndTimePicker = findViewById(R.id.single_day_picker);
        singleDateAndTimePicker.addOnDateChangedListener(new TimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                display(displayed);
            }
        });

        findViewById(R.id.toggleColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //singleDateAndTimePicker.setTextColor(R.color.hot_purple);
                //singleDateAndTimePicker.setIsAmPm(!singleDateAndTimePicker.isAmPm());
                singleDateAndTimePicker.selectDate(Calendar.getInstance());
            }
        });

    }

    private void display(String toDisplay) {
        Toast.makeText(this, toDisplay, Toast.LENGTH_SHORT).show();
    }
}
