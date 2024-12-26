package com.mySpace.user.services;

import java.util.ArrayList;
import java.util.List;


public class Trie {
    private static final int MAX_CHAR = 36;
    private Trie[] children;
    private boolean isLeaf;
    private String storedWord;

    public Trie() {
        children = new Trie[MAX_CHAR];
        isLeaf = false;
        storedWord = null;
    }


    private int getCharIndex(char c) {

        if (Character.isLetter(c)) {
            return Character.toLowerCase(c) - 'a';
        } else if (Character.isDigit(c)) {
            return 26 + (c - '0');
        } else {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
    }

    public void insert(String word) {
        Trie current = this;

        word = word.replace(" ", "");

        for (char c : word.toLowerCase().toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException("Words can only contain letters and numbers: " + word);
            }
            int index = getCharIndex(c);
            if (current.children[index] == null) {
                current.children[index] = new Trie();
            }
            current = current.children[index];
        }
        current.isLeaf = true;
        current.storedWord = word;
    }

    public List<String> getWordsWithPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        Trie current = this;
        prefix = prefix.replace(" ", "");

        for (char c : prefix.toLowerCase().toCharArray()) {
            int index = getCharIndex(c);
            if (current.children[index] == null) {
                return results; // No words with this prefix
            }
            current = current.children[index];
        }

        collectAllWords(current, results);
        return results;
    }

    private void collectAllWords(Trie node, List<String> results) {
        if (node == null) return;

        if (node.isLeaf) {
            results.add(node.storedWord);
        }

        for (Trie child : node.children) {
            collectAllWords(child, results);
        }
    }
}
