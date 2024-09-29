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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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
        // Extract timeSlots as a list from mainText
        String timeSlotString = mainText.replace("SLOT: [", "").replace("]", "");
        String[] timeSlotsArray = timeSlotString.split(", ");
        List<String> timeSlotsList = new ArrayList<>(Arrays.asList(timeSlotsArray));

        // Debugging: Print extracted time slots
        System.out.println("DEBUG: Extracted timeSlotsList: " + timeSlotsList);

        // Extracting Name and Department using a more robust method
        String bookedBy = subText.substring(subText.indexOf("Booked By: ") + 11, subText.indexOf("\nHall:")).trim();
        String[] bookedByParts = bookedBy.split(" ");

        // Debugging: Print the value of bookedByParts
        System.out.println("DEBUG: bookedByParts: " + Arrays.toString(bookedByParts));

        // Assuming the department is always the last word in bookedByParts
        String department = bookedByParts[bookedByParts.length - 1];

        // Name should be everything before the department
        String name = bookedBy.substring(0, bookedBy.lastIndexOf(" " + department)).trim();

        // Debugging: Print extracted name and department
        System.out.println("DEBUG: Extracted Name: " + name + ", Department: " + department);

        // Extracting Hall from the ListItem's Hall text
        String hall = subText.split("Hall: ")[1].trim(); // Ensure correct extraction of hall

        // Debugging: Print extracted hall
        System.out.println("DEBUG: Extracted Hall: " + hall);

        // Query Firestore to find matching bookings
        db.collection("bookings")
                .whereEqualTo("name", name)
                .whereEqualTo("department", department)
                .whereEqualTo("hall", hall)
                .whereArrayContainsAny("timeSlots", timeSlotsList) // Using arrayContainsAny for partial matches
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Loop through the results to find exact match
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                List<String> storedTimeSlots = (List<String>) documentSnapshot.get("timeSlots");

                                // Debugging: Print stored time slots
                                System.out.println("DEBUG: Stored timeSlots in document: " + storedTimeSlots);

                                if (storedTimeSlots != null && storedTimeSlots.size() == timeSlotsList.size()) {
                                    // Sort both lists for comparison
                                    List<String> sortedStoredSlots = new ArrayList<>(storedTimeSlots);
                                    List<String> sortedInputSlots = new ArrayList<>(timeSlotsList);
                                    sortedStoredSlots.sort(String::compareTo);
                                    sortedInputSlots.sort(String::compareTo);

                                    // Compare the sorted lists
                                    if (sortedStoredSlots.equals(sortedInputSlots)) {
                                        // Debugging: Print when match is found
                                        System.out.println("DEBUG: Matching booking found. Deleting document: " + documentSnapshot.getId());
                                        documentSnapshot.getReference().delete();

                                        // Remove from list and update UI
                                        listItems.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Booking deleted successfully", Toast.LENGTH_SHORT).show();
                                        return; // Exit after deleting the booking
                                    }
                                }
                            }
                            // If no matching document is found
                            Toast.makeText(context, "Booking not found", Toast.LENGTH_SHORT).show();
                            System.out.println("DEBUG: No exact match found for the time slots.");
                        } else {
                            // Query returned no results
                            Toast.makeText(context, "Booking not found", Toast.LENGTH_SHORT).show();
                            System.out.println("DEBUG: No bookings found for the given criteria.");
                        }
                    } else {
                        // Error occurred during the query
                        Toast.makeText(context, "Error deleting booking", Toast.LENGTH_SHORT).show();
                        System.out.println("DEBUG: Firestore query failed: " + task.getException());
                    }
                });
    }





    public void updateData(List<ListItem> newItems) {
        this.listItems = newItems;
        notifyDataSetChanged();
    }
}
