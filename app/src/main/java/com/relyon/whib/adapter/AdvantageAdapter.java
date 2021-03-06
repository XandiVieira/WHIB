package com.relyon.whib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.relyon.whib.R;
import com.relyon.whib.modelo.Advantage;

import java.util.ArrayList;

public class AdvantageAdapter extends ArrayAdapter<Advantage> {

    private final Context context;
    private final ArrayList<Advantage> advantages;

    private TextView text;

    public AdvantageAdapter(@NonNull Context context, ArrayList<Advantage> advantages) {
        super(context, R.layout.item_advantage, advantages);
        this.context = context;
        this.advantages = advantages;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_advantage, parent, false);

        setLayoutAttributes(rowView);

        text.setText(advantages.get(position).getDescription());

        return rowView;
    }

    private void setLayoutAttributes(View rowView) {
        text = rowView.findViewById(R.id.text);
    }
}