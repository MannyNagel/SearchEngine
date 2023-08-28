package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;



public class DocumentTest {
    //new URI("scheme","host","path","query","null")
    @Test
    public void createBadDocument() {

        assertThrows(IllegalArgumentException.class,() -> new DocumentImpl(null, "hello"));
    }

    @Test
    public void createBadDocument2() {
        assertThrows(IllegalArgumentException.class,() -> new DocumentImpl(new URI("scheme"), (String) null));
    }

    @Test
    public void testHashCode() throws URISyntaxException {
        DocumentImpl d = new DocumentImpl(new URI("uriuri"),"text");
        DocumentImpl d2 = new DocumentImpl(new URI("uriuri"),"text");
        assertTrue(d.equals(d2));
    }

    @Test
    public void testWordCount() throws URISyntaxException{
        DocumentImpl d = new DocumentImpl(new URI("uriuri"),"The text of this document has The word The Three times the 8 9 8");
        assertEquals(1, d.wordCount("the"));
    }

    @Test
    public void testWordCount2() throws URISyntaxException{
        DocumentImpl d = new DocumentImpl(new URI("uriuri"),"The text of this document has The word The is The jiji The Three times the 8 9 8");
        assertEquals(5, d.wordCount("The"));
    }

    @Test
    public void binaryWordCountTest() throws URISyntaxException{
        String s = "Hello world";
        byte[] b = s.getBytes();
        DocumentImpl d = new DocumentImpl(new URI("uriuri"),b);
        DocumentImpl d2 = new DocumentImpl(new URI("uriuri"),s);

        assertTrue(d2.getWords().size() == 2);
        assertTrue(d.getWords().size() == 0);

    }
}
