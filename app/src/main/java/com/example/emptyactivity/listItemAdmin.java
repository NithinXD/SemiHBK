package com.example.emptyactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class listItemAdmin extends BaseAdapter {

    private Context context;
    private List<ListItem> listItems;
    private FirebaseFirestore db;

    public listItemAdmin(Context context, List<ListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_admin, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView mainText = convertView.findViewById(R.id.mainText);
        TextView subText = convertView.findViewById(R.id.subText);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        ListItem listItem = listItems.get(position);

        icon.setImageResource(listItem.getIconResource());
        mainText.setText(listItem.getMainText());
        subText.setText(listItem.getSubText());

        deleteButton.setOnClickListener(v -> {
            deleteBooking(listItem.getMainText(), listItem.getSubText(), position);
        });

        return convertView;
    }

    private void deleteBooking(String mainText, String subText, int position) {
        // Extract timeSlot and userName from mainText and subText
        String timeSlot = mainText.replace("SLOT: ", "");
        String bookedBy = subText.replace("Booked By: ", "");
        String[] bookedByParts = bookedBy.split(" ");
        String name = bookedByParts[0];
        String department = bookedByParts[1];

        // Query the booking to delete based on timeSlot, name, and department
        db.collection("bookings")
                .whereEqualTo("timeSlot", timeSlot)
                .whereEqualTo("name", name)
                .whereEqualTo("department", department)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Delete the booking document(s)
                            querySnapshot.getDocuments().forEach(documentSnapshot -> {
                                documentSnapshot.getReference().delete();
                            });
                            listItems.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Booking deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Booking not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error deleting booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateData(List<ListItem> newItems) {
        this.listItems = newItems;
        notifyDataSetChanged();
    }
}
