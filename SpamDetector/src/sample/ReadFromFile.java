package sample;

import java.io.*;
import java.util.*;

/**
 * Created by 100490365 on 3/7/2016.
 */
public class ReadFromFile {
    private Map<String, Integer> trainingFreq;
    private String fileName;

    public ReadFromFile(String fileName, Map<String, Integer> trainingFreq){
        this.fileName = fileName;
        this.trainingFreq = trainingFreq;

    }

    public Map<String, Integer> read(File dataFolder, Map<String,Integer> trainingFreq){
        Map<String, Integer> frequency = new TreeMap<>();
        try{
            String word;
            for (File fileList : dataFolder.listFiles()){
                Scanner scan = new Scanner(fileList);
                while(scan.hasNext()){  // reads all the words in the file
                    word = scan.next();
                    if (checkWord(word)){   // checks if its a word
                        if(!frequency.containsKey(word)){
                            frequency.put(word,1);
                        }else{
                            frequency.put(word, frequency.get(word)+1);
                        }

                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return frequency;
    }
    private boolean checkWord(String s){
        String arrangement = "^[a-zA-Z]*$";
        if (s.matches(arrangement)){
            return true;
        }
        return false;
    }


    public Map<String, Double> getchances(Map<String,Integer> map, int files){
        Map<String, Double> chances = new TreeMap<>();
        Set<String> keys = map.keySet();
        Iterator<String> Iterator = keys.iterator();
        String word;
        while (Iterator.hasNext()) {
            word = Iterator.next();
            chances.put(word, map.get(word) / (double) files);
        }
        return chances;
    }

}