package com.example.pmisi.tumblr_app;

import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by pmisi on 28.10.2016.
 */

public class XmlParserService {

    public static  XmlPullParser fetchXML(String urlString, String option){
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
            stream.close();
            return myParser;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static User parseXMLAndStoreIt(XmlPullParser myParser) {
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
                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("photo-url")) {
                                                if (myParser.getAttributeValue(null, "max-width").equals("500")) {
                                                    myParser.next();
                                                    content.addContent(myParser.getText());
                                                }
                                            } else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
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
                                                content.setTittle(myParser.getText());
                                            } else if (myParser.getName().equals("link-url")) {
                                                myParser.next();
                                                content.addContent(myParser.getText());
                                            } else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
                                        variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
                                    }
                                    break;
                                case "conversation":
                                    content = new Content(myParser.getAttributeValue(null,"url"),myParser.getAttributeValue(null,"date"),
                                            myParser.getAttributeValue(null,"slug").replace('-',' '));
                                    content.setType("Chat");

                                    myParser.next();
                                    variable = (!myParser.getName().equals("post"));
                                    while((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
                                        if (myParser.getEventType() == XmlPullParser.START_TAG) {
                                            if (myParser.getName().equals("conversation-title")) {
                                                myParser.next();
                                                content.setTittle(myParser.getText());
                                            }else if (myParser.getName().equals("conversation-text")) {
                                                myParser.next();
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
                                                content.setTittle(myParser.getText());
                                            }else if (myParser.getName().equals("tag")) {
                                                myParser.next();
                                                content.addTag(myParser.getText());
                                            }
                                        }
                                        myParser.next();
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
                                                Document document = Jsoup.parse(myParser.getText());
                                                String output = document.select("embed ").first().attr("src");
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
                        break;

                    case XmlPullParser.END_TAG:
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
