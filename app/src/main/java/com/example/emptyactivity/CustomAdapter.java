package com.example.emptyactivity;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<ListItem> {

    private Context mContext;
    private List<ListItem> mListItems;

    public CustomAdapter(@NonNull Context context, List<ListItem> listItems) {
        super(context, 0, listItems);
        mContext = context;
        mListItems = listItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent, false);
        }

        // Get the current item
        ListItem currentItem = mListItems.get(position);

        // Set the icon
        ImageView iconImageView = listItemView.findViewById(R.id.icon);
        iconImageView.setImageResource(currentItem.getIconResource());

        // Set the main text
        TextView mainTextView = listItemView.findViewById(R.id.text_main);
        mainTextView.setText(currentItem.getMainText());
        // Set text size programmatically (in sp)

        // Set the subtext
        TextView subTextView = listItemView.findViewById(R.id.text_sub);
        subTextView.setText(currentItem.getSubText());
        // Set text size programmatically (in sp)
        subTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Adjust as needed

        return listItemView;
    }

    // Method to update the data in the adapter
    public void updateData(List<ListItem> newList) {
        mListItems.clear();
        mListItems.addAll(newList);
        notifyDataSetChanged();
    }
}
