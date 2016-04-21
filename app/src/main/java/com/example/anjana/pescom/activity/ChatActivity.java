package com.example.anjana.pescom.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.activity.adapter.MessageAdapter;
import com.example.anjana.pescom.util.RequestHelper;
import com.example.anjana.pescom.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_NUMBER = "number";

    public static ChatActivity sChatActivity;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private MessageAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    protected String messageText;
    protected ProgressDialog progressDialog;

    private String mContactName;
    private String mContactNumber;

    JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sChatActivity = this;
        setContentView(R.layout.activity_chat);
        mContactName = getIntent().getStringExtra(EXTRA_NAME);
        mContactNumber = getIntent().getStringExtra(EXTRA_NUMBER);
        getSupportActionBar().setTitle(mContactName);    //Set it to appropriate contact name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Enable back button on Action bar
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sChatActivity=this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sChatActivity = null;
    }

    public void onMessagesPending() {
        // TODO: SOMZ, this is if a message comes for this number WHEN the user is already in chat
        // we need to display the message without
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);

        loadHistory();     //Load dummy sender-side messages on startup. Hardcoded.

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageText = messageET.getText().toString();
                Log.i(TAG, "SendButtonClicked");
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                send_message();
            }
        });
    }

    public String send_message() {
        Log.i("FUNC", "sendRequest_phone");
        try {
            progressDialog = new ProgressDialog(ChatActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setMessage("Sending..");
            progressDialog.show();
            new MyTask().execute(messageText);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return null;
    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        private String error_str="";

        protected Boolean doInBackground(String... urls) {
            Log.d(TAG, "SendingMSG");
            String token = Preferences.getPreferences(ChatActivity.this).getToken();
            String fromNo = Preferences.getPreferences(ChatActivity.this).getNumber();
            try {
                RequestHelper.RequestResult result = RequestHelper.sendMessage(fromNo,
                        mContactNumber, token, urls[0], ChatActivity.this);
                switch (result.RESPONSE_CODE) {
                    case 200:
                        Log.d(TAG, "MessageDelivered");
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("message", urls[0]);
                            jsonObject.put("to_phone_number",mContactName);
                            jsonObject.put("from_phone_number",fromNo);
                            jsonObject.put("me",true);
                        }catch(JSONException e){
                            Log.e(TAG, "JSON parsing failed for addM: "
                                    + jsonObject, e);
                            e.printStackTrace();
                        }
                        Preferences.getPreferences(ChatActivity.this).addMessageFor(mContactNumber, jsonObject);

                        break;
                    case 404:
                        Toast.makeText(ChatActivity.this,
                                "User Not Found",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (IOException e) {
                Log.e(TAG, "IOError", e);
                error_str="Message not Delivered. Try Again";
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            progressDialog.dismiss();
            if (s) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                displayMessage(chatMessage);
                messageSentSuccess();
            } else {
                Log.i(TAG, "elsePostExecute failed");
                messageSentFailed(error_str);
            }
        }
    }

    public void messageSentSuccess() {
        messageET.setText("");
        Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_LONG).show();
    }

    public void messageSentFailed(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadHistory(){    //A Dummy method to simulate initial sender-side messages
        adapter = new MessageAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        jsonArray = Preferences.getPreferences(ChatActivity.this).getMessagesFor(mContactNumber);
        String msg;
        JSONObject json_data;
        ChatMessage chatMessage;
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                json_data = jsonArray.getJSONObject(i);
                msg = json_data.getString("message");
                chatMessage = new ChatMessage();
                chatMessage.setMessage(msg);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                if(json_data.getBoolean("me") == true)
                    chatMessage.setMe(true);
                else
                    chatMessage.setMe(false);
                displayMessage(chatMessage);
            }catch(JSONException e){
                Log.e(TAG, "JSON parsing failed for getM: "
                        + jsonArray, e);
                e.printStackTrace();
            }
        }
    }
}
