package com.example.canteen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class popup extends DialogFragment {

    int table;
    int total;
    String data;
    TextView Table, Data, Total;
    Button done;
    ImageView close;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            table = bundle.getInt("Table", 0);
            total = bundle.getInt("Total", 0);
            data = bundle.getString("Data");
        }

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popupbox, null);

        Table = view.findViewById(R.id.table_no);
        Data = view.findViewById(R.id.dataBox);
        Total = view.findViewById(R.id.subtotal);
        done = view.findViewById(R.id.dialog_button);
        close = view.findViewById(R.id.close);

        Table.setText("Table-" + table);
        Data.setText(data);
        Total.setText("INR " + total);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle done button click
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Close button clicked", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
