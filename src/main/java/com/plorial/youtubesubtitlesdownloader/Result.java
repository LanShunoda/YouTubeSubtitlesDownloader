package com.plorial.youtubesubtitlesdownloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Created by plorial on 9/8/16.
 */
public class Result {
    private String word;
    private TreeMap<String, ArrayList<Integer>> links;

    public Result(String word) {
        this.word = word;
    }

    public void setLinks(TreeMap<String, ArrayList<Integer>> links) {
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
            builder.append(Arrays.toString(links.get(fileName).toArray()));
            builder.append("\n");
        }
        return builder.toString();
    }
}
