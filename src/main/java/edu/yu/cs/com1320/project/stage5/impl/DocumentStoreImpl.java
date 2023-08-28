package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {

    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    private StackImpl<Undoable> commands;

    private BTreeImpl<URI,Document> bTree;

    private MinHeapImpl<HeapNode> heap2;


    private TrieImpl<URI> trie2;

    private int docLimit = Integer.MAX_VALUE;
    private int byteLimit = Integer.MAX_VALUE;
    private int docCount = 0;
    private int byteCount = 0;

    public DocumentStoreImpl() {
        this.commands = new StackImpl<>();
        this.trie2 = new TrieImpl<>();
        this.heap2 = new MinHeapImpl<>();
        // Initialize the BTree and the Persistence Manager
        this.bTree = new BTreeImpl<>();
        DocumentPersistenceManager pm = new DocumentPersistenceManager(new File(System.getProperty("user.dir")));
        bTree.setPersistenceManager(pm);
    }

    /**
     * Constructor accepts a base directory for initializing the persistence manager
     */
    public DocumentStoreImpl(File baseDir){
        this.commands = new StackImpl<>();
        this.trie2 = new TrieImpl<>();
        this.heap2 = new MinHeapImpl<>();
        // Initialize the BTree and the Persistence Manager
        this.bTree = new BTreeImpl<>();
        DocumentPersistenceManager pm = new DocumentPersistenceManager(baseDir);
        bTree.setPersistenceManager(pm);
    }

    public int put(InputStream input, URI uri, DocumentStore.DocumentFormat format) throws IOException {
        if (uri == null || format == null) throw new IllegalArgumentException("URI or format is null");

        if (input == null) {          //Delete the key of given URI
            DocumentImpl di = (DocumentImpl) bTree.get(uri);    //Retrieve document from table;
            if (di == null) return 0;  //Return 0 if doc not found; can't delete a doc thats not there
            delete(di.getKey());
            return di.hashCode();               //Return Hashcode of doc
        }

        //Since the input wasn't null, a new document is being created and might override an existing document
        //oldDoc is the overridden document
        byte[] inputData = readData(input);  //Read input into document
        if(inputData.length > byteLimit) throw new IllegalArgumentException("Document is too large");
        DocumentImpl doc = createDocument(format, uri, inputData); //Create Document with provided URI and format
        doc.setLastUseTime(System.nanoTime());
        DocumentImpl oldDoc = (DocumentImpl) bTree.put(uri, doc);
        addDocumentToTrie(doc);                         //Add Document to Trie
        addDocumentToHeap(doc);


        //Update Time
        // ADDING COMMAND
        // if doc is null: new document was added: undo() will function by deleting new document.
        // if doc is not null: a document was overwritten: undo() will function by replace old document.
        commands.push(generateCommand(uri, oldDoc));

        //Return
        if (oldDoc == null) return 0; //Created new Document
        return Math.abs(oldDoc.hashCode()); //OVERWROTE DOCUMENT
    }

    private byte[] readData(InputStream is) throws IOException {
        byte[] inputData;
        try {
            inputData = is.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Problem Reading InputStream");
        }

        return inputData;
    }

    private DocumentImpl createDocument(DocumentStore.DocumentFormat format, URI uri, byte[] data) {
        DocumentImpl d = null;
        if (format == DocumentFormat.BINARY) return new DocumentImpl(uri, data);
        if (format == DocumentFormat.TXT) return new DocumentImpl(uri, new String(data));
        updateLastTimeUsed(d);
        return d;
    }

    //TODO add document to heap when called in from disk
    @Override
    public Document get(URI uri) {
        Document d = bTree.get(uri);
        //add doc to heap if not currently in the heap
        if(d!=null) {
            URI wasInHeap = null;
            try {
                d.setLastUseTime(0); //by setting the docs
                heap2.reHeapify(new HeapNode(d.getKey(), d.getLastUseTime()));
                wasInHeap = heap2.remove().uri;

            } catch (Exception e) {}
            if (wasInHeap != null) {
                d.setLastUseTime(System.nanoTime());
                heap2.insert(new HeapNode(uri, d.getLastUseTime()));
                System.out.println("was in heap not memory");
            } else {
                //document was in memory so update count and regulate memory
                System.out.println("was in memory not heap");
                d.setLastUseTime(System.nanoTime());
                addDocumentToHeap((DocumentImpl) d);
            }
        }
        return d;
    }

    @Override
    public boolean delete(URI uri) {
        DocumentImpl doc =(DocumentImpl) bTree.get(uri);     //Remove this key from Hashmap
        System.out.println("is it null: " + doc);
        if (doc != null) {
            removeDocFromHeap(doc);
            commands.push(generateCommand(uri, doc)); //
            for (String word : doc.getWords()) {
                trie2.delete(word, uri);
            }
        }
        return doc != null;  //If d == null then the key was never present so return false.
    }

    @Override
    public void undo() throws IllegalStateException {
        System.out.println("Undoing command");
        if (commands.size() == 0) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");
        Undoable lastCommand = commands.pop();

        //undo the commandSet
        //getAll of the
        if (lastCommand == null) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");
        //In order to reset time of the undone documents collect them here and then reset time after its undone
        if(lastCommand instanceof CommandSet){
            CommandSet<URI> cs = ((CommandSet) lastCommand);

            //Collect URI's of CommandSet
            Set<URI> uriSet = new HashSet<>();
            Iterator<GenericCommand<URI>> iterator = cs.iterator();
            while(iterator.hasNext()){
                GenericCommand<URI> command = iterator.next();
                uriSet.add(command.getTarget());
            }

            long updatedTime = System.nanoTime();
            if (!cs.undo()) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");
            if(!cs.isEmpty()) commands.push(lastCommand);
            for (URI uri: uriSet) {
                if(get(uri) != null) get(uri).setLastUseTime(updatedTime);
            }
        }
        else{
            GenericCommand<URI> g = ((GenericCommand<URI>) lastCommand);
            URI uri = g.getTarget();
            if(!g.undo()) throw new IllegalStateException("ERROR: undo unable to be performed");
            get(uri); //This will update the time
        }
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        System.out.println("Undoing command");
        if (commands.size() == 0) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");

        //Create helper stack to temporarily remove commands
        StackImpl<Undoable> helper = createHelperStack(uri);
        Undoable foundURI = commands.pop();

        if (foundURI == null) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");

//      if foundURI is a generic command, then undo it. if it is a CommandSet then undo only the specified uri
        if(foundURI instanceof GenericCommand){
            GenericCommand<URI> g = ((GenericCommand<URI>) foundURI);
            if(!g.undo()) throw new IllegalStateException("ERROR: undo unable to be performed");
            get(uri); //Not sure if this is undoing the undo by recalling it into memory
        }
        else{
            CommandSet<URI> cs = ((CommandSet) foundURI);
            if (!cs.undo(uri)) throw new IllegalStateException("ERROR: undo unable to be performed on given URI");
            get(uri); //Update time
            if(!cs.isEmpty()) {
                commands.push(foundURI);
            }
        }
        while(helper.size() > 0) commands.push(helper.pop());
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        List<URI> list = trie2.getAllSorted(keyword, new DocumentComparator(keyword, true));
        List<Document> docList = new ArrayList<>();
        long nanoTime = System.nanoTime();
        for(URI uri: list) {
            Document d = bTree.get(uri);
            if(d!=null) {
                docList.add(d);
                updateLastTimeUsed((DocumentImpl) d, nanoTime);
            }
        }
        return docList;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        List<URI> list =  trie2.getAllWithPrefixSorted(keywordPrefix, new DocumentComparator(keywordPrefix, false));
        List<Document> docList = new ArrayList<>();
        long nanoTime = System.nanoTime();
        for(URI uri: list) {
            Document d = bTree.get(uri);
            if(d != null) {
                docList.add(d);
                updateLastTimeUsed((DocumentImpl) d, nanoTime);
            }
        }
        return docList;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    // TODO check to make sure this works properly. I dont see how it deletes
    @Override
    public Set<URI> deleteAll(String keyword) {
        List<URI> di = trie2.getAllSorted(keyword, new DocumentComparator(keyword, false));
        Set<URI> uriSet = new HashSet<>();

        //Remove any trace of the document by deleting doc from the node of every word
        //Add Uri to list to create undo functions
        for(URI uri : di) {
            uriSet.add(uri);
        }
        generateAndAddCommandSet(uriSet);
        return uriSet;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    // TODO check to see if this works. How does it delete?
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        List<URI> di = trie2.getAllWithPrefixSorted(keywordPrefix, new DocumentComparator(keywordPrefix, false));
        Set<URI> uriSet = new HashSet<>();
        for(URI uri : di) {
            uriSet.add(uri);
        }
        //Generate Commands with same time Stamp
        generateAndAddCommandSet(uriSet);
        return uriSet;
    }

    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit){
        this.docLimit = limit;
        regulateMemory();
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit){
        this.byteLimit = limit;
        regulateMemory();
    }

    private void generateAndAddCommandSet(Set<URI> uriSet){
        //undo this by re-adding everything to the table and trie
        //Every document gets the same time stamp of nanoTime
        CommandSet<URI> cs = new CommandSet<>();
        long nanoTime = System.nanoTime();
        for(URI uri : uriSet){
            DocumentImpl doc =(DocumentImpl) bTree.get(uri);
            delete(uri);
            cs.addCommand(generateCommand(uri,doc,nanoTime));
        }
        if(cs.size() > 0) commands.push(cs);
    }

    private StackImpl<Undoable> createHelperStack(URI uri){
        StackImpl<Undoable> helper = new StackImpl<>();

        while (commands.size() > 0) {
            if (commands.peek() instanceof GenericCommand) {
                GenericCommand<URI> gc = (GenericCommand) commands.peek();
                if (gc.getTarget().equals(uri)) break;
            }
            if (commands.peek() instanceof CommandSet) {
                CommandSet cs = (CommandSet) commands.peek();
                if (cs.containsTarget(uri)) break;
            }
            helper.push(commands.pop());
        }
        return helper;
    }

    private void updateLastTimeUsed(DocumentImpl doc){
        doc.setLastUseTime(System.nanoTime());
        heap2.reHeapify(new HeapNode(doc.getKey(), doc.getLastUseTime()));
    }
    //Same method with option to pass in nanoTime. This is used when multiple documents are being edited in order to ensure
    //they all have the same timestamp.
    private void updateLastTimeUsed(DocumentImpl doc, long nanoTime){
        doc.setLastUseTime(nanoTime);
        heap2.reHeapify(new HeapNode(doc.getKey(), doc.getLastUseTime()));
    }

    private void addDocumentToTrie(DocumentImpl d) {
        if(d.getDocumentTxt() == null) return;
        Set<String> wordsInDocument = d.getWords();
        for(String word: wordsInDocument) {
            trie2.put(word, d.getKey());
        }
    }

    private void addDocumentToHeap(DocumentImpl doc){
        Function f = generateFunction();
        HeapNode node = new HeapNode(doc.getKey(),doc.getLastUseTime());
        heap2.insert(node);
        updateCount(doc, true);
    }

    private void updateCount(Document doc, boolean add){
        int bytes = (doc.getDocumentTxt() != null ?  doc.getDocumentTxt().getBytes().length
                                                   : doc.getDocumentBinaryData().length);
        System.out.println("Regulating memory on " + doc.getDocumentTxt());
        if(add){
            docCount++;
            System.out.println("DocCount: " + docCount);
            byteCount+=bytes;
            regulateMemory();
        }
        else{
            docCount--;
            byteCount-=bytes;
        }
    }
    private void regulateMemory(){
        while(docCount > this.docLimit || byteCount > byteLimit){
            //Delete the least used documents
            System.out.println("Greater than limit");
            URI uri = heap2.remove().uri;
            Document d = bTree.get(uri);
            //System.out.println("Doc getting deleted: " + d.getDocumentTxt());
            try{
                bTree.moveToDisk(uri);
                System.out.println("Document moved to Disk");
            }
            catch(Exception e){
                System.out.println("Item could not be moved to Disk");
            }
            updateCount(d,false);
        }
    }

    //Remove a specific document from the heap
    private void removeDocFromHeap(Document doc) {
        System.out.println();
        try{
            doc.setLastUseTime(0); //by setting the docs
            heap2.reHeapify(new HeapNode(doc.getKey(), doc.getLastUseTime()));

            // Instead of deleting a document, instead send it to disk
            URI sendToDisk = heap2.remove().uri;
            bTree.moveToDisk(sendToDisk);
            updateCount(doc, false);
        }
        catch (Exception e){
            System.out.println("Error: Item either doesn't exist or can't be sent to disk");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private GenericCommand<URI> generateCommand(URI uri, DocumentImpl doc){
        Function<URI, Boolean> f = u -> {
            if(doc == null && bTree.get(uri) == null) {
                return false;
            }
            bTree.put(u, doc);
            // If doc is not null then a new document was added
            if(doc != null) {
                addDocumentToTrie(doc);
                doc.setLastUseTime(System.nanoTime());
                addDocumentToHeap(doc);
            }
            return true;
        };
        return new GenericCommand<URI>(uri, f);
    }

    private GenericCommand<URI> generateCommand(URI uri, DocumentImpl doc, long nanoTime){
        Function<URI, Boolean> f = u -> {
            if(doc == null && bTree.get(uri) == null) {
                return false;
            }
            bTree.put(u, doc);
            if(doc != null) {
                addDocumentToTrie(doc);
                //THESE TWO LINES ADDED
                doc.setLastUseTime(nanoTime);
                addDocumentToHeap(doc);
            }

            return true;
        };
        return new GenericCommand<URI>(uri, f);
    }

    private Function<URI,Document> generateFunction(){
        Function<URI,Document> f = u -> bTree.get(u);
        return f;
    }


    class DocumentComparator implements Comparator<URI>{
        String word = null;
        boolean single;
        public DocumentComparator(String word, boolean single){
            this.word = word;
            this.single = single;
        }

        @Override
        public int compare(URI doc1, URI doc2) {
            if(bTree.get(doc1) == null) return -1;
            if(bTree.get(doc2) == null) return 1;
            if(single) return compareSingle(doc1, doc2);
            else       return comparePlural((DocumentImpl) bTree.get(doc1),(DocumentImpl) bTree.get(doc2));
        }

        public int compareSingle(URI doc1, URI doc2) {
            int wordCountDoc1 = (bTree.get(doc1).getWords().contains(word)) ? bTree.get(doc1).wordCount(word) : 0;
            int wordCountDoc2 = (bTree.get(doc2).getWords().contains(word)) ? bTree.get(doc2).wordCount(word) : 0;

            if (wordCountDoc1 >= wordCountDoc2) return -1;
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
                if(s.length() >= word.length() &&  s.substring(0,word.length()).equals(s)){
                    count += doc.wordCount(s);
                }
            }
            return count;
        }
    };

    private static final class HeapNode implements Comparable<HeapNode> {

        private URI uri;
        private long time;
        private Function<URI,Document> function;
        public HeapNode(URI uri, Function f){
            this.uri = uri;
            this.function = f;
        }
        public HeapNode(URI uri, long time){
            this.time = time;
            this.uri = uri;
        }

        @Override
        public boolean equals(Object other){
            if (this == other){
                return true;
            }
            if (!(other instanceof HeapNode)){
                return false;
            }
            HeapNode heapNode = (HeapNode) other;
            return this.uri.equals(heapNode.uri);
        }


        @Override
        public int compareTo(HeapNode other){
            long difference = this.time - other.time;
            if(difference == 0) return 0;
            if(difference > 0) return 1;
            else return -1;
        }

        @Override
        public String toString(){
            return this.uri.toString();
        }
    }
}


