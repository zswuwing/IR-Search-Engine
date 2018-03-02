package IR.task3;

import IR.task1.DataCenter;
import IR.task1.DirCheck;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Radu on 12/7/17.
 */
public class Task3B {

  public static void genFiles() throws FileNotFoundException {
    Task3B one = new Task3B();
    DirCheck dr = new DirCheck();
    DataCenter dc = new DataCenter();
    //List<String> queries = new ArrayList<>();
    String corpusPath =  dc.getAbsolutePath() + "/src/main/resources/cacm_stem.txt";
    String outputFilePath = dc.getAbsolutePath() + "/src/main/resources/CorpusForTask3B/";
    File file = new File(corpusPath);

    Scanner reader = new Scanner(new FileReader(file));
    String tempString = "";

    String totalFile = "";
    String fileName = "";
    while (reader.hasNextLine()) {
      tempString = reader.nextLine().toString().trim();
      //System.out.println(tempString);
      if(tempString.contains("#")) {
        //totalFile += tempString;
        fileName = tempString;
        //System.out.println(tempString);
      }
      else {
        continue;
      }

      while (reader.hasNextLine()){
        tempString = reader.nextLine().toString().trim();
        //System.out.println(tempString);
        String[] word = tempString.split(" +");
        boolean inTheEnd = false;
        for(String a : word) {
          //System.out.println(a);
          if(a.equals("am") || a.equals("pm")) {
            totalFile += a + " ";
            //System.out.println(tempString);

            dc.writeFileData(totalFile,outputFilePath + fileName + ".txt");
            inTheEnd = true;
            break;
          }

          //System.out.println(inTheEnd);
          totalFile += a + " ";
        }
        if (inTheEnd == true) {
//          FileWriter writer;
//
//          writer = new FileWriter(outputFilePath + fileName + ".txt");
//          writer.write(totalFile);
//          writer.flush();
//          writer.close();
          break;
        }


      }

      totalFile = "";
      fileName = "";
      ;

    }
    reader.close();

  }

  public static void main() throws IOException {
    System.out.println("This is task3 Part B, the corpus had been stemmed");
    Task3B one = new Task3B();
    one.genFiles();
    DirCheck dr = new DirCheck();
    DataCenter dc = new DataCenter();
    String corpusPath =  dc.getAbsolutePath() + "/src/main/resources/CorpusForTask3B";
    String bmTablePath = dc.getAbsolutePath() + "/src/main/resources/task3/BM25QueryB/";
    String tfidfTablePath = dc.getAbsolutePath() + "/src/main/resources/task3/TfIdfQueryB/";
    String queryPath = dc.getAbsolutePath() + "/src/main/resources/cacm_stem.query.txt";
    String lhTablePath = dc.getAbsolutePath() + "/src/main/resources/task3/LikelihoodQueryB/";

    List<String> queries = new ArrayList<>();
    File file = new File(queryPath);
    Scanner reader = new Scanner(new FileReader(file));
    //int i = 0;
    String tempString = "";
    while(reader.hasNextLine()) {
      tempString = reader.nextLine().toString().trim();
      System.out.println(tempString);
      queries.add(tempString);
      //i++;
    }
    reader.close();
    System.out.println(queries.size());

    Task3A helper = new Task3A();
    helper.getBM25(corpusPath,bmTablePath,queries,"BM25StemCaseFPun");
    helper.getTFIDF(corpusPath,tfidfTablePath,queries,"TFIDFStemCaseFPun");
    helper.getQueryLikelihood(corpusPath,lhTablePath,queries,"SmoothLHStemCaseFPun");


  }


}
