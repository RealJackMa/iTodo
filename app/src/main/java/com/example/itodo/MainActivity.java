package com.example.itodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button addButton;
    EditText editTextItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        editTextItem = findViewById(R.id.editTextItem);
        rvItems = findViewById(R.id.rvItems);
         
        loadItems();
//        items.add("Submit CodePath Application.");
//        items.add("Submit CodePath Prework.");

        // Delete Listener.
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {

                // Delete the item from the model.
                items.remove(position);
                // Notify the adapater at which position we deleted the item.
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item Deleted!", Toast.LENGTH_SHORT).show();

                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single Clicked at " + position + "!");

                // Create the new activity.
                Intent i =  new Intent(MainActivity.this, EditActivity.class);

                // Pass the data being edited.
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                // Display the activity.
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        // Add Listener.
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = editTextItem.getText().toString();

                // Add the item to the model.
                items.add(todoItem);

                // Notify the adapter that an item has been added.
                itemsAdapter.notifyItemInserted(items.size()-1);

                // Clear editText input.
                editTextItem.setText("");
                Toast.makeText(getApplicationContext(), "Item Added!", Toast.LENGTH_SHORT).show();

                saveItems();
            }
        });
    }

    // Handle the result of the edit activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {

            // Retrieve the updated text value.
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            // Extract the position of the edited item from the position key.
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the right position with the new item.
            items.set(position, itemText);

            // Notify the adapter.
             itemsAdapter.notifyItemChanged(position);

            // Persist the changes.
            saveItems();

            // Toast.
            Toast.makeText(getApplicationContext(), "Item Updated!",  Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult!");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    // This function will load files by reading every line of the data file.
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items!", e);
            items = new ArrayList<>();
        }
    }

    // This function will save items by writing into the data file.
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items!", e);
        }
    }

}