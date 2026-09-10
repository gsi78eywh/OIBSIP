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
 * Custom adapter to display recorded stopwatch laps in a ListView.
 * 
 * How this adapter works (Easy to explain):
 * 1. Takes the list of LapItem objects.
 * 2. For each visible row in the ListView, inflates 'item_lap.xml'.
 * 3. Populates the Lap Number, Lap Split Duration, and Cumulative Total Time.
 * 4. Reuses (recycles) existing views via 'convertView' to ensure smooth scrolling and save memory.
 */
public class LapAdapter extends ArrayAdapter<LapItem> {

    public LapAdapter(@NonNull Context context, @NonNull List<LapItem> laps) {
        super(context, R.layout.item_lap, laps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Step 1: Recycle view if already inflated, or inflate new layout if null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lap, parent, false);
        }

        // Step 2: Retrieve the LapItem for the current row position
        LapItem item = getItem(position);
        if (item != null) {
            // Step 3: Connect to UI TextViews inside item_lap.xml
            TextView tvLapNumber = convertView.findViewById(R.id.tvLapNumber);
            TextView tvLapDuration = convertView.findViewById(R.id.tvLapDuration);
            TextView tvTotalElapsed = convertView.findViewById(R.id.tvTotalElapsed);

            // Step 4: Display formatted lap text
            tvLapNumber.setText(String.format("Lap %02d", item.getLapNumber()));
            tvLapDuration.setText("+" + item.getFormattedLapDuration());
            tvTotalElapsed.setText(item.getFormattedTotalElapsed());
        }

        return convertView;
    }
}
