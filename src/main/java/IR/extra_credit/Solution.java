package IR.extra_credit;

import IR.task1.Analyze;
import IR.task1.DataCenter;
import IR.task1.DirCheck;
import IR.task1.GenerateCorpus;
import IR.task1.engine.EngineFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by Radu on 12/5/17.
 */
public class Solution {
    private Map<String, Map<String, ArrayList<Integer>>> Inverted_index;
    private Map<String, Map<Integer, String>> DocsWordOrder;
    private Map<String, String> Docs;

    public Solution(Map<String, Map<String, ArrayList<Integer>>> index) {
        this.Inverted_index = index;
        this.Docs = new HashMap<>();
        this.DocsWordOrder = new HashMap<>();
    }

    public Solution() {
    }


    public Map<String, String> indexToOriOrder(Map<String, Map<String, ArrayList<Integer>>> index) {


        int totalTermNum = 0;
        for (String term : index.keySet()) {
            totalTermNum += index.get(term).size();
        }

        for (String term : index.keySet()) {
            for (String docName : index.get(term).keySet()) {
                if (!DocsWordOrder.keySet().contains(docName)) {
                    DocsWordOrder.put(docName, new HashMap<Integer, String>());
                    Docs.put(docName, "");
                }

                for (Integer place : index.get(term).get(docName)) {
                    Map<Integer, String> one = DocsWordOrder.get(docName);
                    if (one.containsKey(place)) {
                        System.out.println("Have same location word");
                        //return;
                    }
                    one.put(place, term);
                    DocsWordOrder.put(docName, one);
                }
            }
        }


        for (String docName : DocsWordOrder.keySet()) {
            int dl = DocsWordOrder.get(docName).size();
            String doc = "";
            for (int i = 0; i < dl; i++) {
                if (!DocsWordOrder.get(docName).containsKey(i)) {
                    System.out.println("Have missed word");
                }
                doc += DocsWordOrder.get(docName).get(i) + " ";
            }
            Docs.put(docName, doc);
        }

        return Docs;
    }

    public Map<String, Map<Integer, String>> getDocsWordOrder() {
        return DocsWordOrder;
    }

    public TreeMap<String, Double> scoreOfDoc(Map<String, Map<Integer, String>> WordOrder, String query, Map<String, Double> tf_Idf_Score) {
        String[] one = query.split(" ");

        List<String> queries = new ArrayList<>();
        for (int i = 0; i < one.length; i++) {
            queries.add(one[i]);
        }
        TreeMap<String, Double> ScoreOfDocs = new TreeMap<>();
        int index_of_last_query = 0;
        int place_of_last_query = 0;

        //Map<String,List<Integer>> one = new HashMap<>();
        for (String docName : WordOrder.keySet()) {
            int dl = WordOrder.get(docName).size();
            double proximScore = 0.0;
            for (int i = 0; i < dl; i++) {
                String curWord = WordOrder.get(docName).get(i);
                if (queries.contains(curWord)) {
                    if (i - place_of_last_query <= 3) {
                        if (queries.indexOf(curWord) - index_of_last_query > 0) {
                            proximScore++;
                        }
                        place_of_last_query = i;
                        index_of_last_query = queries.indexOf(curWord);
                    } else {
                        proximScore = 0.0;
                        break;
                    }
                }
            }
            ScoreOfDocs.put(docName, proximScore + tf_Idf_Score.get(docName));
        }
        return ScoreOfDocs;
    }

    //public void convertToDoc


    public static void main() {
        //Solution helper = new Solution();
        //HashMap<String,TreeMap<String,Double>> whole_table = new HashMap<>();
        TreeMap<String, Double> scoreOfDocs = new TreeMap<String, Double>();
        Map<String, TreeMap<String, Double>> totalScoreOfDocs = new HashMap<>();
        DataCenter dc = new DataCenter();
        DirCheck dr = new DirCheck();
        String MySystemName = "Extra_Credit";
        HashMap<Integer, String> queries = dc.genQuery();
        String sourcePath = dc.getAbsolutePath() + "/src/main/resources/Source/";
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Do you want to Stopping the corpus? Extract_Credit(y/n) :\n");
        String tfidfTablePath = dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/TfIdfQuery/";
        //String s = scanner.nextLine();

        boolean goOrnot = true;
        //Scanner scanner = new Scanner(System.in);
        String s = "";
        //System.out.print("Do you want to Stopping the corpus? Extract_Credit(y/n) :\n");
        Scanner scanner = new Scanner(System.in);
        while (s != "q") {
            System.out.print("Do you want to Stopping the corpus? Extract_Credit(y/n) :\n");
            System.out.print("q for exit :\n");
            s = scanner.nextLine();
            if (s.equalsIgnoreCase("y")) {
                GenerateCorpus gc = new GenerateCorpus(sourcePath,dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/StopCorpus/");
                gc.generate(true, true, true);
                Analyze a = new Analyze();
                //a.analyze();
                Solution main = new Solution(a.getLocationIndex());
                main.indexToOriOrder(a.getLocationIndex());
                String systemName = "TFIDFStopCaseFRun";
                MySystemName += MySystemName = "StopRun";
                EngineFactory algorithm = new EngineFactory(a);
                for (Map.Entry<Integer, String> entry : queries.entrySet()) {
                    scoreOfDocs = new TreeMap<>();
                    int id = entry.getKey();
                    //EngineFactory algorithm = new EngineFactory(a);
                    scoreOfDocs = main.scoreOfDoc(main.getDocsWordOrder(), entry.getValue(),
                            algorithm.tfidfSearchWithStore(entry.getValue(), id, tfidfTablePath, systemName));
                    totalScoreOfDocs.put(entry.getValue(), scoreOfDocs);
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
                            scoreOfDocs.entrySet());
                    Collections.sort(list, new Comparator<Entry<String, Double>>() {
                        public int compare(Entry<String, Double> o1,
                                           Entry<String, Double> o2) {
                            return -o1.getValue().compareTo(o2.getValue());
                        }
                    });
                    String content = "";
                    int i = 1;
                    for (Map.Entry<String, Double> mapping : list) {
                        if (i > 100) {
                            break;
                        }
                        content +=
                                i + ". " + "Doc:    " + mapping.getKey() + "     " + mapping.getValue() + "    "
                                        + MySystemName + "\n";
                        i++;
                    }
                    dc.writeFileData(content,
                            dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/resultOfStop/" + "#" + id
                                    + "StopResult.txt");

                }
            } else if (s.equalsIgnoreCase("n")) {
                GenerateCorpus gc =  new GenerateCorpus(sourcePath,dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/NonStopCorpus/");;
                gc.generate(true, true, false);
                Analyze a = new Analyze();
                //a.analyze();
                EngineFactory algorithm = new EngineFactory(a);
                Solution main = new Solution(a.getLocationIndex());
                main.indexToOriOrder(a.getLocationIndex());
                String systemName = "TFIDFNoStopCaseFRun";
                MySystemName += MySystemName = "NonStopRun";
                for (Map.Entry<Integer, String> entry : queries.entrySet()) {
                    int id = entry.getKey();
                    //EngineFactory algorithm = new EngineFactory(a);
                    scoreOfDocs = main.scoreOfDoc(main.getDocsWordOrder(), entry.getValue(),
                            algorithm.tfidfSearchWithStore(entry.getValue(), id, tfidfTablePath, systemName));
                    totalScoreOfDocs.put(entry.getValue(), scoreOfDocs);
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
                            scoreOfDocs.entrySet());
                    Collections.sort(list, new Comparator<Entry<String, Double>>() {
                        public int compare(Entry<String, Double> o1,
                                           Entry<String, Double> o2) {
                            return -o1.getValue().compareTo(o2.getValue());
                        }
                    });
                    String content = "";
                    int i = 1;
                    for (Map.Entry<String, Double> mapping : list) {
                        if (i > 100) {
                            break;
                        }
                        content +=
                                i + ". " + "Doc:    " + mapping.getKey() + "     " + mapping.getValue() + "    "
                                        + MySystemName + "\n";
                        i++;
                    }
                    dc.writeFileData(content,
                            dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/resultOfNonStop/" + "#" + id
                                    + "NonStopResult.txt");
                }
            } else if (s.equals("q")) {
                break;
            } else {
                System.out.println("Wrong Input, input again, Please");
            }
        }
    }
}
