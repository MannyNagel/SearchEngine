package edu.yu.cs.com1320.project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager.*;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;
public class PersistenceManagerTest {
    File file = new File(System.getProperty("user.dir"));
    DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
    @Test
    public void createJsonFile() throws URISyntaxException {
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        String uriString = pm.convertURItoDirectory(uri);
        assertEquals(file.getPath()+"/www.yu.edu/documents/doc1.json",uriString);
    }

    @Test
    public void complexUriTest() throws URISyntaxException {
        URI uri = new URI("http://www.yu#.edu@@/@documents-/doc1");
        String uriString = pm.convertURItoDirectory(uri);
        assertEquals(file.getPath()+"/www.yu.edu/documents/doc1.json",uriString);
    }

    @Test
    public void serializeJsonFile() throws URISyntaxException, IOException {
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        Document doc = new DocumentImpl(uri,"document contents");
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        assertTrue(Files.exists(path));
    }

    @Test
    public void serialize100JsonFiles() throws URISyntaxException, IOException {
        Path[] paths = new Path[100];
        //serialize 100 uris
        for(int i = 0; i < 100; i++){
            URI uri = new URI("http://www.yu.edu/documents/doc"+i+1);
            Document doc = new DocumentImpl(uri,"document contents"+i+1);
            pm.serialize(uri,doc);
            paths[i] = Paths.get(pm.convertURItoDirectory(uri));
        }
        //check to see if all of these paths exist
        for(int i = 0; i < 100; i++)
            assertTrue(Files.exists(paths[i]));
    }

    @Test
    public void deserialize100JsonFiles() throws URISyntaxException, IOException {
        Path[] paths = new Path[100];
        Document[] docs = new Document[100];
        URI[] uris = new URI[100];
        //serialize 100 uris
        for(int i = 0; i < 100; i++){
            URI uri = new URI("http://www.yu.edu/documents/doc"+i+1);
            Document doc = new DocumentImpl(uri,"document contents"+i+1);
            pm.serialize(uri,doc);
            paths[i] = Paths.get(pm.convertURItoDirectory(uri));
            docs[i] = doc; uris[i] = uri;
        }
        for(int i = 0; i < 100; i++) {
            assertEquals(docs[i],pm.deserialize(uris[i]));
        }
            //check to see if all of these paths exist
        for(int i = 0; i < 100; i++)
            assertTrue(Files.exists(paths[i]));
    }

    @Test
    public void deleteJsonFile() throws URISyntaxException, IOException {
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        Document doc = new DocumentImpl(uri,"document contents");
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        assertTrue(Files.exists(path));
        //delete file
        pm.delete(uri);
        assertTrue(!Files.exists(path));
    }

    @Test
    public void deleteJsonFile2() throws URISyntaxException, IOException {
        URI uri = new URI("alkjd:nskdf");
        Document doc = new DocumentImpl(uri,"document contents");
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        assertTrue(Files.exists(path));
        //delete file
        pm.delete(uri);
        assertTrue(!Files.exists(path));
    }





    public void randomTests() throws URISyntaxException, IOException {
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);

        URI uri = new URI("http://www.yu.edu/documents/doc1");
        //System.out.println(pm.convertURItoDirectory(uri));
        Document d = new DocumentImpl(uri,"This is the content of my document");

        pm.serialize(uri,d);
        pm.deserialize(uri);
    }

    @Test
    public void serializeText() throws URISyntaxException, IOException{
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        Document doc = new DocumentImpl(uri,"document contents");
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        assertTrue(Files.exists(path));
        Document newDoc = pm.deserialize(uri);
        assertEquals(newDoc,doc);
        assertEquals(newDoc.getDocumentTxt(), doc.getDocumentTxt());
    }

    @Test
    public void serializeBinary() throws URISyntaxException, IOException{
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        Document doc = new DocumentImpl(uri,new byte[]{1,2,3,4,5});
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        assertTrue(Files.exists(path));
        Document newDoc = pm.deserialize(uri);
        assertEquals(newDoc,doc);
    }

    @Test
    public void retrieveFromDiskTest1()throws URISyntaxException, IOException{
        URI uri = new URI("http://www.yu.edu/documents/doc1");
        Document doc = new DocumentImpl(uri,new byte[]{1,2,3,4,5});
        String uriString = pm.convertURItoDirectory(uri);
        pm.serialize(uri,doc);
        Path path = Paths.get(uriString);
        System.out.println(pm.retrieveJsonFromDisk(uriString));
        JsonSerializer<Document> serializer = new DocumentPersistenceManager.Serializer();
        String content = serializer.serialize(doc,Document.class,null).toString();
        assertEquals(pm.retrieveJsonFromDisk(uriString),content);
        assertTrue(Files.exists(path));
        Document newDoc = pm.deserialize(uri);
        assertEquals(newDoc,doc);
    }

}
