package IR.phase2;

import IR.ModelEnum;
import IR.task1.DataCenter;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Scanner;

public class Phase2 {
    public static void main() throws IOException {
            while (true) {
                System.out.println("Phase 2 Instruction:\nPlease input the query ID to get the search result, (0 to output all result, q : quit)");
                Scanner scanner = new Scanner( System.in );
                String command = scanner.nextLine();
                if (command.equals("q")) {
                    break;
                }
                System.out.println("Choose the retrieval model the search used:\n 1. BM25 2. TF-IDF 3. Smoothed Query Likelihood Model 4. Lucene");
                String modelCommand = scanner.nextLine();
                ModelEnum model;
                if (modelCommand.equals("1")) {
                    model = ModelEnum.BM25;
                } else if (modelCommand.equals("2")) {
                    model = ModelEnum.TF_IDF;
                } else if (modelCommand.equals("3")) {
                    model = ModelEnum.Likelihood;
                } else if (modelCommand.equals("4")) {
                    model = ModelEnum.Luence;
                } else {
                    System.out.println("Invalid option!");
                    break;
                }
                System.out.println("Did the search perform PRF?(y/n)");
                String prfCommand = scanner.nextLine();
                Boolean isprf = prfCommand.equalsIgnoreCase("y") ? true : false;
                Integer queryId = Integer.parseInt(command);
                DataCenter dc = new DataCenter();
                Map<Integer,String> queries = dc.genQuery();
                if (!queries.containsKey(queryId) && !command.equals("0")) {
                    System.out.println("Invalid query ID!");
                    continue;
                }
                for (Map.Entry<Integer,String> query:queries.entrySet()) {
                    if (command.equals("0") || queryId == query.getKey()) {
                        Map.Entry<Integer,String> queryEntry = new AbstractMap.SimpleEntry<Integer, String>(query.getKey(), query.getValue());
                        SnippetGenerator sg = new SnippetGenerator(queryEntry, model, isprf);
                        sg.generate();
                    }
                }
            }
    }
}
