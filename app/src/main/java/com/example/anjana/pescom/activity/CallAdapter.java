package com.example.anjana.pescom.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.contacts.DummyContent.phoneName;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.anjana.pescom.contacts.DummyContent.phoneName} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    private final List<phoneName> mValues;
    private final CallFragment.OnListFragmentInteractionListener mListener;

    public CallAdapter(List<phoneName> items, CallFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        //holder.mContentView.setText(mValues.get(position).getNumber());
        holder.mView.setClickable(true);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // this number should exist on the server already
                    String number = holder.mItem.getNumber();
                    
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        //public final TextView mContentView;
        public phoneName mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }

    }
}
