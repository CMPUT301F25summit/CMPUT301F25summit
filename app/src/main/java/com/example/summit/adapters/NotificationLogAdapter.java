package com.example.summit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summit.R;
import com.example.summit.model.NotificationLogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationLogAdapter extends RecyclerView.Adapter<NotificationLogAdapter.ViewHolder> {

    private List<NotificationLogItem> logs = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    public void setLogs(List<NotificationLogItem> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationLogItem item = logs.get(position);

        holder.textEventTitle.setText(item.getEventTitle() != null ? item.getEventTitle() : "Unknown Event");
        holder.textTimestamp.setText(dateFormat.format(new Date(item.getTimestamp())));

        String recipient = item.getRecipientName() != null ? item.getRecipientName() : item.getRecipientId();
        holder.textRecipient.setText("To: " + (recipient != null ? recipient : "Unknown"));

        holder.textMessage.setText(item.getMessage());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEventTitle;
        TextView textTimestamp;
        TextView textRecipient;
        TextView textMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEventTitle = itemView.findViewById(R.id.text_event_title);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textRecipient = itemView.findViewById(R.id.text_recipient);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }
}
