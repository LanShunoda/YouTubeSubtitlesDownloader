package com.plorial.youtubesubtitlesdownloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by plorial on 9/8/16.
 */
public class TextSearcher {

    private static final String dirPath = "/home/plorial/Documents/YouTubeSubs/EEOneGay/subs/";
    private static final String timeStampPattern = "([\\d:,]+)";

    static Pattern pattern;

    private static File[] files;
    private static List<String> textWords;
    private static List<Result> result = new ArrayList<>();

    private static final String TEXT = "ног";

    public static void main(String[] args) {
        File folder = new File(dirPath);
        if(folder.isDirectory()){
            files = folder.listFiles();
        } else {
            System.err.println("file is not directory");
            System.exit(1);
        }


        pattern = Pattern.compile(timeStampPattern);
        textWords = new ArrayList<>();
        textWords.add(TEXT);
//        textWords =  getWords(TEXT);
        searchInFiles();
    }

    private static void searchInFiles() {
        for (String word: textWords) {
            Result r = new Result(word);
            TreeMap<String, Map<String, String>> links = new TreeMap<>();
            r.setLinks(links);
            for (int i = 0; i < files.length - 1; i++){
                Map<String, String> rowNums = new TreeMap();

                List<String> fileRows = null;
                try {
                    fileRows = Files.readAllLines(Paths.get(files[i].getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < fileRows.size() - 1; j ++) {
                    if(fileRows.get(j).toLowerCase().contains(word.toLowerCase())){
                        String time = "";
                        for(int k = 1; k < 3; k++){
                            Matcher matcher = pattern.matcher(fileRows.get(j - k));
                            if(matcher.find()){
                                time = matcher.group();
                                break;
                            }
                        }
                        rowNums.put(j + " " + fileRows.get(j).replace(TEXT, "\u001B[31m" + TEXT  + "\u001B[0m"), time);
                    }
                }
                if(rowNums.size() > 0){
                    links.put(files[i].getName(),rowNums);
                }
            }
            System.out.println(r.toString());
            result.add(r);
        }
//        writeToFile();
    }

    private static void writeToFile() {
        FileWriter writer = null;
        try {
            File out = new File("/home/plorial/Documents/YouTubeSubs/EEOneGay/output.txt");
            if(!out.exists()){
                out.createNewFile();
            }
            writer = new FileWriter(out);
            for(Result r: result) {
                writer.write(r.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> getWords(String text) {
        List<String> words = new ArrayList<String>();
        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                words.add(text.substring(firstIndex, lastIndex));
            }
        }

        return words;
    }
}
