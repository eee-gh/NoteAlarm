package com.example.notealarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ImageButton plus;
    Button timeSelect;
    Button create;
    int sYear;
    int sMonth;
    int sDay;
    int sHour;
    int sMinute;

    void showDialog() {
        sYear = -1;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.time_select);


        timeSelect = dialog.findViewById(R.id.time_choose);
        timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDialog();
            }
        });

        create = dialog.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ScheduleExactAlarm")
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                GregorianCalendar dateStart = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0);
                GregorianCalendar dateEnd = new GregorianCalendar(sYear, sMonth, sDay, sHour, sMinute, 0);

                if (sYear == -1) {
                    Toast.makeText(getApplicationContext(), "Выберите дату и время", Toast.LENGTH_LONG).show();
                } else if (dateStart.getTimeInMillis() >= dateEnd.getTimeInMillis()) {
                    Toast.makeText(getApplicationContext(), "Выберите корректное время", Toast.LENGTH_LONG).show();
                } else {
                    long time = dateEnd.getTimeInMillis();

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time, getAlarmInfo());

                    manager.setAlarmClock(alarmClockInfo, getAlarmAction());

                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Напоминание установлено", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    void CalendarDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int mouth, int day) {
                sYear = year;
                sMonth = mouth;
                sDay = day;
                ClockDialog();
            }
        };
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    void ClockDialog() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                sHour = hour;
                sMinute = minute;
            }
        };
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    PendingIntent getAlarmInfo() {
        Intent alarmInfo = new Intent(this, MainActivity.class);
        alarmInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 0, alarmInfo, PendingIntent.FLAG_MUTABLE);
    }

    PendingIntent getAlarmAction() {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_MUTABLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plus = findViewById(R.id.plus_button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
}