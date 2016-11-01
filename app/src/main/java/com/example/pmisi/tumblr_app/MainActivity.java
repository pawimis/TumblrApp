package com.example.pmisi.tumblr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends AppCompatActivity  {

    final int seekBarMin = 20;
    EditText usernameEditText;
    Button sendButton;
    Spinner typeSpinner;
    SeekBar seekBar;
    TextView amountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = (EditText) findViewById(R.id.main_activity_editText_username);
        sendButton = (Button) findViewById(R.id.main_activity_button_send);
        typeSpinner = (Spinner) findViewById(R.id.main_activity_spinner);
        amountTextView = (TextView) findViewById(R.id.main_activity_amountLabel);
        amountTextView.setText(String.valueOf(seekBarMin));

        seekBar = (SeekBar) findViewById(R.id.main_activity_seekBar);
        seekBar.setMax(30);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amountTextView.setText(String.valueOf(progress + seekBarMin));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (XmlParserService.isNetworkAvailable(getApplicationContext()))
                    serviceUi();
                else
                    setupAlertDialog("no internet connection");
            }
        });
    }

    private void serviceUi() {
        String username = usernameEditText.getText().toString();
        String spinnerValue = String.valueOf(typeSpinner.getSelectedItem());
        Log.i("Spinner", spinnerValue);
        if (username.length() > 0) {
            fetchUserData(username, spinnerValue, amountTextView.getText().toString());
        } else {
            setupAlertDialog("Give username");
        }
    }

    private void fetchUserData(String username, String option, String value) {
        class FetchUserDataTask extends AsyncTask<String, Void, User> {
            private ProgressDialog loading;
            @Override
            protected User doInBackground(String... params) {
                User fetchedUser = null;
                String url = "http://"+params[0]+".tumblr.com/api/read";
                XmlPullParser resultParser = XmlParserService.fetchXML(url, params[1], params[2]);
                if(resultParser != null){
                    fetchedUser = XmlParserService.parseXMLAndStoreIt(resultParser);
                    Log.i("User has: ",String.valueOf(fetchedUser.getContentList().size()));
                    for (Content content : fetchedUser.getContentList()) {
                        Log.i("tittle ",content.getTittle());
                        Log.i("Type",content.getType());
                    }
                }
                if (fetchedUser != null)
                    return fetchedUser;
                else
                    return null;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Fetch","Fetching data",true,true);
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                loading.dismiss();
                if (user != null) {
                    if (user.getContentList() != null) {
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putParcelableArrayListExtra("Content", user.getContentList());
                        intent.putExtra("UserName", user.getName());
                        intent.putExtra("UserTittle", user.getTitle());
                        startActivity(intent);
                    } else
                        setupAlertDialog("nothing to show");
                } else
                    setupAlertDialog("user does not exists");
            }
        }
        FetchUserDataTask task = new FetchUserDataTask();
        task.execute(username, option, value);
    }

    private void setupAlertDialog(String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
