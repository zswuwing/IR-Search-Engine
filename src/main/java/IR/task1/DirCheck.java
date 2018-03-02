package IR.task1;

import java.io.File;

public class DirCheck {

  public DirCheck() {
    File f = new File("");
    String absolutePath = f.getAbsolutePath();
    DataCenter dc = new DataCenter();
    // Phase 1
    dc.createDir(absolutePath + "/src/main/resources/Source/");
    dc.createDir(absolutePath + "/src/main/resources/Corpus/");
    dc.createDir(absolutePath + "/src/main/resources/Task3Corpus/");
    dc.createDir(absolutePath + "/src/main/resources/task1/BM25Query");
    dc.createDir(absolutePath + "/src/main/resources/task1/LuceneQuery");
    dc.createDir(absolutePath + "/src/main/resources/task1/TfIdfQuery");
    dc.createDir(absolutePath + "/src/main/resources/task1/LikelihoodQuery");
    dc.createDir(absolutePath + "/src/main/resources/task1/LuceneIndex");
    dc.createDir(absolutePath + "/src/main/resources/task2/BM25Query");
    dc.createDir(absolutePath + "/src/main/resources/task2/TfIdfQuery");

    dc.createDir(absolutePath + "/src/main/resources/task3/TfIdfQueryA");
    dc.createDir(absolutePath + "/src/main/resources/task3/BM25QueryA");
    dc.createDir(absolutePath + "/src/main/resources/task3/LikelihoodQueryA");
    dc.createDir(absolutePath + "/src/main/resources/task3/TfIdfQueryB");
    dc.createDir(absolutePath + "/src/main/resources/task3/BM25QueryB");
    dc.createDir(absolutePath + "/src/main/resources/task3/LikelihoodQueryB");

    dc.createDir(absolutePath + "/src/main/resources/CorpusForTask3B");
    // Phase 2
    dc.createDir(absolutePath + "/src/main/resources/task1/BM25QueryHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task1/LuceneQueryHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task1/TfIdfQueryHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task1/LikelihoodQueryHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task1/LuceneIndexHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task2/BM25QueryHtmlResult");
    dc.createDir(absolutePath + "/src/main/resources/task2/TfIdfQueryHtmlResult");

    // Phase 3
    dc.createDir(absolutePath + "/src/main/resources/phase3/BM25Query");
    dc.createDir(absolutePath + "/src/main/resources/phase3/LuceneQuery");
    dc.createDir(absolutePath + "/src/main/resources/phase3/TfIdfQuery");
    dc.createDir(absolutePath + "/src/main/resources/phase3/LikelihoodQuery");
    dc.createDir(absolutePath + "/src/main/resources/Phase3/BM25PesQuery/");
    dc.createDir(absolutePath + "/src/main/resources/Phase3/TfIdfPesQuery/");
    dc.createDir(absolutePath + "/src/main/resources/phase3/BM25QueryWithStop");
    dc.createDir(absolutePath + "/src/main/resources/phase3/TfIdfQueryWithStop");
    dc.createDir(absolutePath + "/src/main/resources/phase3/LikelihoodQueryWithStop");

    //Extra_Credit
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/TfIdfQuery/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/resultOfNonStop/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/resultOfStop/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/StopCorpus/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Extra_Credit/NonStopCorpus/");

    // intermediate result files
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/Unigrams/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task1/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task3A/");
    dc.createDir(dc.getAbsolutePath() + "/src/main/resources/EstimatedLanguageModels/task3B/");
  }
}