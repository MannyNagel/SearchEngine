/*
package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;
public class MinHeapTest {
    MinHeapImpl<Document> heap = new MinHeapImpl<>();
    private InputStream randomIS(){
        int DATA_SIZE = 1024; // Size of data in bytes
        byte[] data = new byte[DATA_SIZE];
        Random random = new Random();
        random.nextBytes(data);
        return new ByteArrayInputStream(data);
    }
    @Test
    public void addToHeapTest1(){

//        String change = "C";
//        heap.insert("D");
//        heap.insert(change);
//        heap.insert("B");
//        heap.insert("A");
//        heap.insert("E");
//
//        System.out.println(heap.getArrayIndex("A"));
//        System.out.println(heap.getArrayIndex("B"));
//        System.out.println(heap.getArrayIndex(change));
//        System.out.println(heap.getArrayIndex("D"));
//        System.out.println(heap.getArrayIndex("E"));
//
//        change = "a";
//        //heap.reHeapify(change);
//
//        System.out.println(heap.getArrayIndex("A"));
//        System.out.println(heap.getArrayIndex("B"));
//        System.out.println(heap.getArrayIndex("a"));
//        System.out.println(heap.getArrayIndex("D"));
//        System.out.println(heap.getArrayIndex("E"));

    }

    @Test
    public void nanoTimeTest() throws URISyntaxException {
        DocumentImpl d1 = new DocumentImpl(new URI("uriuri"),"text1");
        DocumentImpl d2 = new DocumentImpl(new URI("uriuri"),"text2");
        DocumentImpl d3 = new DocumentImpl(new URI("uriuri"),"text3");
        DocumentImpl d4 = new DocumentImpl(new URI("uriuri"),"text4");
        DocumentImpl d5 = new DocumentImpl(new URI("uriuri"),"text5");

        heap.insert(d1);
        heap.insert(d2);
        heap.insert(d3);
        heap.insert(d4);
        heap.insert(d5);

        System.out.println(d1.getLastUseTime());
        System.out.println(d2.getLastUseTime());
        System.out.println(d3.getLastUseTime());
        System.out.println(d4.getLastUseTime());
        System.out.println(d5.getLastUseTime());


        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));
        System.out.println(heap.getArrayIndex(d4));
        System.out.println(heap.getArrayIndex(d5));

        d2.setLastUseTime(System.nanoTime());

        assertFalse(d2.getLastUseTime() < d4.getLastUseTime());

        heap.reHeapify(d2);

        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));
        System.out.println(heap.getArrayIndex(d4));
        System.out.println(heap.getArrayIndex(d5));
    }

    @Test
    public void nanoTimeTestInDocStore() throws URISyntaxException {
        DocumentImpl d1 = new DocumentImpl(new URI("uriuri"),"text1");
        DocumentImpl d2 = new DocumentImpl(new URI("uriuri"),"text2");
        DocumentImpl d3 = new DocumentImpl(new URI("uriuri"),"text3");
        DocumentImpl d4 = new DocumentImpl(new URI("uriuri"),"text4");
        DocumentImpl d5 = new DocumentImpl(new URI("uriuri"),"text5");

        DocumentStoreImpl store = new DocumentStoreImpl();

        heap.insert(d1);
        heap.insert(d2);
        heap.insert(d3);
        heap.insert(d4);
        heap.insert(d5);

        System.out.println(d1.getLastUseTime());
        System.out.println(d2.getLastUseTime());
        System.out.println(d3.getLastUseTime());
        System.out.println(d4.getLastUseTime());
        System.out.println(d5.getLastUseTime());


        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));
        System.out.println(heap.getArrayIndex(d4));
        System.out.println(heap.getArrayIndex(d5));

        d2.setLastUseTime(System.nanoTime());

        assertFalse(d2.getLastUseTime() < d4.getLastUseTime());
        heap.reHeapify(d2);

        d1.setLastUseTime(System.nanoTime());
        heap.reHeapify(d1);

        d2.setLastUseTime(d2.getLastUseTime() - 1000000000);
        heap.reHeapify(d2);
        assertFalse(d3.getLastUseTime() < d2.getLastUseTime());

        System.out.println(heap.getArrayIndex(d1));
        System.out.println(heap.getArrayIndex(d2));
        System.out.println(heap.getArrayIndex(d3));
        System.out.println(heap.getArrayIndex(d4));
        System.out.println(heap.getArrayIndex(d5));
    }



}
*/
