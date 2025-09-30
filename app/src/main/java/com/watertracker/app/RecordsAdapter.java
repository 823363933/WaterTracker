package com.watertracker.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private List<WaterRecord> records;

    public RecordsAdapter(List<WaterRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterRecord record = records.get(position);
        holder.timeText.setText(record.getTime());
        holder.amountText.setText("+" + record.getAmount() + "ml");
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView amountText;

        ViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.recordTimeText);
            amountText = itemView.findViewById(R.id.recordAmountText);
        }
    }
}