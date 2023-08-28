package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.JsonSerializer;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.Utils;



import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import jakarta.xml.bind.DatatypeConverter;

import javax.swing.event.DocumentEvent;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;


/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    //TODO CHANGE THIS LATER: just class Serializer
    public static class Serializer implements com.google.gson.JsonSerializer<Document>{
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            // Add Content and URI to the Json
            // TODO check and account for a binary document
            if(document == null){return null;}
            if(document.getDocumentTxt() != null) jsonObject.addProperty("contentsText", document.getDocumentTxt()); //add document contents
            if(document.getDocumentBinaryData() != null) {
                String binaryData = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                jsonObject.addProperty("contentsBinary", binaryData); //add document contents
            }

            URI uri = document.getKey();
            // String uriString = convertURItoDirectory(uri);
            String uriString = uri.toString();
            jsonObject.addProperty("uriString", uriString); // Add URI

            //Add the wordMap by making a new object a adding all the key-value pairs
            JsonObject mapObject = new JsonObject();
            Map<String, Integer> map = document.getWordMap();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                mapObject.addProperty(entry.getKey(), entry.getValue());
            }
            jsonObject.add("mapProperty", mapObject);
            return jsonObject;

        }
    }

    class Deserializer implements JsonDeserializer<Document> {
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();
            // Retrieve the properties from the JSON object
            String text = "";
            byte[] byteArray = null;
            boolean isTextDoc = false;
            if(jsonObject.has("contentsText")) {
                isTextDoc = true;
                text = jsonObject.get("contentsText").getAsString();
            }
            if(jsonObject.has("contentsBinary")) {
                String byteArrayString = jsonObject.get("contentsBinary").getAsString();
                byteArray = DatatypeConverter.parseBase64Binary(byteArrayString);
            }

            String uriString = jsonObject.get("uriString").getAsString();
            URI uri;
            try {
                uri = new URI(uriString);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            // Retrieve map
            Map<String, Integer> map = new HashMap<>();
            JsonObject mapObject = jsonObject.getAsJsonObject("mapProperty");
            for (Map.Entry<String, JsonElement> entry : mapObject.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue().getAsInt();
                map.put(key, value);
            }
            // TODO convert the uriString back into a URI
            //From specifications
            /*  3.3 Converting URIs to a location on disk for Serialized files
                Let’s assume the user gives your doc a URI of “http://www.yu.edu/documents/doc1”. The JSON file for that document
                should be stored under [base directory]/www.yu.edu/documents/doc1.json. In other words, remove the
                “http://”, and then convert the remaining path of the URI to a file path under your base directory. Each path segment
                represents a directory, and the namepart of the URI represents the name of your file. You must add “.json” to the end of the
                file name.*/
            Document d = null;
            if(isTextDoc)  d = new DocumentImpl(uri, text, map);
            if(!isTextDoc) d = new DocumentImpl(uri,byteArray);
            return d;
        }
    }

    private File baseDir;

    public DocumentPersistenceManager(File baseDir){
        this.baseDir = baseDir;
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        System.out.println("uri path: " + convertURItoDirectory(uri));
        System.out.print("converted uri: ");
        System.out.println(convertURItoDirectory(uri));
        JsonSerializer<Document> serializer = new Serializer();
        JsonElement jsonElement = serializer.serialize(val, Document.class, null);
        //System.out.println(jsonElement.toString());
        saveJsonToFile(jsonElement.toString(),convertURItoDirectory(uri));
//        FileWriter fileWriter = new FileWriter(,convertURItoDirectory(uri));
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        JsonDeserializer<Document> deserializer = new Deserializer();
        String filePath = convertURItoDirectory(uri);
        String jsonData = retrieveJsonFromDisk(filePath);
        JsonElement jsonElement = JsonParser.parseString(jsonData);
        Document doc = deserializer.deserialize(jsonElement,Document.class,null);
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String filePathString = convertURItoDirectory(uri);
        Path filePath = Paths.get(filePathString);

        if(Files.exists(filePath)){
            System.out.println("file exists");
        }
//        System.out.println(Files.deleteIfExists(filePath));
        try{
            Files.deleteIfExists(filePath);
        }
        catch(NoSuchFileException e){ System.out.println("No such file");}
        catch(IOException io){
            System.out.println("IO Exception");
            System.out.println(io.getMessage());
            io.printStackTrace();
            System.out.println(io.getCause());
        }
        return false;
    }
    //TODO CHANGE TO PRIVATE
    public String retrieveJsonFromDisk(String path) throws IOException {
        /*BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder jsonContent = new StringBuilder();
        String line;*/

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes);

        /*while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }
        reader.close();
        return jsonContent.toString();*/
    }

    public String convertURItoDirectory(URI uri){
        StringBuilder sb = new StringBuilder();
        //Start every file with the base directory
        sb.append(baseDir.getPath());
        String uriString = uri.toString();
        uriString = uriString.replaceAll("[^a-zA-Z0-9/.]", "");
        uriString = uriString.substring(uri.getScheme().length()+1);
        if(uriString.startsWith("//")){
            uriString = uriString.substring(6);
        }
        //sb.append(File.separator + uri.getAuthority());
        //sb.append(File.separator + uri.getPath());
        sb.append(uriString);

        sb.append(".json");

        return sb.toString();
    }

    private boolean saveJsonToFile(String jsonData, String filePath) throws IOException {
        Path filepath = Path.of(filePath).getParent();
        Files.createDirectories(filepath);
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(jsonData);
        System.out.println("JSON file saved successfully.");
        fileWriter.close();
        return true;
    }

    /*public static void main(String[] args) throws URISyntaxException, IOException {
        File file = new File(System.getProperty("user.dir"));
        System.out.println("FILEPATH: " + file.getPath());
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);

        URI uri = new URI("http://www.yu.edu/documents/doc1");
        //System.out.println(pm.convertURItoDirectory(uri));
        Document d = new DocumentImpl(uri,"This is the content of my document");
        pm.serialize(uri,d);
        pm.deserialize(uri);
    }*/
}
