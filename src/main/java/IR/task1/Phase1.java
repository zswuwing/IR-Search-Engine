package IR.task1;

import IR.ModelEnum;
import IR.task1.engine.*;
import IR.task3.Task3B;
import java.io.IOException;
import java.util.*;

import IR.task2.PseudoRelevanceFeedback;
import IR.task3.Task3A;

public class Phase1 {
    public static void main() throws IOException {
        DataCenter dc = new DataCenter();
        DirCheck dr = new DirCheck();
        String sourcePath = dc.getAbsolutePath() + "/src/main/resources/Source/";
        String luenceTablePath = dc.getAbsolutePath() + "/src/main/resources/task1/LuceneQuery/";
        String bmTablePath = dc.getAbsolutePath() + "/src/main/resources/task1/BM25Query/";
        String tfidfTablePath = dc.getAbsolutePath() + "/src/main/resources/task1/TfIdfQuery/";
        String lhTablePath = dc.getAbsolutePath() + "/src/main/resources/task1/LikelihoodQuery/";
        String task3CorpusPath =  dc.getAbsolutePath() + "/src/main/resources/Task3Corpus/";
        String bmTablePathA = dc.getAbsolutePath() + "/src/main/resources/task3/BM25QueryA/";
        String tfidfTablePathA = dc.getAbsolutePath() + "/src/main/resources/task3/TfIdfQueryA/";
        String lhTablePathA = dc.getAbsolutePath() + "/src/main/resources/task3/LikelihoodQueryA/";
        Integer topDocNum = 10;
        Integer topTermNum = 10;
        String prfSystem = "(PRF)";
        String systemName = "CaseFPun";;
        // Update corpus
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to update corpus for Phase 1, task 1(y/n) :\n");
        String s = scanner.nextLine();
        if (s.equalsIgnoreCase("y")) {
            GenerateCorpus gc = new GenerateCorpus(sourcePath);
            gc.generate(true, true, false);
            System.out.print("Update all corpus: Casefold true Punctuation : true Stop : false\n");
        }else{
            System.out.print("Based on corpus with Casefold and Punctuation!\n");
        }
        Analyze a = new Analyze();
        System.out.print("Do you want to update unigram and language models for Phase 1, task 1(y/n) :\n");
        s = scanner.nextLine();
        if (s.equalsIgnoreCase("y")) {
            a.storeInvertedIndex(dc.getAbsolutePath()+"/src/main/resources/Unigrams/task1_unigram.txt");
            a.storeEstimatedLanguageModels(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task1/");
        }
        EngineFactory factory = new EngineFactory(a);
        HashMap<Integer, String> querys = dc.genQuery();
        while (true) {
            System.out.println("Phase 1 Instruction：\n 1. Update BM25 Query Table\n " +
                    "2. Update TF IDF Query table \n 3. Update Smoothed Query Likelihood Model table \n 4." +
                    " Update Lucene Query table\n 5. Update result for 3 base runs with Stop \n"
                + " 6. Update result for 3 base runs with Stem \n q. Quit");
            String command = scanner.nextLine();
            if (command.equals("1")) {
                String bm25SystemName = "BM25" + systemName;
                System.out.println("Perform Pseudo-Relevance Feedback?(y/n):");
                String isPRF = scanner.nextLine();
                for (Map.Entry<Integer, String> entry : querys.entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    Map<String, Double> score = factory.bm25SearchWithStore(query, id, bmTablePath, bm25SystemName);
                    if (isPRF.equalsIgnoreCase("y")) {
                        PseudoRelevanceFeedback prf = new PseudoRelevanceFeedback(factory, topDocNum, topTermNum);
                        List<String> topResult = PseudoRelevanceFeedback.rankFromMap(score);
                        prf.autoSearch(topResult, entry, ModelEnum.BM25, bm25SystemName + prfSystem);
                    }
                }
                System.out.print("Results Store in "+bmTablePath+"\n");
                a=new Analyze();
                factory = new EngineFactory(a);
            } else if (command.equals("2")) {
                String tfIdfSystemName = "TFIDF" + systemName;
                System.out.println("Perform Pseudo-Relevance Feedback?(y/n):");
                String isPRF = scanner.nextLine();
                for (Map.Entry<Integer, String> entry : querys.entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    Map<String, Double> score = factory.tfidfSearchWithStore(query, id, tfidfTablePath, tfIdfSystemName);
                    if (isPRF.equalsIgnoreCase("y")) {
                        PseudoRelevanceFeedback prf = new PseudoRelevanceFeedback(factory, topDocNum, topTermNum);
                        List<String> topResult = PseudoRelevanceFeedback.rankFromMap(score);
                        prf.autoSearch(topResult, entry, ModelEnum.TF_IDF, tfIdfSystemName + prfSystem);
                    }
                }
                System.out.print("Results Store in "+tfidfTablePath+"\n");
            } else if (command.equals("3")) {
                String lhSystemName = "SmoothLH" + systemName;
                for (Map.Entry<Integer, String> entry : querys.entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    factory.likelihoodSearchWithStore(query, id, lhTablePath, lhSystemName);
                }
                System.out.print("Results Store in "+lhTablePath+"\n");
            } else if (command.equals("4")) {
                String sysName = "Lucene";
                for (Map.Entry<Integer, String> entry : querys.entrySet()) {
                    int id = entry.getKey();
                    String query = entry.getValue();
                    factory.luceneSearch(query, id, luenceTablePath, sysName);
                }
                System.out.print("Results Store in "+luenceTablePath+"\n");
            } else if (command.equals("5")) {
                System.out.print("Do you want to update corpus for Phase 1, task 3(y/n) :\n");
                s = scanner.nextLine();
                if (s.equalsIgnoreCase("y")) {
                    systemName = "StopCaseFPun";
                    GenerateCorpus gc = new GenerateCorpus(sourcePath,task3CorpusPath);
                    gc.generate(true, true, true);
                    System.out.print("Update all corpus: Casefold true Punctuation : true Stop : true\n");
                }else {
                    System.out.print("Based on corpus with Stop, Casefold and Punctuation!\n");
                }
                System.out.print("Do you want to update unigram and language models for Phase 1, task 3 A(y/n) :\n");
                s = scanner.nextLine();
                if (s.equalsIgnoreCase("y")) {
                    a = new Analyze(task3CorpusPath);
                    a.storeInvertedIndex(dc.getAbsolutePath()+"/src/main/resources/Unigrams/task3A_unigram.txt");
                    a.storeEstimatedLanguageModels(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task3A/");
                }
                Task3A helper = new Task3A();
                List<String> queries = new ArrayList<>();
                HashMap<Integer,String> queriesMap = dc.genQuery();
                for(int i : queriesMap.keySet()) {
                    queries.add(queriesMap.get(i));
                }
                helper.getBM25(task3CorpusPath,bmTablePathA,queries,"BM25StopCaseFPun");
                helper.getTFIDF(task3CorpusPath,tfidfTablePathA,queries,"TFIDFStopCaseFPun");
                helper.getQueryLikelihood(task3CorpusPath,lhTablePathA,queries,"SmoothLHStopCaseFPun");
            } else if (command.equals("6")) {
                Task3B task3B = new Task3B();
                task3B.main();
                System.out.print("Do you want to update unigram and language models for Phase 1, task 3 B(y/n) :\n");
                s = scanner.nextLine();
                if (s.equalsIgnoreCase("y")) {
                    a = new Analyze(dc.getAbsolutePath() + "/src/main/resources/CorpusForTask3B");
                    a.storeInvertedIndex(dc.getAbsolutePath()+"/src/main/resources/Unigrams/task3B_unigram.txt");
                    a.storeEstimatedLanguageModels(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task3B/");
                }
            } else if (command.equals("q")) {
                break;
            } else {
                System.out.print("Command error!");
            }
        }
    }
}