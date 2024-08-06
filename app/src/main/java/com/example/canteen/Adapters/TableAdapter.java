package com.example.canteen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.Models.TableData;
import com.example.canteen.R;
import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TableData> tableDataList;
    private OnTableClickListener onTableClickListener;

    public TableAdapter(Context context, ArrayList<TableData> tableDataList, OnTableClickListener onTableClickListener) {
        this.context = context;
        this.tableDataList = tableDataList;
        this.onTableClickListener = onTableClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_item, parent, false);
        return new ViewHolder(view, onTableClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableData tableData = tableDataList.get(position);
        holder.tableTextView.setText("T -  " + tableData.getTable());
    }

    @Override
    public int getItemCount() {
        return tableDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tableTextView;
        OnTableClickListener onTableClickListener;

        public ViewHolder(@NonNull View itemView, OnTableClickListener onTableClickListener) {
            super(itemView);
            tableTextView = itemView.findViewById(R.id.tableTextView);
            this.onTableClickListener = onTableClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTableClickListener.onTableClick(getAdapterPosition());
        }
    }

    public interface OnTableClickListener {
        void onTableClick(int position);
    }
}
