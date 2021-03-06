package com.example.pmisi.tumblr_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Html;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


class XmlParserService {

    static XmlPullParser fetchXML(String urlString, String option, String amount) {
        try {
            XmlPullParserFactory xmlFactoryObject;
            URL url = new URL(urlString + "?type=" + option.toLowerCase() + "&num=" + amount.toLowerCase());
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
            return myParser;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
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
                            String name = myParser.getAttributeValue(null, "name");
                            StringBuilder tittle = new StringBuilder();
                            tittle.append(myParser.getAttributeValue(null, "title"));
                            myParser.next();
                            if (myParser.getEventType() == XmlPullParser.TEXT)
                                tittle.append(myParser.getText());
                            user = new User(name, tittle.toString(), new ArrayList<Content>());
                        } else if (myParser.getName().equals("post")) {
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
                                case "answer":
                                    content.setType("Question");
                                    myParser.next();
                                    parseQuestion(myParser, content);
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
                if (myParser.getName().equals("regular-body")) {
                    myParser.next();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        content.addContent(Html.fromHtml(myParser.getText(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        content.addContent(Html.fromHtml(myParser.getText()).toString());
                    }
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                } else if (myParser.getName().equals("regular-title")) {
                    myParser.next();
                    content.setTittle(myParser.getText());
                }
            }
            myParser.next();
            variable = (myParser.getEventType() == XmlPullParser.TEXT) || (!myParser.getName().equals("post"));
        }
    }

    private static void parseQuestion(XmlPullParser myParser, Content content) throws Exception {
        boolean variable = (!myParser.getName().equals("post"));
        while ((myParser.getEventType() != XmlPullParser.END_TAG) || variable) {
            if (myParser.getEventType() == XmlPullParser.START_TAG) {
                if (myParser.getName().equals("answer")) {
                    myParser.next();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        content.addContent(Html.fromHtml(myParser.getText(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        content.addContent(Html.fromHtml(myParser.getText()).toString());
                    }
                } else if (myParser.getName().equals("tag")) {
                    myParser.next();
                    content.addTag(myParser.getText());
                } else if (myParser.getName().equals("question")) {
                    myParser.next();
                    content.setTittle(myParser.getText());
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
                    StringBuilder quoteText = new StringBuilder();
                    quoteText.append('"');
                    quoteText.append(myParser.getText().replaceAll("<p>", "").replaceAll("<br/>", "\n"));
                    quoteText.append('"');
                    content.addContent(quoteText.toString());
                } else if (myParser.getName().equals("quote-source")) {
                    myParser.next();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        content.addContent(Html.fromHtml(myParser.getText(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        content.addContent(Html.fromHtml(myParser.getText()).toString());
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
                    content.addContent(myParser.getText());
                } else if (myParser.getName().equals("video-player")) {
                    if (myParser.getAttributeCount() == 0) {
                        Log.i("video-player", "parse");
                        myParser.next();
                        //content.addContent(myParser.getText());
                        Document document = Jsoup.parse(myParser.getText());
                        Element video = document.select("source").first();
                        if (video == null) {
                            video = document.select("iframe").first();
                        }
                        if(video != null) {
                            String urlValue = video.attr("src");
                            content.removePreviousElement();
                            content.addContent(urlValue);
                        }
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

    static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
