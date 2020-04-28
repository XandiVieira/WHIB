package com.relyon.whib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.relyon.whib.modelo.Advantage;

import java.util.ArrayList;

public class AdvantageAdapter extends ArrayAdapter {

    private final Context context;
    private final ArrayList<Advantage> elementos;

    AdvantageAdapter(@NonNull Context context, ArrayList<Advantage> elementos) {
        super(context, R.layout.resource_item, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.resource_item, parent, false);

        TextView text = rowView.findViewById(R.id.text);

        text.setText(elementos.get(position).getDescription());

        return rowView;
    }
}
