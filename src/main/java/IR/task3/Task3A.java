package IR.task3;

import IR.task1.Analyze;
import IR.task1.engine.BM25;
import IR.task1.engine.Likelihood;
import IR.task1.engine.Tf_Idf;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Radu on 12/6/17.
 */
public class Task3A {

  public static void getBM25(String corpusPath, String bmTablePath, List<String> queries, String systemName) throws FileNotFoundException {
    Analyze a = new Analyze(corpusPath);
    a.analyze();
    int id = 0;
    for (String query : queries) {
      //String systemName = "BM25StopCaseFPun";
      BM25 algorithm = new BM25(a);
      id += 1;
      algorithm.search(query);
      algorithm.store(id, bmTablePath, systemName);
    }
  }

  public static void getTFIDF(String corpusPath, String tfidfTablePath, List<String> queries, String systemName) throws FileNotFoundException {
    int id = 0;
    Analyze a = new Analyze(corpusPath);
    a.analyze();
    //String systemName = "TFIDFStopCaseFPun";
    for (String query : queries) {
      Tf_Idf algorithm = new Tf_Idf(a);
      algorithm.search(query);
      id += 1;
      //String tfidfTablePath = dc.getAbsolutePath() + "/src/main/resources/task3/TfIdfQueryA/";
      algorithm.store( id, tfidfTablePath, systemName );
    }
  }

  public static void getQueryLikelihood(String corpusPath, String lhTablePath, List<String> queries, String systemName) throws FileNotFoundException {
    int id = 0;
    Analyze a = new Analyze(corpusPath);
    a.analyze();
    //String systemName = "SmoothLHStopCaseFPun";
    for (String query : queries) {
      Likelihood algorithm = new Likelihood(a);
      algorithm.search(query);
      id += 1;
      //String lhTablePath = dc.getAbsolutePath() + "/src/main/resources/task1/LikelihoodQuery/";
      algorithm.store( id, lhTablePath, systemName );
    }
  }
}
