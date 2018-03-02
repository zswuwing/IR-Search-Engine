package IR.task1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class Analyze {
    private List<String> STOP = new ArrayList<String>();
    private DataCenter fm = new DataCenter();

    public Analyze() {
        this.folderPath = fm.getAbsolutePath() + "/src/main/resources/Corpus";
        STOP = fm.genSTOP();
        this.analyze();
    }

    public Analyze(String sourcePath) {
        this.folderPath = sourcePath;
        STOP = fm.genSTOP();
        this.analyze();
    }

    public void analyze() {
        File file = new File(this.folderPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println(file.getName() + " is empty!");
                return;
            } else {
                for (File analysisFile : files) {
                    String[] content = this.read(analysisFile.getPath());
                    String fileName = analysisFile.getName().replaceAll("[.][^.]+$", "");
                    int documentLength = 0;
                    Integer wordLocation = 0;
                    for (String word : content) {
                        documentLength = documentLength + 1;
                        // Inverted index
                        Map<String, Integer> mapChild = new HashMap<>();
                        if (index.containsKey(word)) {
                            mapChild = index.get(word);
                        }
                        if (mapChild.containsKey(fileName)) {
                            Integer preNum = mapChild.get(fileName);
                            mapChild.put(fileName, preNum + 1);
                        } else {
                            mapChild.put(fileName, 1);
                        }
                        index.put(word, mapChild);

                        // Inverted index with location
                        Map<String, ArrayList<Integer>> mapLocatioinChild = new HashMap<>();
                        if (locationIndex.containsKey(word)) {
                            mapLocatioinChild = locationIndex.get(word);
                        }
                        ArrayList<Integer> update = new ArrayList<>();
                        update.add(wordLocation);
                        if (mapLocatioinChild.containsKey(fileName)) {
                            ArrayList<Integer> pre = mapLocatioinChild.get(fileName);
                            update.addAll(pre);
                        }
                        mapLocatioinChild.put(fileName, update);
                        locationIndex.put(word, mapLocatioinChild);

                        // Term Frequency
                        if (tf.containsKey(word)) {
                            Integer preNum = tf.get(word);
                            tf.put(word, preNum + 1);
                        } else {
                            tf.put(word, 1);
                        }

                        // Document Frequency
                        ArrayList<String> fileList = new ArrayList<>();
                        if (df.containsKey(word)) {
                            fileList = df.get(word);
                        }
                        if (!fileList.contains(fileName)) {
                            fileList.add(fileName);
                            df.put(word, fileList);
                        }
                        wordLocation = wordLocation + 1;
                    }
                    dl.put(fileName, documentLength);
                }
            }
        } else {
            System.out.println(this.folderPath + "Source : Folder doesn't exist!");
        }
    }

    static public String[] read(String filepath) {
        String[] content = null;
        File file = new File(filepath);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars).split(" ");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void storeInvertedIndex(String location) {
        System.out.print("Unigrams Store Start, please wait for second!\n");
        String res = "";
        int i = 0;
        List<Map.Entry<String, Map<String, Integer>>> list = new ArrayList<Map.Entry<String, Map<String, Integer>>>(this.index.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Integer>>>() {
            public int compare(Map.Entry<String, Map<String, Integer>> o1, Map.Entry<String, Map<String, Integer>> o2) {
                return new Double(o2.getValue().size()).compareTo(new Double(o1.getValue().size()));
            }
        });
        for (Map.Entry<String, Map<String, Integer>> entry : list) {
            i = i + 1;
            String word = entry.getKey();
            String line = word + " Index : ";
            Map<String, Integer> childMap = entry.getValue();
            ObjectMapper mapperObj = new ObjectMapper();
            try {
                String jsonStr = mapperObj.writeValueAsString(childMap);
                line = line + jsonStr;
            } catch (IOException e) {
                e.printStackTrace();
            }
            res = res + line + "\n";
            if(i%1000==0){
                System.out.print("Gen unigram index for "+i+" words!\n");
            }
        }
        fm.writeFileData(res, location);
        System.out.print("Unigrams Store in "+location+".\n");
    }

    public void storeEstimatedLanguageModels( String storepath) {
        File file = new File(this.folderPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println(file.getName() + " is empty!");
                return;
            } else {
                int process=0;
                for (File analysisFile : files) {
                    String[] content = this.read(analysisFile.getPath());
                    String fileName = analysisFile.getName().replaceAll("[.][^.]+$", "");
                    if(this.dl.containsKey(fileName)){
                        int dl=this.dl.get(fileName);
                        Map<String,Double> languageModel=new HashMap<>();
                        for (String word : content) {
                            if(!languageModel.keySet().contains(word)){
                                int occur=this.index.get(word).get(fileName);
                                languageModel.put(word,(double)occur/(double)dl);
                            }
                        }
                        String write="";
                        for(Map.Entry<String, Double> wordToScore : dc.stringToDoubleRank(languageModel)){
                            write+=wordToScore.getKey()+" "+wordToScore.getValue()+"\n";
                        }
                        dc.writeFileData(write,storepath+fileName+".txt");
                    }else{
                        System.out.print("Cannot find "+fileName+" in index!\n");
                        break;
                    }
                    process+=1;
                    if(process%100==0){
                        System.out.print("Store Language Model for "+process+" files!\n");
                    }
                }
            }
        } else {
            System.out.println(this.folderPath + "Language Model Source : Folder doesn't exist!");
        }
        System.out.print("Language Model Store in "+storepath+".\n");
    }

    public void print() {
        String res = "";
        List<Map.Entry<String, Map<String, Integer>>> list = new ArrayList<Map.Entry<String, Map<String, Integer>>>(this.index.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Integer>>>() {
            public int compare(Map.Entry<String, Map<String, Integer>> o1, Map.Entry<String, Map<String, Integer>> o2) {
                return new Double(o2.getValue().size()).compareTo(new Double(o1.getValue().size()));
            }
        });
        res = res + "Inverted index\n";
        for (Map.Entry<String, Map<String, Integer>> entry : list) {
            System.out.print(entry.getKey() + " " + entry.getValue().toString() + "\n");
            res = res + entry.getKey() + " " + entry.getValue().toString() + "\n";
        }

        List<Map.Entry<String, ArrayList<String>>> list1 = new ArrayList<Map.Entry<String, ArrayList<String>>>(this.df.entrySet());
        Collections.sort(list1, new Comparator<Map.Entry<String, ArrayList<String>>>() {
            public int compare(Map.Entry<String, ArrayList<String>> o1, Map.Entry<String, ArrayList<String>> o2) {
                return new Double(o2.getValue().size()).compareTo(new Double(o1.getValue().size()));
            }
        });
        res = res + "DF\n";
        for (Map.Entry<String, ArrayList<String>> entry : list1) {
            res = res + entry.getKey() + " " + entry.getValue().toString() + "\n";
        }

        List<Map.Entry<String, Integer>> list2 = new ArrayList<Map.Entry<String, Integer>>(this.dl.entrySet());
        Collections.sort(list2, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        res = res + "DL\n";
        for (Map.Entry<String, Integer> entry : list2) {
            res = res + entry.getKey() + " " + entry.getValue().toString() + "\n";
        }

        List<Map.Entry<String, Integer>> list3 = new ArrayList<Map.Entry<String, Integer>>(this.tf.entrySet());
        Collections.sort(list3, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        res = res + "TF\n";
        for (Map.Entry<String, Integer> entry : list3) {
            res = res + entry.getKey() + " " + entry.getValue().toString() + "\n";
        }

    }

    public Map<String, Integer> getTf() {
        return tf;
    }

    public Map<String, Map<String, Integer>> getIndex() {
        return index;
    }

    public Map<String, ArrayList<String>> getDf() {
        return df;
    }

    public Map<String, Integer> getDl() {
        return dl;
    }

    public Map<String, Map<String, ArrayList<Integer>>> getLocationIndex() {
        return locationIndex;
    }

    private String folderPath;
    private Map<String, Map<String, Integer>> index = new HashMap<>();
    private Map<String, Map<String, ArrayList<Integer>>> locationIndex = new HashMap<>();
    private Map<String, Integer> tf = new HashMap<>();
    private Map<String, ArrayList<String>> df = new HashMap<>();
    private Map<String, Integer> dl = new HashMap<>();
    private DataCenter dc=new DataCenter();
}
