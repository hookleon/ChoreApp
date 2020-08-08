/*
  ListAdapter.java
  ----------------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * The adapter allows the app to display names of users when adding names to the household
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private List<Member> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView chores;

        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            chores = (TextView) v.findViewById(R.id.chores);
        }
    }

    /**
     * Creates instance object that depends on the kind of dataset
     * @param myDataset
     */
    public ListAdapter(List<Member> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Creates new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_list_row, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Member m = mDataset.get(position);
        holder.name.setText(m.getName());
        List<String> chores = m.getChores();
        String choresText = "";
        for(int i = 0; i < chores.size(); i++){
            //add coma if it has more chores
            if(i == chores.size() - 1){
                choresText += chores.get(i);
            } else {
                choresText += chores.get(i) + ", ";
            }
        }
        holder.chores.setText(choresText);
    }

    /**
     * Returns the size of your dataset (invoked by the layout manager)
     * @return size of the mDataset
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}