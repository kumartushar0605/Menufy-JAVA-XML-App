package com.example.canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.canteen.Adapters.TableAdapter;
import com.example.canteen.Models.TableData;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.example.canteen.LoginActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class MainActivity extends AppCompatActivity implements TableAdapter.OnTableClickListener {

    private RecyclerView tableRecyclerView;
    private ArrayList<TableData> tableDataList;
    private TableAdapter adapter;
    private ImageView imageView;
    private TextView textView;
    private MaterialSearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        tableRecyclerView = findViewById(R.id.tableRecyclerView);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.restroName);
        searchBar=findViewById(R.id.searchBar);
        ImageButton threeDotButton = findViewById(R.id.btn_three_dot_menu);

        threeDotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        // Set layout manager for RecyclerView
        tableRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        // Initialize table data list
        tableDataList = new ArrayList<>();

        // Retrieve the intent and get the extras
//        Intent intent = getIntent();
//        if (intent != null) {
//            String resName = intent.getStringExtra("resName");
//            String resImage = intent.getStringExtra("resImage");
//
//            // Log received values
//            Log.d("MainActivity", "Received resName: " + resName + ", resImage: " + resImage);
//
//            // Set restaurant name and image if not null
//            if (resName != null && resImage != null) {
//                textView.setText(resName);
//                Glide.with(this).load(resImage).into(imageView);
//            } else {
//                Log.e("MainActivity", "Received null values for resName or resImage");
//            }
//        } else {
//            Log.e("MainActivity", "Intent is null");
//        }


        SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
       String resName = sharedPref.getString("RES_NAME", "");
      String  resImage = sharedPref.getString("RES_IMAGE", "");

   Log.d("MainActivity",resImage + "   " +resName+" hiiiiiiiiiiiii");
   textView.setText(resName);
   Glide.with(this).load(resImage).into(imageView);

        // Fetch table data from server
        String url = "http://10.0.2.2:5000/tables"; // Use 10.0.2.2 for Android emulator

        fetchTableData(url);

        // Set adapter for RecyclerView
        adapter = new TableAdapter(this, tableDataList, this);
        tableRecyclerView.setAdapter(adapter);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Optional: handle search state changes if needed
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // Handle the search input and scroll to the position
                try {
                    String TableNo = text.toString();

                    int position = findTablePosition(TableNo);
                    Log.d("MainActivity","Positionnnnnnnnnn"+position);
                    if (position != -1) {
                        if (tableDataList.size() > 0) {

                                // Use smoothScrollToPosition for smooth scrolling
                                tableRecyclerView.smoothScrollToPosition(position);

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Table not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("MainActivity", "Invalid input for table number", e);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                // Optional: handle button clicks if needed
            }
        });
    }
    private int findTablePosition(String tableNumber) {
        for (int i = 0; i < tableDataList.size(); i++) {
            String tablePosition = String.valueOf(tableDataList.get(i).getTable());
            if (tablePosition.equals(tableNumber)) {
                return i;
            }
        }
        return -1;
    }

    private void fetchTableData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("dataa");
                            for (int i = 0; i < dataArray.length(); i++) {
                                Gson gson = new Gson();
                                TableData table = gson.fromJson(dataArray.getJSONObject(i).toString(), TableData.class);
                                tableDataList.add(table);
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onTableClick(int position) {
        if (position < tableDataList.size()) {
            showPopup(tableRecyclerView, tableDataList.get(position));
        } else {
            Toast.makeText(MainActivity.this, "Error: Table data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopup(View anchorView, TableData tableData) {
        Log.d("MainActivity", "showPopup called");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Log.e("MainActivity", "LayoutInflater is null");
            return;
        }

        View popupView = inflater.inflate(R.layout.popup_layout, null);
        if (popupView == null) {
            Log.e("MainActivity", "popupView is null");
            return;
        }

        final PopupWindow popupWindow = new PopupWindow(popupView,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setFocusable(true);

        TextView popupText = popupView.findViewById(R.id.popupText);
        if (popupText == null) {
            Log.e("MainActivity", "popupText is null");
            return;
        }

        String popupContent = "Table Number: " + tableData.getTable() + "\nFood Items: " + tableData.getData() +
                "\nTotal Price: " + tableData.getTotalPrice() + "\nPayment Mode: " + tableData.getPayment();
        popupText.setText(popupContent);
        Log.d("MainActivity", "Popup content set: " + popupContent);

        Button closeButton = popupView.findViewById(R.id.closeButton);
        Button doneButton = popupView.findViewById(R.id.doneButton);

        if (closeButton == null || doneButton == null) {
            Log.e("MainActivity", "CloseButton or DoneButton is null");
            return;
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTableData(tableData.getId());
                tableDataList.remove(tableData);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    private void deleteTableData(String id) {
        String url = "http://10.0.2.2:5000/tables/" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response
                        Toast.makeText(MainActivity.this, "Table data deleted", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error deleting data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // Handle logout action here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return handleMenuItemClick(item);
            }
        });
        popup.show();
    }

    private boolean handleMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // Handle logout action
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity","Loggggggout");

                SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("IS_LOGGED_IN", false);
                editor.remove("TOKEN");
                editor.remove("RES_IMAGE");
                editor.remove("RES_NAME");
                editor.apply();

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
                return true;
            default:
                return false;
        }
    }
}
