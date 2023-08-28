package edu.yu.cs.com1320.project;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DocumentStoreTest {

    private InputStream randomIS(){
        int DATA_SIZE = 1024; // Size of data in bytes
        byte[] data = new byte[DATA_SIZE];
        Random random = new Random();
        random.nextBytes(data);
        return new ByteArrayInputStream(data);
    }
    @Test
    public void addDocument() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int test = ds.put(randomIS(),new URI("scheme"), TXT);
        assertNotNull(test);
    }

    @Test
    public void addDocument2() throws URISyntaxException, IOException {
        URI uri = new URI("scheme");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int test = ds.put(randomIS(),uri, TXT);
        assertNotNull(ds.get(uri));
    }

    @Test
    public void addDocument3() throws URISyntaxException, IOException {
        URI uri = new URI("scheme");
        URI uri2 = new URI("scheme2");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        DocumentStoreImpl ds2 = new DocumentStoreImpl();
        ds.put(randomIS(),uri, TXT);
        ds2.put(randomIS(),uri2,TXT);
        assertNotNull(ds.get(uri));
        assertNotNull(ds2.get(uri2));
    }

    @Test
    public void addDocumentsMany() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int numDocs = 10;
        for (int i = 0; i < numDocs; i++) {
            URI urit = new URI("scheme" + i);
            ds.put(randomIS(), urit, TXT);
            assertNotNull(ds.get(urit));
        }
    }

    @Test
    public void addDocumentsMany3() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int numDocs = 1;
        for (int i = 0; i < numDocs; i++) {
            URI urip = new URI("scheme" + i);
            ds.put(randomIS(), urip, BINARY);
            assertNotNull(ds.get(urip));
        }
    }

    @Test
    public void addDocumentsMany4() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int numDocs = 3;
        for (int i = 0; i < numDocs; i++) {
            URI uri = new URI("scheme" + i);
            ds.put(randomIS(), uri, TXT);
            assertNotNull(ds.get(uri));
        }
    }

    @Test
    public void addDocumentsMany2() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        int numDocs = 500;
        for (int i = 0; i < numDocs; i++) {
            URI uri = new URI("scheme" + i);
            ds.put(randomIS(), uri, TXT);
            assertNotNull(ds.get(uri));
        }
    }

    @Test
    public void addDocumentsMany5() throws URISyntaxException, IOException{
        Object[] t =  addNDocs(500);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        for(int i = 0; i < uris.length; i++){
            assertNotNull(ds.get(uris[i]));
        }
    }

    @Test
    public void bugTest() throws URISyntaxException, IOException{
        int n = 2;
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI[] uris = new URI[n];
        for (int i = 0; i < n; i++) {
            URI uri = new URI("scheme" + i);
            ds.put(randomIS(), uri, TXT);
            assertTrue(ds.get(uri) != null);
            uris[i] = uri;
        }
        assertNotNull(ds.get(uris[0]));
    }

    @Test
    public void deleteDocTest() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri = new URI("scheme");
        InputStream inputStream = randomIS();
        ds.put(inputStream, uri, TXT);
        assertNotNull(ds.get(uri));
        ds.delete(uri);
        assertNull(ds.get(uri));
    }

    @Test
    public void deleteDocTest2() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri = new URI("scheme");
        InputStream inputStream = randomIS();
        URI uri1 = new URI("scheme1");
        InputStream inputStream1 = randomIS();
        ds.put(inputStream, uri, TXT);
        //ds.put(inputStream1,uri1,TXT);
        assertNotNull(ds.get(uri));
        //assertNotNull(ds.get(uri1));
        ds.put(null,uri,TXT);
        assertNull(ds.get(uri));
    }


 /*@OBJECTIVE Test Undo
    * 1. Add and then undo
    * 2. Overwrite and then undo
    * 3. Delete and then undo
    * 4. Place a null value for a URI and then undo
    * 5. a) Undo a specific URI that is present
    *    b) Undo a specific URI that is NOT present (Should throw error)
    * 6. Undo after nothing is in the table
    **/


    @Test
    public void undoTest1a() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        assertNotNull(ds.get(uris[1]));
        ds.undo();
        assertNull(ds.get(uris[1]));
    }

    @Test //undo 2 documents
    public void undoTest1b() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        assertNotNull(ds.get(uris[1]));
        ds.undo();
        assertNull(ds.get(uris[1]));
        ds.undo();
        assertNull(ds.get(uris[0]));
    }

    @Test //undo 30 documents
    public void undoTest1c() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(4);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        int length = 4;
        for(int i = 0; i < length; i++){
            assertNotNull(ds.get(uris[i]));
            ds.undo(uris[i]);
            assertNull(ds.get(uris[i]));
        }

    }

    @Test //Overwrite a document
    public void undoTest2a() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        int length = 2;
        URI overwrite = uris[length-1];
        DocumentImpl old = (DocumentImpl)  ds.get(overwrite);
        assertTrue(old == (DocumentImpl) ds.get(overwrite));
        int hash = ds.put(randomIS(),overwrite,TXT);
        assertFalse(old == (DocumentImpl) ds.get(overwrite));
        ds.undo();
        assertTrue(old == (DocumentImpl) ds.get(overwrite));
    }

    @Test //Overwrite a document
    public void undoTest2b() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        int length = 2;
        URI overwrite = uris[length-2];
        DocumentImpl old = (DocumentImpl)  ds.get(overwrite);
        assertTrue(old == (DocumentImpl) ds.get(overwrite));
        int hash = ds.put(randomIS(),overwrite,BINARY);
        assertFalse(old == (DocumentImpl) ds.get(overwrite));
        ds.undo();
        assertTrue(old == (DocumentImpl) ds.get(overwrite));
    }

    @Test //Overwrite a document
    public void undoTest3a() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        int length = 2;
        URI deletingURI = uris[length-2];
        DocumentImpl old = (DocumentImpl) ds.get(deletingURI);
        assertTrue(old == (DocumentImpl) ds.get(deletingURI));
        ds.delete(deletingURI);
        assertNull(ds.get(deletingURI));
        //TRYING TO Pull a document from btree when it is not in there
        ds.undo(deletingURI);
        assertTrue(old.equals((DocumentImpl) ds.get(deletingURI)));
    }

    @Test //Overwrite a document
    public void undoTest34() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(1);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        int length = 1;
        URI deletingURI = uris[length-1];
        DocumentImpl old = (DocumentImpl) ds.get(deletingURI);
        assertTrue(old == (DocumentImpl) ds.get(deletingURI));
        ds.delete(deletingURI);
        assertNull(ds.get(deletingURI));
        //TRYING TO Pull a document from btree when it is not in there
        ds.undo(deletingURI);
        assertTrue(old.equals((DocumentImpl) ds.get(deletingURI)));
    }

    @Test //Undo an empty stack
    public void undoTest6() throws URISyntaxException, IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        assertThrows(IllegalStateException.class,() -> ds.undo());
    }

    @Test //undo 2 documents
    public void undoTest4() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(2);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        assertNotNull(ds.get(uris[1]));
        ds.undo();
        assertNull(ds.get(uris[1]));

        URI a = new URI("FAKE");
        assertThrows(IllegalStateException.class,() -> ds.undo(a));
    }

    @Test //undo 2 documents
    public void undoTest7() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(3);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        assertNotNull(ds.get(uris[1]));
        ds.undo(uris[1]);
        assertNull(ds.get(uris[1]));
        URI a = new URI("FAKE");
        assertThrows(IllegalStateException.class,() -> ds.undo(a));
    }

    @Test //undo 2 documents
    public void undoTest8() throws URISyntaxException, IOException {
        Object[] t =  addNDocs(9);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        assertNotNull(ds.get(uris[0]));
        ds.undo(uris[0]);
        assertNull(ds.get(uris[0]));

        URI a = new URI("FAKE");
        assertThrows(IllegalStateException.class,() -> ds.undo(a));
    }

    @Test //undo 2 documents
    public void undoTest9() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        ds.undo((URI) t[0]);
        assertNull(ds.get((URI) t[0]));

        URI a = new URI("FAKE");
        assertThrows(IllegalStateException.class,() -> ds.undo(a));
    }

    @Test //undo 2 documents
    public void undoTest11() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO");
        Object[] t2 = addDoc("HI");
        Object[] t3 = addDoc("HUEU");
        URI uri1 = (URI) t[0];
        URI uri2 = (URI) t2[0];
        URI uri3 = (URI) t3[0];
        DocumentStoreImpl ds = new DocumentStoreImpl();

        ds.put((InputStream) t[1], uri1, TXT);
        ds.put((InputStream) t2[1], uri2, TXT);
        ds.put((InputStream) t3[1], uri3, TXT);
        assertNotNull(ds.get(uri3));
        ds.undo(uri3);
        assertNull(ds.get(uri3));

        URI a = new URI("FAKE");
        assertThrows(IllegalStateException.class,() -> ds.undo(a));
    }

    @Test
    public void undoTest10() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        List<Document> prefix = ds.searchByPrefix("H");
//        System.out.println("Prefix List size: " + prefix.size());
        Set<URI> deletedDocuments = ds.deleteAllWithPrefix("H");
//        System.out.println("DELETED DOCS SIZE: " + deletedDocuments.size());
        assertNull(ds.get((URI) t[0]));
        assertNull(ds.get((URI) t2[0]));
        //assertEquals((URI) t[0], deletedDocuments.toArray()[0]);
        ds.undo((URI) t2[0]);
        assertFalse(ds.searchByPrefix("H").contains(ds.get((URI) t[0])));
        assertNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));

        ds.undo();
        assertNotNull(ds.get((URI) t[0]));

        Object[] t3 = addDoc("THird Documennt Has no probels");
        ds.put((InputStream) t3[1], (URI) t3[0], TXT);
        assertTrue(ds.searchByPrefix("H").size() == 3);

        ds.undo();
        assertNull(ds.get((URI) t3[0]));

        //assertEquals(deletedDocuments.toArray(),ds.searchByPrefix("HELL").toArray()[0].getURI());



    }

    @Test
    public void undoTest12() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO H");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));

        Set<URI> deletedDocuments = ds.deleteAllWithPrefix("H");
        //ds.deleteAll("HEY");

//        System.out.println("DELETED DOCS SIZE: " + deletedDocuments.size());
        assertNull(ds.get((URI) t[0]));
        assertNull(ds.get((URI) t2[0]));
        URI[] test = new URI[0];
//        assertEquals(0, deletedDocuments.toArray().length);
        ds.undo();

        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));


    }

    @Test
    public void undoTest13() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));

        ds.delete((URI) t[0]);
        //ds.deleteAll("HEY");
//        System.out.println("DELETED DOCS SIZE: " + deletedDocuments.size());
        assertNull(ds.get((URI) t[0]));
//        assertNull(ds.get((URI) t2[0]));
        //assertEquals((URI) t[0], deletedDocuments.toArray()[0]);
        ds.undo((URI) t[0]);

        assertNotNull(ds.get((URI) t[0]));
//        assertNull(ds.get((URI) t2[0]));
//
    }

    @Test
    public void deleteAllTest() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO H");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        List<Document> prefix = ds.searchByPrefix("H");
//        System.out.println("Prefix List size: " + prefix.size());
        Set<URI> deletedDocuments = ds.deleteAll("H");
//        System.out.println("DELETED DOCS SIZE: " + deletedDocuments.size());
        assertNull(ds.get((URI) t[0]));
        assertNull(ds.get((URI) t2[0]));
        //assertEquals((URI) t[0], deletedDocuments.toArray()[0]);
        ds.undo((URI) t2[0]);
        assertFalse(ds.searchByPrefix("H").contains(ds.get((URI) t[0])));
        assertNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
    }

    @Test
    public void deleteAllWithPrefixTest() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO H");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        List<Document> prefix = ds.searchByPrefix("H");
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory((URI)t[0]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory((URI)t2[0]))));

    }


    @Test
    public void heapifyTest1() throws URISyntaxException, IOException {
        String[] texts = {"doc11", "doc12", "doc21", "doc22"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];

        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);
        List<Document> list = ds.searchByPrefix("doc");
        for(Document doc : list) System.out.print(doc.getDocumentTxt() + " ");
        System.out.println();

        /*System.out.println(heap.getArrayIndex(d0));
        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));*/

    }

    @Test
    public void heapifyTest2() throws URISyntaxException, IOException {
        String[] texts = {"doc1", "doc112", "doc1123", "doc234"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];

        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);
        List<Document> list = ds.searchByPrefix("doc1");
//        for(Document doc : list) System.out.print(doc.getDocumentTxt() + " ");
        System.out.println();

        /*System.out.println(heap.getArrayIndex(d0));
        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));*/

    }

    @Test
    public void removeHeapTest() throws URISyntaxException, IOException {
        String[] texts = {"doc1", "doc112", "doc1123", "doc234"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];

        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);
//        List<Document> list = ds.searchByPrefix("doc1");
//        for(Document doc : list) System.out.print(doc.getDocumentTxt() + " ");
        System.out.println();
        ds.delete(uris[1]);
        /*System.out.println(heap.getArrayIndex(d0));
//        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));*/

    }

    @Test
    public void removeHeapTest2() throws URISyntaxException, IOException {
        String[] texts = {"doc1", "doc112", "doc1123", "doc234"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);
        System.out.println();
        ds.delete(uris[1]);
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[1]))));
    }

    @Test
    public void docLimitTest1() throws URISyntaxException, IOException {
        String[] texts = {"doc1", "doc112", "doc1123", "doc234"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];

        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);

//        List<Document> list = ds.searchByPrefix("doc1");
//        for(Document doc : list) System.out.print(doc.getDocumentTxt() + " ");

//        ds.setMaxDocumentCount(2);

        System.out.println();
        /*System.out.println(heap.getArrayIndex(d0));
        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));*/


//        assertNull(ds.get(uris[1]));
//        assertEquals(ds.search("doc112").size(), 0);
    }

    @Test
    public void docLimitTest3() throws URISyntaxException, IOException {
        String[] texts = {"http://doc1", "http://doc2", "http://doc3", "http://doc4"};
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        URI[] uris = (URI[]) t[1];
        /*ds.get(uris[0]);
        ds.get(uris[1]);
        ds.get(uris[2]);
        ds.get(uris[3]);*/

        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[0]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[1]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[2]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[3]))));
        ds.setMaxDocumentCount(2);
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory(uris[0]))));
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory(uris[1]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[2]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[3]))));
        ds.get(uris[0]);
        ds.get(uris[1]);
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[0]))));
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory(uris[1]))));
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory(uris[2]))));
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory(uris[3]))));
        System.out.println();

    }

    @Test
    public void docLimitTest2() throws URISyntaxException, IOException, NoSuchElementException {
        String[] texts = {"doc", "doc", "doc", "doc"};
        String doc = "doc";
        int bytelimit = doc.getBytes().length*4;
        Object[] t =  addNDocs(4, texts);
        DocumentStoreImpl ds = (DocumentStoreImpl) t[0];
        URI[] uris = (URI[]) t[1];

        DocumentImpl d0 = (DocumentImpl) ds.get(uris[0]);
        DocumentImpl d2 = (DocumentImpl) ds.get(uris[2]);
        DocumentImpl d1 = (DocumentImpl) ds.get(uris[1]);
        DocumentImpl d3 = (DocumentImpl) ds.get(uris[3]);

//        List<Document> list = ds.searchByPrefix("doc1");
//        for(Document doc : list) System.out.print(doc.getDocumentTxt() + " ");

//        ds.setMaxDocumentCount(2);
//        ds.setMaxDocumentBytes(bytelimit+5000000);
        ds.setMaxDocumentBytes(bytelimit-1);

        ds.put(new ByteArrayInputStream(doc.getBytes()),uris[0],TXT);

        System.out.println();
        /*System.out.println(heap.getArrayIndex(d0));
        System.out.println(heap.getArrayIndex(d1));
//        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));*/


//        assertNull(ds.get(uris[1]));
//        assertEquals(ds.search("doc112").size(), 0);
    }


    @Test
    public void docLimitTest4() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO H");
        Object[] t2 = addDoc("HIT HEY H");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        ds.setMaxDocumentCount(2);
        Object[] t3 = addDoc("Doc3");
        ds.put((InputStream) t3[1], (URI) t3[0], TXT);
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
    }

    @Test
    public void docLimitTest5() throws URISyntaxException, IOException {
        Object[] t =  addDoc("HELLO H");
        Object[] t2 = addDoc("HIT HEY H");
        File file = new File(System.getProperty("user.dir"));
        DocumentStoreImpl ds = new DocumentStoreImpl(file);
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ((InputStream) t[1]).close();
        ((InputStream) t2[1]).close();

        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        Object[] t3 = addDoc("Doc3");
        ds.put((InputStream) t3[1], (URI) t3[0], TXT);
        ((InputStream) t3[1]).close();
        ds.setMaxDocumentCount(2);
        //assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        //assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
        //assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
        ds.setMaxDocumentCount(5);
        ds.get((URI) t[0]);
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
    }

    @Test
    public void docLimitTest6() throws URISyntaxException, IOException {
        Object[] t =  addDoc("Doc1");
        Object[] t2 = addDoc("Doc2");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        ds.setMaxDocumentCount(2);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ((InputStream) t[1]).close();
        ((InputStream) t2[1]).close();
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        Object[] t3 = addDoc("Doc3");
        ds.delete((URI) t[0]);
        ds.put((InputStream) t3[1], (URI) t3[0], TXT);
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
        ds.undo((URI) t[0]);
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
    }

    @Test
    public void docLimitTest7() throws URISyntaxException, IOException {
        Object[] t =  addDoc("Doc1");
        Object[] t2 = addDoc("Doc2");
        DocumentStoreImpl ds = new DocumentStoreImpl();
        File file = new File(System.getProperty("user.dir"));
        DocumentPersistenceManager pm = new DocumentPersistenceManager(file);
        ds.setMaxDocumentCount(2);
        ds.put((InputStream) t[1], (URI) t[0], TXT);
        ds.put((InputStream) t2[1], (URI) t2[0], TXT);
        ((InputStream) t[1]).close();
        ((InputStream) t2[1]).close();
        assertNotNull(ds.get((URI) t[0]));
        assertNotNull(ds.get((URI) t2[0]));
        Object[] t3 = addDoc("Doc3");
        ds.put((InputStream) t3[1], (URI) t3[0], TXT);
        assertTrue(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
        ds.undo(); //undo the put
        assertTrue(!Files.exists(Paths.get(pm.convertURItoDirectory((URI) t3[0]))));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t2[0]))));
        //should be brought back into memory by search
        ds.get((URI) t[0]);
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
        assertNotNull(ds.get((URI) t[0]));
        assertFalse(Files.exists(Paths.get(pm.convertURItoDirectory((URI) t[0]))));
    }

    @Test
    public void stackTest1(){
        StackImpl<Integer> s = new StackImpl<>();
        assertEquals(0, s.size());
    }

    @Test
    public void stackTest2(){
        StackImpl<Integer> s = new StackImpl<>();
        s.push(1);
        assertEquals(1, s.size());
    }

    private Object[] addNDocs(int n) throws URISyntaxException, IOException{
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI[] uris = new URI[n];
        for (int i = 0; i < n; i++) {
            URI urim;
            if(i%2==0){urim = new URI("even" + i);}
            else {urim = new URI("odd" + i);}
            ds.put(randomIS(), urim, TXT);
            uris[i] = urim;
        }
        Object[] items = new Object[]{ds,uris};
        return items;
    }

    private Object[] addNDocs(int n, String[] texts) throws URISyntaxException, IOException{
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI[] uris = new URI[n];
        for (int i = 0; i < n; i++) {
            URI urim;
            urim = new URI("http://doc"+i);

            ds.put(convertiStringToInputStream(texts[i]), urim, TXT);
            uris[i] = urim;
        }
        Object[] items = new Object[]{ds,uris};
        return items;
    }

    private Object[] addDoc(String s) throws URISyntaxException, IOException{
        URI uri = new URI("http://stage5/i" + (Math.random()*100 * Math.random()));
        byte[] b = s.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Object[] items = new Object[]{uri,is};
        return items;
    }

    public InputStream convertiStringToInputStream(String str){
        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
        return inputStream;
    }

    private DocumentImpl generateDoc(String s) throws URISyntaxException{
        URI uri = new URI("Scheme:" + (Math.random()*100 * Math.random()));
        DocumentImpl di = new DocumentImpl(uri, s);
        return di;
    }

}
