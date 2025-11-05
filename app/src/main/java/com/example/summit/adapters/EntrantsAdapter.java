package com.example.summit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.summit.R;
import com.example.summit.model.Entrant;

import java.util.List;

public class EntrantsAdapter extends ArrayAdapter<Entrant> {

    private final Context context;
    private final List<Entrant> entrants;

    public EntrantsAdapter(@NonNull Context context, @NonNull List<Entrant> entrants) {
        super(context, R.layout.item_entrant_listview, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            listItem = inflater.inflate(R.layout.item_entrant_listview, parent, false);
        }

        Entrant currentEntrant = entrants.get(position);

        TextView textViewName = listItem.findViewById(R.id.text_view_entrant_name);
        TextView textViewEmail = listItem.findViewById(R.id.text_view_entrant_email);

        if (currentEntrant != null) {
            String name = currentEntrant.getName();
            textViewName.setText(name != null ? name : "Unknown");

            String email = currentEntrant.getEmail();
            if (email != null && !email.isEmpty()) {
                textViewEmail.setText(email);
                textViewEmail.setVisibility(View.VISIBLE);
            } else {
                textViewEmail.setVisibility(View.GONE);
            }
        }

        return listItem;
    }
}