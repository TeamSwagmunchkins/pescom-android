package com.example.anjana.pescom.activity.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.activity.adapter.CallAdapter;
import com.example.anjana.pescom.contacts.RegisteredContacts;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CallFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener
            = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(RegisteredContacts.Contact item) {
            String number = item.getNumber();
            Toast.makeText(getActivity(), "" + number, Toast.LENGTH_SHORT).show();
        }
    };

    public HashSet<RegisteredContacts.Contact> clist = new HashSet<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CallFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CallFragment newInstance(int columnCount) {
        CallFragment fragment = new CallFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        //view.setOnI;
        // Set the adapter

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            Thread makeList = new Thread(new Runnable() {
                @Override
                public void run() {

                    String name, phoneNumber;

                    String[] proj = new String[]{
                            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    };
                    Cursor cursor = getContext().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, proj, null, null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    //Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                    while (cursor.moveToNext()) {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                        System.out.println(name + "  " + phoneNumber);
                        if (phoneNumber != null)
                            clist.add(new RegisteredContacts.Contact(name, phoneNumber));
                    }

                    cursor.close();

                }
            });
            makeList.start();

            try {
                makeList.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<RegisteredContacts.Contact> list1 = new ArrayList<>();
            for (RegisteredContacts.Contact p : clist)
                list1.add(p);
            RegisteredContacts list = new RegisteredContacts(list1);
            recyclerView.setAdapter(new CallAdapter(list.ITEMS, mListener));
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(RegisteredContacts.Contact item);
    }
}


