package com.example.notealarm;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ArrayList<ListItem> alarmArray;
    SharedPreferences alarms;
    ListAdapter adapter;
    ListView lv;
    ImageButton plus;
    EditText n_text;
    Button timeSelect;
    Button create;
    int sYear;
    int sMonth;
    int sDay;
    int sHour;
    int sMinute;

    void showDialog() {
        sYear = -1;
        sMinute = -1;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.time_select);

        n_text = dialog.findViewById(R.id.n_text);

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

                if (n_text.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Введите текст напоминания", Toast.LENGTH_SHORT).show();
                } else if (sYear == -1) {
                    Toast.makeText(getApplicationContext(), "Выберите дату и время", Toast.LENGTH_SHORT).show();
                } else if (dateStart.getTimeInMillis() >= dateEnd.getTimeInMillis()) {
                    Toast.makeText(getApplicationContext(), "Выберите корректное время", Toast.LENGTH_SHORT).show();
                } else {
                    long time = dateEnd.getTimeInMillis();
                    String text = n_text.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("text", text);

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    intent.putExtras(bundle);
                    int current_time = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, current_time, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(time, pendingIntent);
                    manager.setAlarmClock(info, pendingIntent);


                    ListItem listItem = new ListItem(timeFormat(time), text, current_time, time);
                    alarmArray.add(listItem);
                    SharedPreferences.Editor prefEditor = alarms.edit();
                    prefEditor.putString("list", serialize(alarmArray));
                    prefEditor.apply();
                    lv.setAdapter(adapter);


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

                if (sMinute != -1) {
                    GregorianCalendar buttonDate = new GregorianCalendar(sYear, sMonth, sDay, sHour, sMinute, 0);
                    timeSelect.setText(timeFormat(buttonDate.getTimeInMillis()));
                }

            }
        };
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    Uri getUri() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (notification == null) {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        return notification;
    }

    void createNotificationChannel() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationChannel channel = new NotificationChannel("notealarm", "Уведомления", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setSound(getUri(), audioAttributes);
        channel.setVibrationPattern(new long[]{0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500});

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static String serialize(ArrayList<ListItem> arr) {
        Gson gson = new Gson();
        return gson.toJson(arr);
    }

    public static ArrayList<ListItem> deserialize(String s) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        return gson.fromJson(s, listType);
    }

    public static String timeFormat(long timeMills) {
        Date date = new Date(timeMills);
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
        return formater.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            final String PERMISSION_POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS;
            if (!checkPermission(this, PERMISSION_POST_NOTIFICATIONS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        createNotificationChannel();
        alarms = getPreferences(MODE_PRIVATE);
        if (alarms.contains("list")) {
            alarmArray = deserialize(alarms.getString("list", ""));
        } else {
            alarmArray = new ArrayList<ListItem>();
        }


        adapter = new ListAdapter(this, alarmArray);
        lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(adapter);

        plus = findViewById(R.id.plus_button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor prefEditor = alarms.edit();
        prefEditor.putString("list", serialize(alarmArray));
        prefEditor.apply();
        super.onStop();
    }

    @Override
    public void onDetachedFromWindow() {
        SharedPreferences.Editor prefEditor = alarms.edit();
        prefEditor.putString("list", serialize(alarmArray));
        prefEditor.apply();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor prefEditor = alarms.edit();
        prefEditor.putString("list", serialize(alarmArray));
        prefEditor.apply();
        super.onDestroy();
    }
}