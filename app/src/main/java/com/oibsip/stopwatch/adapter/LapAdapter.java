package com.oibsip.stopwatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oibsip.unitconverter.R;
import com.oibsip.stopwatch.model.LapItem;

import java.util.List;

/**
 * Custom adapter to display recorded stopwatch laps in a clean ListView.
 */
public class LapAdapter extends ArrayAdapter<LapItem> {

    public LapAdapter(@NonNull Context context, @NonNull List<LapItem> laps) {
        super(context, R.layout.item_lap, laps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lap, parent, false);
        }

        LapItem item = getItem(position);
        if (item != null) {
            TextView tvLapNumber = convertView.findViewById(R.id.tvLapNumber);
            TextView tvLapDuration = convertView.findViewById(R.id.tvLapDuration);
            TextView tvTotalElapsed = convertView.findViewById(R.id.tvTotalElapsed);

            tvLapNumber.setText(String.format("Lap %02d", item.getLapNumber()));
            tvLapDuration.setText("+" + item.getFormattedLapDuration());
            tvTotalElapsed.setText(item.getFormattedTotalElapsed());
        }

        return convertView;
    }
}
