package com.example.pmisi.tumblr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
                XmlPullParser resultParser = fetchXML(url,params[1]);
                if(resultParser != null){
                    fetchedUser = parseXMLAndStoreIt(resultParser);
                    Log.i("User has: ",String.valueOf(fetchedUser.getContentList().size()));
                    for (Content content : fetchedUser.getContentList()) {
                        Log.i("tittle ",content.getTittle());
                        Log.i("Type",content.getType());
                    }
                }
                return fetchedUser.getContentList();
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
    public XmlPullParser fetchXML(String urlString,String option){
        try {
            XmlPullParserFactory xmlFactoryObject;
            URL url = new URL(urlString+"?type="+option.toLowerCase());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream stream = conn.getInputStream();
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myParser.setInput(stream, null);
            //stream.close();
            return myParser;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public User parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text;
        User user = null;
        Content content = null;
        boolean variable;
        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event){
                    case XmlPullParser.START_TAG:
                        Log.i("START_TAG",myParser.getName());
                        if(myParser.getName().equals("tumblelog")){
                            user = new User(myParser.getAttributeValue(null,"name"),
                                    myParser.getAttributeValue(null,"title"),new ArrayList<Content>());
                        }
                        if(myParser.getName().equals("post")){

                            switch (myParser.getAttributeValue(null,"type")){
                                case "photo":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Photo");
                                    //Log.i("True first",String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                   // Log.i("True Test",String.valueOf((myParser.getEventType() != XmlPullParser.END_TAG) && (!myParser.getName().equals("post"))));
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("photo-url")) {
                                                if (myParser.getAttributeValue(null, "max-width").equals("500")) {
                                                    Log.i("Photo", myParser.getPositionDescription());
                                                    myParser.next();
                                                    Log.i("TEST", myParser.getText());
                                                    content.addContent(myParser.getText());
                                                }
                                            } else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "quote":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Quote");
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("quote-text")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                String quoteText = myParser.getText();
                                                quoteText = quoteText.replaceAll("<p>", "");
                                                quoteText = quoteText.replaceAll("<br/>", "\n");
                                                content.addContent(quoteText);
                                            } else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "link":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Link");
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("link-text")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                content.setTittle(myParser.getText());
                                            } else if (myParser.getName().equals("link-url")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                content.addContent(myParser.getText());
                                            } else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "conversation":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Chat");
                                    //Log.i("True first",String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                    // Log.i("True Test",String.valueOf((myParser.getEventType() != XmlPullParser.END_TAG) && (!myParser.getName().equals("post"))));
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("conversation-title")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                content.setTittle(myParser.getText());
                                            }else if (myParser.getName().equals("conversation-text")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                if(content.getTittle().isEmpty()){
                                                    content.setTittle(myParser.getText());
                                                }
                                            }else if (myParser.getName().equals("conversation")) {
                                                variable = (!myParser.getName().equals("conversation"));
                                                while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                                    if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                                        if (myParser.getName().equals("line")) {
                                                            String chatString;
                                                            chatString = myParser.getAttributeValue(null, "label");
                                                            myParser.next();
                                                            chatString += " ";
                                                            chatString += myParser.getText();
                                                            content.addContent(chatString);
                                                        }
                                                    }
                                                    myParser.next();
                                                    variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("conversation"));
                                                }
                                            }else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "video":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Video");
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("link-text")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                content.setTittle(myParser.getText());
                                            }else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "audio":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Audio");
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("link-text")) {
                                                myParser.next();
                                                Log.i("TEST", myParser.getText());
                                                Document document = Jsoup.parse(myParser.getText());
                                                String output = document.select("embed ").first().attr("src");
                                                Log.i("output", output);
                                                content.addContent(output);
                                            }else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }else if (myParser.getName().equals("id3-artist")) {
                                                myParser.next();
                                                content.setTittle(myParser.getText());
                                            }else if (myParser.getName().equals("id3-title")) {
                                                myParser.next();
                                                content.appendToTittle(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "regular":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Text");
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("link-text")) {
                                                myParser.next();
                                                content.addContent(Html.fromHtml(myParser.getText()).toString());
                                            }else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }else if (myParser.getName().equals("id3-artist")) {
                                                myParser.next();
                                                content.setTittle(myParser.getText());
                                            } else if (myParser.getName().equals("id3-title")) {
                                                myParser.next();
                                                content.appendToTittle(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        Log.i("True first", String.valueOf(myParser.getEventType() != XmlPullParser.END_TAG));
                                        variable = (myParser.getEventType() == XmlPullParser.TEXT) || (!myParser.getName().equals("post"));
                                    }
                                    break;
                            }
                            if(user != null && content !=null)
                                user.addToContentList(content);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        Log.i("TEXT",text);
                        break;

                    case XmlPullParser.END_TAG:
                        Log.i("END_TAG",myParser.getName());
                        break;
                }
                event = myParser.next();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
