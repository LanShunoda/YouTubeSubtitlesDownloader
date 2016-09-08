package com.plorial.youtubesubtitlesdownloader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Captions.Download;
import com.google.api.services.youtube.YouTube.Captions.Insert;
import com.google.api.services.youtube.YouTube.Captions.Update;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by plorial on 9/6/16.
 */
public class Main {

    private static YouTube youtube;

    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    static Properties properties;

    static String chanelId = "UCyGPa4_EYA1OiBNjf6IeF6A";

    public static void main(String[] args) {

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account and requires requests to use an SSL connection.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");

        // Read the developer key from the properties file.
        properties = new Properties();
        try {
            InputStream in = YouTube.Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("/home/plorial/Documents/YouTubeSubtitlesDownloader-017d8a98ef76.json"));
           credential = credential.createScoped(scopes);

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(transport, jsonFactory, credential)
                    .setApplicationName("youtubesubtitlesdownloader").build();

            String queryTerm = "";

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            initSearch(search);
            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            String nextPageToken = searchResponse.getNextPageToken();
            System.out.println(nextPageToken);
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
//                prettyPrint(searchResultList.iterator(), queryTerm);
                System.out.println("search list " + searchResultList.size());
                printAllURLS(searchResultList);
            }
            System.out.println("first done");
            while (nextPageToken != null){
                search = youtube.search().list("id,snippet");
                initSearch(search);
                search.setPageToken(nextPageToken);
                searchResponse = search.execute();
                nextPageToken = searchResponse.getNextPageToken();
                searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    printAllURLS(searchResultList);
                }
            }

        } /*catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        }*/ catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static void initSearch(YouTube.Search.List search){

        // Set your developer key from the Google API Console for
        // non-authenticated requests. See:
        // https://console.developers.google.com/
        String apiKey = properties.getProperty("youtube.apikey");
        search.setKey(apiKey);
//            search.setQ(queryTerm);
        search.setChannelId(chanelId);
//            search.setVideoCaption("closedCaption");

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");
        search.setOrder("date");
        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
    }

    private static void printAllURLS(List<SearchResult> searchResultList){
        Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
//                    String title = singleVideo.getSnippet().getTitle();
            String videoId = rId.getVideoId();
            System.out.println("http://www.youtube.com/watch?v="+videoId);

        }
    }

    private static void writeVideoTimedText(String videoId, String videoName){
        try {
        String url = "http://video.google.com/timedtext?";
        String lang = "lang=ru&";
        String v = "v=";

        String full = url + lang + v + videoId;
        System.out.println(full);
        URL u = new URL(full);

            URLConnection conn = u.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(conn.getInputStream());

        TransformerFactory f = TransformerFactory.newInstance();
        Transformer xform = f.newTransformer();

        OutputStream outputStream = null;
            File file = null;
            String outputFolder = "/home/plorial/Documents/YouTubeSubs/";
            try {
                File folder = new File(outputFolder);
                if(!folder.exists()){
                    folder.mkdir();
                }
                file = new File(outputFolder, videoName + videoId + ".xml");
                if(!file.exists()){
                    file.createNewFile();
                }
                outputStream = new FileOutputStream(file);

                xform.transform(new DOMSource(doc), new StreamResult(outputStream));
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(outputStream != null){
                    outputStream.close();
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
