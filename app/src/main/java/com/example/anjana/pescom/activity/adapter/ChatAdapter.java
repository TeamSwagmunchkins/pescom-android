package com.example.anjana.pescom.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.activity.fragment.ChatFragment;
import com.example.anjana.pescom.contacts.RegisteredContacts;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RegisteredContacts.Contact} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<RegisteredContacts.Contact> mValues;
    private final ChatFragment.OnListFragmentInteractionListener mListener;

    public ChatAdapter(List<RegisteredContacts.Contact> items, ChatFragment.OnListFragmentInteractionListener listener) {

        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_chat, parent, false);

        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "The Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });

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
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
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
        public RegisteredContacts.Contact mItem;

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
        /*public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }*/
    }
}
