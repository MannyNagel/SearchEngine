package edu.yu.cs.com1320.project;

import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {

    class DocumentComparator implements Comparator<DocumentImpl>{
        String word = null;
        boolean single;

        public DocumentComparator(String word, boolean single){
            this.word = word;
            this.single = single;
        }

        @Override
        public int compare(DocumentImpl doc1, DocumentImpl doc2) {
            if(single) return compareSingle(doc1, doc2);
            else       return comparePlural(doc1, doc2);
        }

        public int compareSingle(DocumentImpl doc1, DocumentImpl doc2) {
            int wordCountDoc1 = (doc1.getWords().contains(word)) ? doc1.wordCount(word) : 0;
            int wordCountDoc2 = (doc2.getWords().contains(word)) ? doc2.wordCount(word) : 0;

            if (wordCountDoc1 >=  wordCountDoc2) return -1;
            return 1;
        }


        public int comparePlural(DocumentImpl doc1, DocumentImpl doc2){
            int wordCountDoc1 = countOfPrefix(doc1);
            int wordCountDoc2 = countOfPrefix(doc2);

            if (wordCountDoc1 >= wordCountDoc2) return -1;
            return 1;
        }

        private int countOfPrefix(DocumentImpl doc){
            int count = 0;
            Set<String> words = doc.getWords();
            for(String s : words){
                if(s.length() >= word.length()) {
                    System.out.println("Word: " + s);
                    //System.out.println(" substring: " + s.substring(0,word.length()));
                    System.out.println("  " + (s.length() >= word.length()));
                    // System.out.println("  " + (s.substring(0,word.length()).equals(word)));
                    if (s.length() >= word.length() && s.substring(0, word.length()).equals(word)) {
                        count += doc.wordCount(s);
                        //System.out.println(" substring: " + s.substring(0,word.length()));
                    }
                }

            }
            System.out.println("\"" + doc.getDocumentTxt() + "\" countOfPrefix: " + count);
            return count;
        }

    };

    TrieImpl<DocumentImpl> trie = new TrieImpl();

    DocumentStoreImpl docStore = new DocumentStoreImpl();
    @Test
    public void addDocumentToTrie() throws URISyntaxException {
        String s = "Hello My Name Is Manny";
        URI uri = new URI("scheme");

        DocumentImpl di = new DocumentImpl(uri, s);

        for (String word : di.getWords()) {
            trie.put(word, di);
            assertTrue(trie.getAllSorted(word, new DocumentComparator(word, true)).contains(di));
        }
        trie.getAllSorted("Hello", new DocumentComparator("Hello", true));
        assertTrue(trie.getAllSorted("Hello", new DocumentComparator("Hello", true)).contains(di));
    }

    @Test
    public void addDocumentToTrie2() throws URISyntaxException {
        String s = "Hello My Name Is Manny";
        URI uri = new URI("scheme");

        DocumentImpl di = new DocumentImpl(uri, s);

        for (String word : di.getWords()) {
            trie.put(word, di);
            assertTrue(trie.getAllSorted(word, new DocumentComparator(word, true)).contains(di));
        }

        //trie.getAllSorted("ny", new DocumentComparator("ny"));
        assertTrue(trie.getAllSorted("My", new DocumentComparator("My", true)).contains(di));
        assertEquals(di, trie.getAllSorted("My", new DocumentComparator("My", true)).get(0));

    }

    @Test
    public void sortsInCorrectOrder() throws URISyntaxException {
        String s = "Hello My Name Is Mandy, and I am a Man";
        URI uri = new URI("scheme");
        DocumentImpl di = new DocumentImpl(uri, s);

        String s2 = "Manfhester is a good sports team";
        URI uri2 = new URI("scheme");
        DocumentImpl di2 = new DocumentImpl(uri2, s2);

        for (String word : di.getWords()) {
            //System.out.println(word);
            trie.put(word, di);
        }

        for (String word : di2.getWords()) {
            //System.out.println(word);
            trie.put(word, di2);
        }

        ArrayList<DocumentImpl> tester = new ArrayList<DocumentImpl>();
        tester.add(di);
        tester.add(di2);

        //System.out.println(trie.getAllWithPrefixSorted("Man", new DocumentComparator("Man")).size());

        assertTrue(trie.getAllWithPrefixSorted("Man", new DocumentComparator("Man", true)).size() == 2);
        assertEquals(tester, trie.getAllWithPrefixSorted("Man", new DocumentComparator("Man", true)));

    }

    @Test
    public void sortsInCorrectOrder2() throws URISyntaxException {
        String s = "H Hi Hii Hiii Hiiii Hiiiii";
        URI uri = new URI("scheme");
        DocumentImpl di = new DocumentImpl(uri, s);

        String s2 = "G";
        URI uri2 = new URI("scheme");
        DocumentImpl di2 = new DocumentImpl(uri2, s2);

        for (String word : di.getWords()) {
            //System.out.println(word);
            trie.put(word, di);
        }

        for (String word : di2.getWords()) {
            //System.out.println(word);
            trie.put(word, di2);
        }

        ArrayList<DocumentImpl> tester = new ArrayList<DocumentImpl>();
        //tester.add(di);
        tester.add(di);


        //System.out.println(trie.getAllWithPrefixSorted("Man", new DocumentComparator("Man")).size());

        //assertTrue(trie.getAllWithPrefixSorted("H", new DocumentComparator("H", false)).size() == 2);
        //assertEquals(tester, trie.getAllWithPrefixSorted("H", new DocumentComparator("H", true)));
        assertEquals(tester, trie.getAllWithPrefixSorted("H", new DocumentComparator("H", false)));

    }


    @Test
    public void sortsInCorrectOrder3() throws URISyntaxException {
        String s = "H Hi Hii Hiii Hiiii Hiiiii H";
        URI uri = new URI("scheme");
        DocumentImpl di = new DocumentImpl(uri, s);

        String s2 = "Hhjkjh H";
        URI uri2 = new URI("scheme");
        DocumentImpl di2 = new DocumentImpl(uri2, s2);

        String s3 = "H Hjkj Hgjhg hjk";
        URI uri3 = new URI("scheme");
        DocumentImpl di3 = new DocumentImpl(uri3, s3);

        for (String word : di.getWords()) {
            //System.out.println(word);
            trie.put(word, di);
        }

        for (String word : di2.getWords()) {
            //System.out.println(word);
            trie.put(word, di2);
        }

        for (String word : di3.getWords()) {
            //System.out.println(word);
            trie.put(word, di3);
        }

        ArrayList<DocumentImpl> tester = new ArrayList<DocumentImpl>();
        tester.add(di);
        tester.add(di3);
        tester.add(di2);

        System.out.println("FINAL PRINT: " + (trie.getAllWithPrefixSorted("H", new DocumentComparator("H", true)).size()));

        assertTrue(trie.getAllWithPrefixSorted("H", new DocumentComparator("H", false)).size() == 3);
        //assertEquals(tester, trie.getAllWithPrefixSorted("H", new DocumentComparator("H", true)));
        assertEquals(tester, trie.getAllWithPrefixSorted("H", new DocumentComparator("H", false)));

    }

    @Test
    public void sortsInCorrectOrder4() throws URISyntaxException {
        String s = "A Prefix of Precaution";
        URI uri = new URI("scheme");
        DocumentImpl di = new DocumentImpl(uri, s);

        String s2 = "Preschool is awesome";
        URI uri2 = new URI("scheme");
        DocumentImpl di2 = new DocumentImpl(uri2, s2);

        String s3 = "kindergarden";
        URI uri3 = new URI("scheme");
        DocumentImpl di3 = new DocumentImpl(uri3, s3);

        for (String word : di.getWords()) {
            //System.out.println(word);
            trie.put(word, di);
        }

        for (String word : di2.getWords()) {
            //System.out.println(word);
            trie.put(word, di2);
        }

        for (String word : di3.getWords()) {
            //System.out.println(word);
            trie.put(word, di3);
        }

        ArrayList<DocumentImpl> tester = new ArrayList<DocumentImpl>();
        tester.add(di);
        tester.add(di2);

        System.out.println("FINAL PRINT: " + (trie.getAllWithPrefixSorted("H", new DocumentComparator("H", true)).size()));

        assertTrue(trie.getAllWithPrefixSorted("Pre", new DocumentComparator("Pre", false)).size() == 2);
        //assertEquals(tester, trie.getAllWithPrefixSorted("H", new DocumentComparator("H", true)));
        assertEquals(tester, trie.getAllWithPrefixSorted("Pre", new DocumentComparator("Pre", false)));

    }

    @Test
    public void deleteValue() throws URISyntaxException {
        String s = "Hello My Name Is Manny";
        URI uri = new URI("scheme");

        DocumentImpl di = new DocumentImpl(uri, s);

        for (String word : di.getWords()) {
            System.out.println(word);
            trie.put(word, di);
        }
        assertTrue(trie.deleteAllWithPrefix("H").size() > 0);
        assertTrue(trie.deleteAllWithPrefix("M").contains(di));
        //assertEquals(di, trie.delete("Is", di));
    }

    @Test
    public void deleteValueSimple() throws URISyntaxException {
        String s = "Hello My Name Is Manny";
        URI uri = new URI("scheme");

        DocumentImpl di = new DocumentImpl(uri, s);

        for (String word : di.getWords()) {
            System.out.println(word);
            trie.put(word, di);
        }

        assertEquals(di, trie.delete("My", di));
    }

    @Test
    public void deleteValue2() throws URISyntaxException {
        ArrayList<DocumentImpl> docList = new ArrayList<>();
        docList.add(generateDoc("abc a"));
        docList.add(generateDoc("bat"));
        docList.add(generateDoc("cat"));
        docList.add(generateDoc("dog"));
        docList.add(generateDoc("alligator"));
        docList.add(generateDoc("the bad kangaroo"));
        docList.add(generateDoc("big donkey"));


        ArrayList<DocumentImpl> compare = new ArrayList<DocumentImpl>();
        compare.add(docList.get(0));
        compare.add(docList.get(4));

        assertEquals(compare, trie.getAllWithPrefixSorted("a", new DocumentComparator("a", false)));

        HashSet<DocumentImpl> compareSet = new HashSet<>();
        compareSet.add(docList.get(0));
        compareSet.add(docList.get(4));
        assertEquals(compareSet, trie.deleteAllWithPrefix("a"));

        HashSet<DocumentImpl> compare2 = new HashSet<>();
        compare2.add(docList.get(1));
        compare2.add(docList.get(5));

        assertEquals(compare2, trie.deleteAllWithPrefix("ba"));

    }

    @Test
    public void deleteValue3() throws URISyntaxException {
        ArrayList<DocumentImpl> docList = new ArrayList<>();
        docList.add(generateDoc("abc a"));
        docList.add(generateDoc("bat"));
        docList.add(generateDoc("cat"));
        docList.add(generateDoc("dog"));
        docList.add(generateDoc("alligator is not very BIG"));
        docList.add(generateDoc("the big big big kangaroo"));
        docList.add(generateDoc("big big donkey dog"));


        ArrayList<DocumentImpl> compare = new ArrayList<DocumentImpl>();
        compare.add(docList.get(5));
        compare.add(docList.get(6));

        assertEquals(compare, trie.getAllWithPrefixSorted("big", new DocumentComparator("big", false)));

        HashSet<DocumentImpl> compareSet = new HashSet<>();
        compareSet.add(docList.get(0));
        compareSet.add(docList.get(4));
        assertEquals(compareSet, trie.deleteAllWithPrefix("a"));

        HashSet<DocumentImpl> compare2 = new HashSet<>();
        compare2.add(docList.get(1));
        //compare2.add(docList.get(5));

        assertEquals(compare2, trie.deleteAllWithPrefix("ba"));

    }

    @Test
    public void deleteAllTest() throws URISyntaxException {
        ArrayList<DocumentImpl> docList = new ArrayList<>();
        docList.add(generateDoc("abc a"));
        docList.add(generateDoc("bat"));
        docList.add(generateDoc("cat"));
        docList.add(generateDoc("dog"));
        docList.add(generateDoc("alligator is not very BIG"));
        docList.add(generateDoc("the big big big kangaroo"));
        docList.add(generateDoc("big big donkey dog"));
        Set<DocumentImpl> set = trie.deleteAll("big");
        assertEquals(set.size(), 2);
        Set<DocumentImpl> empty = new HashSet<>();
        assertEquals(trie.getAllWithPrefixSorted("big", new DocumentComparator("big", false)).size(), 0);
    }

    private DocumentImpl generateDoc(String s) throws URISyntaxException{
        URI uri = new URI("scheme");
        DocumentImpl di = new DocumentImpl(uri, s);
        for (String word : di.getWords()) {
            System.out.println(word);
            trie.put(word, di);
        }
        return di;
    }

}
