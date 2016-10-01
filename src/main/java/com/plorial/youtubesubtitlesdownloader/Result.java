package com.plorial.youtubesubtitlesdownloader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by plorial on 9/8/16.
 */
public class Result {
    private String word;
    private TreeMap<String, Map<String, String>> links;

    public Result(String word) {
        this.word = word;
    }

    public void setLinks(TreeMap<String, Map<String, String>> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(word);
        builder.append("\n");
        for (String fileName : links.keySet()) {
            builder.append(fileName);
            builder.append(": ");
            builder.append("https://youtube.com/watch?v=" + fileName.substring(0,fileName.length()-9));
            builder.append("\n");
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss,SSS");

            for (Map.Entry<String,String> entry : links.get(fileName).entrySet()) {
                Date time = null;
                try {
                    time = dateFormat.parse(entry.getValue());
                } catch (ParseException e) {}
                if(time != null) {
                    builder.append(entry.getValue() + " - "
                            + "https://youtu.be/" + fileName.substring(0, fileName.length() - 9) +
                            "?t=" + time.getHours() + "h" + time.getMinutes() + "m" + time.getSeconds() + "s" + "  " +
                            entry.getKey());
                } else {
                    builder.append(entry.getValue() + " - " + entry.getKey());
                }
                builder.append("\n");
            }
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }
}
