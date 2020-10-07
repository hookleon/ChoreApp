/*
  AddChoreAdapter.java
  --------------------
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
 * An adapter to display the names of users when adding names to the household
 */
public class AddChoreAdapter extends RecyclerView.Adapter<AddChoreAdapter.MyViewHolder> {
    private List<String> mDataset;
    private Context mContext;

    /**
     * Provides a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView chore;
        public TextView edit;
        public TextView remove;

        public MyViewHolder(View v) {
            super(v);
            chore = (TextView) v.findViewById(R.id.name);
            edit = (TextView) v.findViewById(R.id.edit);
            remove = (TextView) v.findViewById(R.id.remove);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of dataset)
     * @param myDataset
     * */
    public AddChoreAdapter(List<String> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }

    /**
     * Construct a new view (invoked by the layout manager)
     * @param parent ViewGroup object
     * @param viewType
     * @return a new MyViewHolder object
     * */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder MyViewHolder object
     * @param position index for mDataset
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String m = mDataset.get(position);
        // Chore name
        holder.chore.setText(m);

        // Edit Chore
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final EditText editChore = new EditText(mContext);
                builder.setTitle("Edit Name: " + mDataset.get(position));
                builder.setView(editChore);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataset.set(position, editChore.getText().toString());
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

        // Remove Chore
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String itemLabel = mDataset.get(position).getName();
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mDataset.size());
            }
        });
    }

    /**
     * Returns the size of your dataset (invoked by the layout manager)
     * @return The size of the dataset
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}