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
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    EditText usernameEditText;
    Button sendButton;
    Spinner typeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = (EditText) findViewById(R.id.main_activity_edit_text_username);
        sendButton = (Button) findViewById(R.id.main_activity_button_send);
        typeSpinner = (Spinner) findViewById(R.id.main_activity_spinner);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String spinnerValue = String.valueOf(typeSpinner.getSelectedItem());
                Log.i("Spinner",spinnerValue);
                if(username.length() > 0){
                    fetchUserData(username,spinnerValue);
                }else{
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                    dlgAlert.setMessage("This is an alert with no consequence");
                    dlgAlert.setTitle("App Title");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int which) {}});
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            }
        });
    }
    private void fetchUserData(String username,String option){
        class FetchUserDataTask extends AsyncTask<String,Void,ArrayList<Content>>{
            private ProgressDialog loading;
            @Override
            protected ArrayList<Content> doInBackground(String... params) {
                User fetchedUser = null;
                String url = "http://"+params[0]+".tumblr.com/api/read";
                XmlPullParser resultParser = XmlParserService.fetchXML(url, params[1]);
                if(resultParser != null){
                    fetchedUser = XmlParserService.parseXMLAndStoreIt(resultParser);
                    Log.i("User has: ",String.valueOf(fetchedUser.getContentList().size()));
                    for (Content content : fetchedUser.getContentList()) {
                        Log.i("tittle ",content.getTittle());
                        Log.i("Type",content.getType());
                    }
                }
                if (fetchedUser != null)
                    return fetchedUser.getContentList();
                else
                    return null;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Fetch","Fetching data",true,true);
            }

            @Override
            protected void onPostExecute(ArrayList<Content> content) {
                super.onPostExecute(content);
                loading.dismiss();
                if(content != null){
                    Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                    intent.putParcelableArrayListExtra("Content",content);
                    startActivity(intent);
                }

            }
        }
        FetchUserDataTask task = new FetchUserDataTask();
        task.execute(username,option);
    }
}
