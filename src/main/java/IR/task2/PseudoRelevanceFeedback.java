package IR.task2;

import IR.task1.*;
import IR.ModelEnum;
import IR.task1.engine.BM25;
import IR.task1.engine.EngineFactory;
import IR.task1.engine.Tf_Idf;

import java.util.*;
import java.util.regex.Pattern;

public class PseudoRelevanceFeedback {
    private Integer topDocNum;
    private Integer topTermNum;
    private EngineFactory factory;
    private Map<String, Double> score;
    private String corpusPath = "/src/main/resources/Corpus/";
    private String task2BM25Path = "/src/main/resources/task2/BM25Query/";
    private String task2TfIdfPath = "/src/main/resources/task2/TfIdfQuery/";

    public PseudoRelevanceFeedback(EngineFactory factory, Integer topDocNum, Integer topTermNum) {
        this.topDocNum = topDocNum;
        this.topTermNum = topTermNum;
        this.factory = factory;
    }

    public void autoSearch(List<String> res, Map.Entry<Integer, String> queryEntry, ModelEnum model, String systemName) {
        DataCenter dc = new DataCenter();
        if (model == ModelEnum.BM25 || model == ModelEnum.TF_IDF) {
            Map<String, Integer> tf = getTopTf(res);
            List<Map.Entry<String, Integer>> topTf = new ArrayList<>(tf.entrySet());
            Collections.sort(topTf, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            String expandedQuery = queryEntry.getValue();
            for (Map.Entry<String, Integer> entry : topTf.subList(0, Math.min(topTf.size(), this.topTermNum))) {
                expandedQuery += " " + entry.getKey();
            }
            if (model == ModelEnum.BM25) {
                if (!systemName.equals("")) {
                    this.score = this.factory.bm25SearchWithStore(expandedQuery, queryEntry.getKey(), dc.getAbsolutePath() + task2BM25Path, systemName);
                } else {
                    this.score = this.factory.bm25SearchWithStore(expandedQuery, queryEntry.getKey(), "","");;
                }
            } else {
                if (!systemName.equals("")) {
                    this.score = this.factory.tfidfSearchWithStore(expandedQuery, queryEntry.getKey(), dc.getAbsolutePath() + task2TfIdfPath, systemName);
                } else {
                    this.score = this.factory.tfidfSearch(expandedQuery);
                }
                ;
            }
        }
    }

    private Map<String, Integer> getTopTf(List<String> res) {
        Map<String, Integer> tf = new HashMap<String, Integer>();
        DataCenter dc = new DataCenter();
        List<String> stopList = dc.genSTOP();
        for (String doc : res.subList(0, Math.min(res.size(), this.topDocNum))) {
            String[] content = Analyze.read(dc.getAbsolutePath() + corpusPath + "/" + doc + ".txt");
            for (String term : content) {
                if (stopList.contains(term) || isNumeric(term)) {
                    continue;
                }
                if (!tf.containsKey(tf)) {
                    tf.put(term, 1);
                } else {
                    tf.put(term, tf.get(term) + 1);
                }
            }
        }
        return tf;
    }

    static public List<String> rankFromMap(Map<String, Double> score) {
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(score.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<String> res = new ArrayList<String>();
        for (Map.Entry<String, Double> entry : list.subList(0, Math.min(list.size(), 100))) {
            res.add(entry.getKey());
        }
        return res;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public Map<String, Double> getScore() {
        return score;
    }
}
