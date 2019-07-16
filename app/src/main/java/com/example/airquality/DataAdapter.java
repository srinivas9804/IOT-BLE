/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     Recycler View adapter to display data stored in the Room Database.
 *
 */
package com.example.airquality;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private static List<String> list;
    private static Context context;

    public DataAdapter(List<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mDataCell;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mDataCell = (TextView) view.findViewById(R.id.dataCell);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            System.out.println(pos);
        }
    }

    @Override
    public DataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_table_view, parent, false);
        DataAdapter.MyViewHolder vh = new DataAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DataAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mDataCell.setText(list.get(position));
        if(position <= 9){
            holder.mDataCell.setTypeface(null, Typeface.BOLD);
            holder.mDataCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
