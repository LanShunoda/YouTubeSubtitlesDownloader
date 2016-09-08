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
import java.util.TreeMap;

/**
 * Created by plorial on 9/8/16.
 */
public class TextSearcher {

    private static final String dirPath = "/home/plorial/Documents/YouTubeSubs/Sokol";
    private static File[] files;

    private static List<String> textWords;

    private static List<Result> result = new ArrayList<>();

    private static final String TEXT = "Услыхав судьбы призыв\n" +
            "Не трусь вперёд иди\n" +
            "Ты не слушай шопот свой\n" +
            "И победишь лишь ты\n" +
            "\n" +
            "В путь смелее пробил час\n" +
            "И недруг не помеха нам \n" +
            "Видь победный светит знак\n" +
            "Воплоти наш план\n" +
            "\n" +
            "Покемон Мы сможим всё\n" +
            "Ты и я Я знаю судьба моя\n" +
            "Покемон\n" +
            "Оу Мой лучший друг\n" +
            "Нас узнают все вокруг\n" +
            "Покемон Мы сможем всё\n" +
            "Бой зовëт Не бойся иди вперëд\n" +
            "\n" +
            "Смело в бoй, Друзья с toboй!\n" +
            "ПОКЕМОН\n" +
            "Всех их cоберëм";

    public static void main(String[] args) {
        File folder = new File(dirPath);
        if(folder.isDirectory()){
            files = folder.listFiles();
        } else {
            System.err.println("file is not directory");
            System.exit(1);
        }
        textWords =  getWords(TEXT);
        searchInFiles();
    }

    private static void searchInFiles() {
        for (String word: textWords) {
            Result r = new Result(word);
            TreeMap<String, ArrayList<Integer>> links = new TreeMap<>();
            r.setLinks(links);
            for (int i = 0; i < files.length - 1; i++){
                ArrayList<Integer> rowNums = new ArrayList<>();

                List<String> fileRows = null;
                try {
                    fileRows = Files.readAllLines(Paths.get(files[i].getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < fileRows.size() - 1; j ++) {
                    if(fileRows.get(j).toLowerCase().contains(word.toLowerCase())){
                        rowNums.add(j);
                    }
                }
                if(rowNums.size() > 0){
                    links.put(files[i].getName(),rowNums);
                }
            }
            System.out.println(r.toString());
            result.add(r);
        }
        writeToFile();
    }

    private static void writeToFile() {
        FileWriter writer = null;
        try {
            File out = new File("/home/plorial/Documents/YouTubeSubs/Sokol/output.txt");
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
