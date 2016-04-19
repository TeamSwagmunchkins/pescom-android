package com.example.anjana.pescom.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.activity.fragment.CallFragment;
import com.example.anjana.pescom.activity.fragment.ChatFragment;
import com.example.anjana.pescom.contacts.RegisteredContacts;
import com.example.anjana.pescom.util.Preferences;
import com.example.anjana.pescom.util.RequestHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactsTabActivity extends AppCompatActivity {

    final HashSet<RegisteredContacts.Contact> clist = new HashSet<>();
    ArrayList<RegisteredContacts.Contact> list1 = new ArrayList<>();
    Set<String> numbers = new HashSet<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_call);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("PESCom");
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            Thread makeList= new TaskGetRegisteredContacts();

            makeList.start();
            try {
                makeList.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (position) {
                case 1:
                    return new ChatFragment().newInstance(1, list1);
                //break;
                case 0:
                    return new CallFragment().newInstance(1,list1);
            }
            throw new IllegalArgumentException("No tab with position " + position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CALL";
                case 1:
                    return "CHAT";
            }
            return null;
        }
    }


    private class TaskGetRegisteredContacts extends Thread {
        @Override
        public void run()
        {
            try {
                Log.i("EventContact:", "INTASK");

                Preferences preferences = Preferences.getPreferences(getParent());
                String token = preferences.getToken();
                RequestHelper.RequestResult response=RequestHelper.getRegisteredUsers(token,getApplicationContext());
                JSONArray json = new JSONArray(response.RESPONSE_BODY);
                for (int i = 0; i < json.length(); i++) {
                    numbers.add(json.getJSONObject(i).getString("phone_number"));

                }
                preferences.setRegisteredNumbers(numbers);

                setNumbers();

            } catch (Exception E) {

                E.printStackTrace();

            }

        }

        private boolean checkNumber(String phoneNumber) {
            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            return numbers.contains(phoneNumber);
        }


        public void setNumbers()
        {
            String name, phoneNumber;
            String[] proj = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            };
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    proj,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            assert cursor != null;
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                if (phoneNumber != null && checkNumber(phoneNumber))
                    clist.add(new RegisteredContacts.Contact(name, phoneNumber));
            }
            cursor.close();



            for (RegisteredContacts.Contact p : clist) {
                list1.add(p);

            }

        }

    }


}





