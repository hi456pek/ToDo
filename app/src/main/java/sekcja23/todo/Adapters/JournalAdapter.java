package sekcja23.todo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sekcja23.todo.Models.JournalEntry;
import sekcja23.todo.R;

/**
 * Created by mwojtowicz on 17.01.2018.
 */

public class JournalAdapter extends ArrayAdapter<JournalEntry> {

    Context context;
    int layoutResourceId;
    ArrayList<JournalEntry> data = null;

    public JournalAdapter(Context context, int resource, List<JournalEntry> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        JournalHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new JournalHolder();
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.content = (TextView)row.findViewById(R.id.content);

            row.setTag(holder);
        }
        else
        {
            holder = (JournalHolder) row.getTag();
        }

        JournalEntry item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());

        return row;
    }

    private class JournalHolder {
        public TextView title;
        public TextView content;

    }
}
