package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {

    private URI uri;
    private byte[] binaryData;
    private String text;
    private boolean implementedAsText;
    private Map<String, Integer> wordMap = new HashMap<>();
    private HashSet<String> wordSet = new HashSet<>();
    private long lastTimeUsed;



    public DocumentImpl(URI uri, String text){
        if(uri == null || text == null || text.equals("")) throw new IllegalArgumentException("Blank or Null argument");
        this.uri = uri;
        this.text = text;
        //this.binaryData = text.getBytes();
        this.implementedAsText = true;
        createWordBank(text);
        setLastUseTime(System.nanoTime());
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || binaryData == null || binaryData.length == 0) throw new IllegalArgumentException("Blank or null");
        this.uri = uri;
        this.binaryData = binaryData;
        //this.text = new String(binaryData); //Dont allow a binary document to have text
        this.implementedAsText = false;
        setLastUseTime(System.nanoTime()); //set last used time as the time it is made.
    }

    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap){
        if(uri == null || text == null || text.equals("")) throw new IllegalArgumentException("Blank or Null argument");
        this.uri = uri; this.text = text; this.implementedAsText = true;
        if(wordCountMap == null) createWordBank(text);
        // TODO do i need to make the workBank??
        if(wordCountMap != null) setWordMap(wordCountMap);
        else createWordBank(text);
        setLastUseTime(System.nanoTime());
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt(){
        return this.text;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData(){
        return binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey(){
        return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word){
        if(!implementedAsText) return 0;
        return wordMap.get(word);
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords(){
        if(!implementedAsText) return new HashSet<String>();
        return Collections.unmodifiableSet(wordSet);
    }

    public void setWordMap(Map<String,Integer> wordMap){
        this.wordMap = wordMap;
    }

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
        Map<String, Integer> copyMap = new HashMap<>();
        copyMap.putAll(this.wordMap);
        return copyMap;
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    public long getLastUseTime(){
        return this.lastTimeUsed;
    }

    public void setLastUseTime(long timeInNanoseconds){
        this.lastTimeUsed = timeInNanoseconds;
    }

    private void createWordBank(String text){
        if(text == null) return;

        String[] words = text.split(" ");
        for(String a: words) {
            String str = "";
            for (int i = 0; i < a.length(); i++) {
                char c = a.charAt(i);
                if ('0' <= c && c <= '9')
                    str = str + c;
                if ('a' <= c && c <= 'z')
                    str = str + c;
                if ('A' <= c && c <= 'Z')
                    str = str + c;
            }
            int wc = (wordMap.get(str) == null) ? 0 : wordMap.get(str);
            wordMap.put(str, wc + 1);
            wordSet.add(str);
        }
    }

    @Override
    public int hashCode(){
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;                    //Same Object       == true
        if(!(obj instanceof DocumentImpl)) return false;//Different classes == false
        DocumentImpl other = (DocumentImpl) obj;        //Cast obj to DocumentImpl
        return other.hashCode() == this.hashCode();     //Check if hash codes are equal
    }

    @Override
    public int compareTo(Document o) {
        long difference = this.getLastUseTime() - o.getLastUseTime();
        if(difference == 0) return 0;
        if(difference > 0) return 1;
        else return -1;
    }


}
