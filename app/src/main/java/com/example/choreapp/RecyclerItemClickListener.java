/*
  RecyclerItemClickListener.java
  ------------------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    /**
     *
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    /**
     *
     * @param context
     * @param recyclerView
     * @param listener
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    /**
     *
     * @param view
     * @param e
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    /**
     *
     * @param view
     * @param motionEvent
     */
    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        //Empty
    }

    /**
     *
     * @param disallowIntercept
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){
        //Empty
    }
}