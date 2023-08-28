package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import javax.print.Doc;
import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private static final int alphabetSize = 62;
    private Set<Value> searcherSet = new HashSet<>();
    private Value tempValue = null;
    private Set<String> words = new HashSet<>();
    private Node root;

    private int convert(char c){

        int i = (int)c;
        if(i >= 48 && i <= 57){
            return i-48;
        }
        if(i >= 65 && i <= 90){
            return i-55;
        }
        if(i >= 97 && i <= 122){
            return i-61;
        }
     return -1;

    }

    private char revert(int i){
        if(i < 10)      return (char)(i+48);
        else if(i < 36) return (char)(i+55);
        else            return (char)(i+61);
    }

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val){
        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }

    /**
     * @param x
     * @param key
     * @param val
     * @param d
     * @return
     */
    private Node put(Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            //Add this DocumentImpl to the set
            x.addValue(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        int c = convert(key.charAt(d));
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
        Node x = this.get(this.root, key, 0);

        if (x == null)
        {
            return Collections.emptyList();
        }

        List<Value> list = new ArrayList<>();

        for(Object v : x.val){
            list.add((Value) v);
        }

        Collections.sort(list, comparator);

        return list;
    }

    /**
     * A char in java has an int value.
     * see http://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#getNumericValue-char-
     * see http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
     */
    private Node get(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        int c = convert(key.charAt(d));
        return this.get(x.links[c], key, d + 1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        searcherSet = new HashSet<>();

        //Find Root
        Node rootOfPrefix = get(root, prefix, 0);

        //Collect whole tree into searcherSet
        getAllWithPrefixSorted(rootOfPrefix, prefix);
        //Now words holds all words in the trie with the prefix
        //Create a list of all documents with all of those prefixes

        //Sort set into list
        List<Value> list = new ArrayList<>(searcherSet);
        System.out.println("CONTAINS NULL?: " +searcherSet.contains(null));
        Collections.sort(list, comparator);
        return list;
    }

    private void getAllWithPrefixSorted(Node x, String str){
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return;
        }

        if(x.val.size() > 0){
            for(Object v: x.val) {
                if(v!=null) searcherSet.add((Value) v);
            }
        }

        for(int i = 0; i < alphabetSize; i++){
            if(x.links[i] != null) {
                String add = str+revert(i);
                getAllWithPrefixSorted(x.links[i], add);
            }
        }

    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){
        searcherSet = new HashSet<>();
        root =  deleteAllWithPrefix(root,prefix,0);
        return searcherSet;
    }

    private Node deleteAllWithPrefix(Node x, String key, int d){

        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            clearSubtree(x);
        }
        //continue down the trie to the target node
        else
        {
            int c = convert(key.charAt(d));
            x.links[c] = this.deleteAllWithPrefix(x.links[c], key, d + 1);
        }

        //this node has a val – do nothing, return the node
        if (x.val != null && x.val.size() > 0)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;

    }

    private void clearSubtree(Node x){
        if(x == null){
            return;
        }

        if(x.val.size() > 0){
            for(Object v: x.val) {
                searcherSet.add((Value) v);
            }
            x.val = null;
        }

        for(int i = 0; i < alphabetSize; i++){
            if(x.links[i] != null) clearSubtree(x.links[i]);
        }

        x = null;

    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){
        searcherSet = new HashSet<>();
        root = deleteAll(root,key,0);
        return Collections.unmodifiableSet(searcherSet);
    }

    private Node deleteAll(Node x, String key, int d)
    {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
          searcherSet = x.val;
          x.val = null;
        }
        //continue down the trie to the target node
        else
        {
            int c = convert(key.charAt(d));
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }

        //this node has a val – do nothing, return the node
        if (x.val != null && x.val.size() > 0)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){
        tempValue = null;
        root = delete(root, key, 0, val);
        return tempValue;
    }

    private Node delete(Node x, String key, int d, Value val)
    {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            if (x.val.remove(val)) tempValue = val;

        }
        //continue down the trie to the target node
        else
        {
            int c = convert(key.charAt(d));
            x.links[c] = this.delete(x.links[c], key, d + 1, val);
        }
        //this node has a val – do nothing, return the node
        if (x.val != null && x.val.size() > 0)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    static class Node<Value>
    {
        protected Set<Value> val = new HashSet<>();
        protected Node[] links = new Node[alphabetSize];
        private int index;

        public void addValue(Value v){
            val.add(v);
        }

    }

}
