package com.example.notealarm;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class ListAdapter extends ArrayAdapter<ListItem> {

    public ListAdapter(Context context, ArrayList<ListItem> arr) {
        super(context, R.layout.list_item, arr);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ListItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        }

        assert item != null;
        ((TextView) convertView.findViewById(R.id.n_time)).setText(item.time);
        ((TextView) convertView.findViewById(R.id.n_text)).setText(item.notification);

        if (System.currentTimeMillis() > item.targetTime) {
            String timeText = item.time + " - Время прошло";
            ((TextView) convertView.findViewById(R.id.n_time)).setText(timeText);
        }

        ImageButton button = (ImageButton) convertView.findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("text", item.notification);
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtras(bundle);
                PendingIntent.getBroadcast(getContext(), item.pendingIntentTime, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                remove(item);
                Toast.makeText(getContext(), "Напоминание удалено", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;

    }
}
