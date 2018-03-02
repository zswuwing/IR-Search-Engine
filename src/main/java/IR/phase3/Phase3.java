package IR.phase3;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;
import IR.task1.engine.*;
import IR.task2.PseudoRelevanceFeedback;

import java.io.IOException;
import java.util.*;

public class Phase3 {
    public static void main() throws IOException {
        Evaluation e=new Evaluation();
        DataCenter dc=new DataCenter();
        Analyze a;
        EngineFactory factory;
        String bm25Eva = dc.getAbsolutePath() + "/src/main/resources/Phase3/BM25Query/";
        String bm25PesEva = dc.getAbsolutePath() + "/src/main/resources/Phase3/BM25PesQuery/";
        String luenceEva = dc.getAbsolutePath() + "/src/main/resources/Phase3/LuceneQuery/";
        String tfidfEva = dc.getAbsolutePath() + "/src/main/resources/Phase3/TfIdfQuery/";
        String tfidfPesEva = dc.getAbsolutePath() + "/src/main/resources/Phase3/TfIdfPesQuery/";
        String lhEva = dc.getAbsolutePath() + "/src/main/resources/Phase3/LikelihoodQuery/";
        String task3CorpusPath =  dc.getAbsolutePath() + "/src/main/resources/Task3Corpus/";
        String bm25EvaWithStop = dc.getAbsolutePath() + "/src/main/resources/Phase3/BM25QueryWithStop/";
        String tfidfEvaWithStop = dc.getAbsolutePath() + "/src/main/resources/Phase3/TfIdfQueryWithStop/";
        String lhEvaWithStop = dc.getAbsolutePath() + "/src/main/resources/Phase3/LikelihoodQueryWithStop/";
        Integer topDocNum = 20;
        Integer topTermNum = 10;
        while (true) {
            System.out.println("Notice : You are using corpus generating in Indexing and Retrieval\n" +
                    "Phase 3 Instruction：\n 1. Update evaluation for BM25 Query\n " +
                    "2. Update evaluation for TF IDF Query table \n 3. Update evaluation for Likelihood Model " +
                    "table \n 4. Update evaluation for Lucene Query table\n 5. Update evaluation for BM25 Pseudo" +
                    "-Relevance Query table\n 6. Update evaluation for TF IDF Pseudo-Relevance Query table\n 7. " +
                    "Update evaluation for BM25 with stop\n 8. Update evaluation for TF IDF with stop\n" +
                    " 9. Update evaluation for Likelihood with stop\n q. Quit");
            Scanner scanner = new Scanner( System.in );
            String command = scanner.nextLine();
            if (command.equals( "1" )) {
                System.out.print("BM25 Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.BM25);
                e.evaluate(queryidToScores,bm25Eva);
                System.out.print("Results Store in "+bm25Eva+"\n");
            }else if(command.equals("2")){
                System.out.print("TF IDF Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.TF_IDF);
                e.evaluate(queryidToScores,tfidfEva);
                System.out.print("Results Store in "+tfidfEva+"\n");
            }else if(command.equals("3")){
                System.out.print("Likelihood Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.Likelihood);
                e.evaluate(queryidToScores,lhEva);
                System.out.print("Results Store in "+lhEva+"\n");
            }else if(command.equals("4")){
                System.out.print("Lucene Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=new ArrayList<>();
                for (Map.Entry<Integer, String> entry : dc.genQuery().entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    Map<String,Double> score=factory.luceneSearch(query,-1,"","");
                    Map<Integer,Map<String ,Double>> singleQuery=new HashMap<>();
                    singleQuery.put(id,score);
                    queryidToScores.add(singleQuery);
                }
                e.evaluate(queryidToScores,luenceEva);
                System.out.print("Results Store in "+luenceEva+"\n");
            }else if(command.equals("5")){
                System.out.print("BM25 Pseudo Relevance Feedback Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=new ArrayList<>();
                for (Map.Entry<Integer, String> entry : dc.genQuery().entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    Map<String, Double> score = factory.bm25SearchWithStore(query,id,"","");
                    PseudoRelevanceFeedback prf = new PseudoRelevanceFeedback(factory, topDocNum, topTermNum);
                    List<String> topResult = PseudoRelevanceFeedback.rankFromMap(score);
                    prf.autoSearch(topResult, entry, ModelEnum.BM25, "");
                    Map<String,Double> pseScore=prf.getScore();
                    Map<Integer,Map<String ,Double>> singleQuery=new HashMap<>();
                    singleQuery.put(id,pseScore);
                    queryidToScores.add(singleQuery);
                }
                e.evaluate(queryidToScores,bm25PesEva);
                System.out.print("Results Store in "+bm25PesEva+"\n");
            }else if(command.equals("6")){
                System.out.print("TF IDF Pseudo Relevance Feedback Evaluation Start!\n");
                a=new Analyze();
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=new ArrayList<>();
                for (Map.Entry<Integer, String> entry : dc.genQuery().entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    Map<String, Double> score = factory.tfidfSearch(query);
                    PseudoRelevanceFeedback prf = new PseudoRelevanceFeedback(factory, topDocNum, topTermNum);
                    List<String> topResult = PseudoRelevanceFeedback.rankFromMap(score);
                    prf.autoSearch(topResult, entry, ModelEnum.TF_IDF, "");
                    Map<String,Double> pseScore=prf.getScore();
                    Map<Integer,Map<String ,Double>> singleQuery=new HashMap<>();
                    singleQuery.put(id,pseScore);
                    queryidToScores.add(singleQuery);
                }
                e.evaluate(queryidToScores,tfidfPesEva);
                System.out.print("Results Store in "+tfidfPesEva+"\n");
            }else if(command.equals("7")){
                System.out.print("BM25 Evaluation With Stop Start!\n");
                a=new Analyze(task3CorpusPath);
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.BM25);
                e.evaluate(queryidToScores,bm25EvaWithStop);
                System.out.print("Results Store in "+bm25EvaWithStop+"\n");
            }else if(command.equals("8")){
                a=new Analyze(task3CorpusPath);
                System.out.print("TF IDF Evaluation With Stop Start!\n");
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.TF_IDF);
                e.evaluate(queryidToScores,tfidfEvaWithStop);
                System.out.print("Results Store in "+tfidfEvaWithStop+"\n");
            }else if(command.equals("9")){
                System.out.print("Likelihood Evaluation With Stop Start!\n");
                a=new Analyze(task3CorpusPath);
                factory = new EngineFactory(a);
                ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=dc.getQueryIdToScores(factory,ModelEnum.Likelihood);
                e.evaluate(queryidToScores,lhEvaWithStop);
                System.out.print("Results Store in "+lhEvaWithStop+"\n");
            }else if (command.equals("q")) {
                break;
            }else{
                System.out.print("Command Error!\n");
            }
        }
    }

}
