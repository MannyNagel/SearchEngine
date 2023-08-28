package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
public class BTreeTest {
    BTreeImpl<String,Integer> bTree = new BTreeImpl<>();
    BTree<URI,Document> bTreeDoc = new BTreeImpl<URI, Document>();

    public BTreeTest(){
        DocumentPersistenceManager pm = new DocumentPersistenceManager(new File(System.getProperty("user.dir")));
        bTreeDoc.setPersistenceManager(pm);
    }


    @Test
    public void addElement1(){
        String s = "tester";
        bTree.put(s,3);
        assertEquals(3,bTree.get(s));
    }

    @Test
    public void add10Elements(){
        String[] array = new String[10];
        for(int i = 0; i < 10; i++){
            array[i] = "string" + i;
            bTree.put(array[i], i);
        }
        for(int i = 0; i < 10; i++){
            assertEquals(i,bTree.get(array[i]));
        }
    }




    private Object[] generateDoc(String s) throws URISyntaxException{
        URI uri = new URI("scheme" + (Math.random()*100 * Math.random()));
        DocumentImpl di = new DocumentImpl(uri, s);
        return new Object[]{uri,di};
    }

}
