package sekcja23.todo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sekcja23.todo.Models.Addon;
import sekcja23.todo.R;


public class AddonAdapter extends ArrayAdapter<Addon> {


    private Context context;
    private int layoutResourceId;
    private ArrayList<Addon> data = null;

    public AddonAdapter(Context context, int resource, List<Addon> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AddonAdapter.AddonHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AddonAdapter.AddonHolder();
            holder.text = row.findViewById(R.id.addonName);
            holder.icon = row.findViewById(R.id.addonImage);

            row.setTag(holder);
        } else {
            holder = (AddonHolder) row.getTag();
        }

        Addon item = data.get(position);
        holder.text.setText(item.getText());
        holder.icon.setImageResource(item.getIcon());
        holder.remove = new Button(context);
        holder.remove.setOnClickListener((View view) -> {
            data.remove(position);

        });

        return row;
    }

    private class AddonHolder {
        public TextView text;
        public ImageView icon;
        public Button remove;

    }

}
