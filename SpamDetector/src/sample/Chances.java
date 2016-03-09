package sample;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Chances {
    private Map<String, Double> hamW;
    private Map<String, Double> spamW;


    public Chances(Map<String, Double> hamW, Map<String,Double> spamW) {
        this.hamW = hamW;
        this.spamW = spamW;
    }


    public Map<String,Double> spamChance(Map<String,Double>hamW, Map<String,Double>spamW){ // keeps iterating through and determines if its spam or ham
        Map<String, Double> spam = new TreeMap<>();
        Set<String> keys = spamW.keySet();
        Iterator<String> Iterator = keys.iterator();
        String word;
        while (Iterator.hasNext()) {
            word = Iterator.next();
            if (!hamW.containsKey(word)) {
                spam.put(word, spamW.get(word) / spamW.get(word));
            } else {
                spam.put(word, spamW.get(word) / (spamW.get(word) + hamW.get(word)));
            }

        }
        return spam;
    }
}