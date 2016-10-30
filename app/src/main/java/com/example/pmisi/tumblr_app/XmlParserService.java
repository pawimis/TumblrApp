package com.example.pmisi.tumblr_app;

import android.text.Html;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


class XmlParserService {

    static XmlPullParser fetchXML(String urlString, String option) {
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

    ///Todo User nie potrzebny
    static User parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        User user = null;
        Content content;
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
                            content = new Content(myParser.getAttributeValue(null, "url"), myParser.getAttributeValue(null, "date"),
                                    myParser.getAttributeValue(null, "slug").replace('-', ' '));
                            switch (myParser.getAttributeValue(null,"type")){
                                case "photo":
                                    content.setType("Photo");
                                    myParser.next();
                                    parsePhoto(myParser, content);
                                    break;
                                case "quote":
                                    content.setType("Quote");
                                    myParser.next();
                                    parseQuote(myParser, content);
                                    break;
                                case "link":
                                    content.setType("Link");
                                    myParser.next();
                                    parseLink(myParser, content);
                                    break;
                                case "conversation":
                                    content.setType("Chat");
                                    myParser.next();
                                    parseChat(myParser, content);
                                    break;
                                case "video":
                                    content.setType("Video");
                                    myParser.next();
                                    parseVideo(myParser, content);
                                    break;
                                case "audio":
                                    content.setType("Audio");
                                    myParser.next();
                                    parseAudio(myParser, content);
                                    break;
                                case "regular":
                                    content.setType("Text");
                                    myParser.next();
                                    parseText(myParser, content);
                                    break;
                            }
                            Log.i("CheckUser", user == null ? "Null" : "not Null");
                            if (user != null)
                                user.addToContentList(content);
                        }
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

    private static void parseText(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
            if (myParser.getEventType() == XmlPullParser.START_TAG) {
                if (myParser.getName().equals("link-text")) {
                    myParser.next();
                    content.addContent(Html.fromHtml(myParser.getText()).toString());
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                } else if (myParser.getName().equals("id3-artist")) {
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
    }

    private static void parseAudio(XmlPullParser myParser, Content content) throws Exception {
        Log.i("ParseAudio", "parse");
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
            Log.i("ParseAudio", "while");
            if (myParser.getEventType() == XmlPullParser.START_TAG) {
                if (myParser.getName().equals("audio-embed")) {
                    myParser.next();
                    //Document document = Jsoup.parse(myParser.getText());
                    //String output = document.select("iframe").first().attr("src");
                    content.addContent(myParser.getText());
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                } else if (myParser.getName().equals("id3-artist")) {
                    myParser.next();
                    content.setTittle(myParser.getText());
                } else if (myParser.getName().equals("id3-album")) {
                    myParser.next();
                    content.appendToTittle("-(" + myParser.getText() + ")");
                } else if (myParser.getName().equals("id3-title")) {
                    myParser.next();
                    content.appendToTittle("-" + myParser.getText());
                }
            }
            myParser.next();
            variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
        }
    }

    private static void parsePhoto(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
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
    }

    private static void parseQuote(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
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
    }

    private static void parseLink(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
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
    }

    private static void parseChat(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
            if (myParser.getEventType() == XmlPullParser.START_TAG) {
                if (myParser.getName().equals("conversation-title")) {
                    myParser.next();
                    content.setTittle(myParser.getText());
                } else if (myParser.getName().equals("conversation-text")) {
                    myParser.next();
                    if (content.getTittle().isEmpty()) {
                        content.setTittle(myParser.getText());
                    }
                } else if (myParser.getName().equals("conversation")) {
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
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                }
            }
            myParser.next();
            variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
        }
    }

    private static void parseVideo(XmlPullParser myParser, Content content) throws Exception {
        Log.i("ParseVideo", "parse");
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
            if (myParser.getEventType() == XmlPullParser.START_TAG) {
                if (myParser.getName().equals("video-source")) {
                    myParser.next();
                    //content.addContent(myParser.getText());
                } else if (myParser.getName().equals("video-player")) {
                    if (myParser.getAttributeCount() == 0) {
                        Log.i("video-player", "parse");
                        myParser.next();
                        content.addContent(myParser.getText());
                        /*Document document = Jsoup.parse(myParser.getText());
                        Element video = document.select("source").first();
                        if (video == null) {
                            video = document.select("iframe").first();
                        }
                        if(video != null) {
                            String urlValue = video.attr("src");
                            content.removePreviousElement();
                            content.addContent(urlValue);
                        }*/
                    }
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                }
            }
            myParser.next();
            variable = myParser.getEventType() == XmlPullParser.TEXT || (!myParser.getName().equals("post"));
        }
    }
}
