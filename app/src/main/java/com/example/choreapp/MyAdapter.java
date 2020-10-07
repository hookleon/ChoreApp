/*
  MyAdapter.java
  --------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Creates an adapter to display names of users when adding names to the household.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Member> mDataset;
    private Context mContext;
    /**
     * Provides a reference to the views for each data item
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView edit;
        public TextView remove;
        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            edit = (TextView) v.findViewById(R.id.edit);
            remove = (TextView) v.findViewById(R.id.remove);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of dataset)
     * @param myDataset
     */
    public MyAdapter(List<Member> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Member m = mDataset.get(position);

        // Name of member
        holder.name.setText(m.getName());

        // Edit name
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final EditText editName = new EditText(mContext);
                builder.setTitle("Edit Name: " + mDataset.get(position).getName());
                builder.setView(editName);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataset.get(position).setName(editName.getText().toString());
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing
                    }
                });
                builder.show();
            }
        });

        // Remove Name
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mDataset.size());
            }
        });
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return the size of mDataset
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}