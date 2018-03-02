package IR.phase2;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;
import IR.task2.PseudoRelevanceFeedback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SnippetGenerator {
    private String queryResultPath;
    private String sourceFilePath;
    private String outputHtmlPath;
    private Integer queryId;
    private String query;
    private String[] queryTerms;
    private ModelEnum model;
    private Integer MAX_SNIPPET_LEN = 156;
    private List<String> resultDocs;
    private Map<String, String> titles;
    private Map<String, String> snippets;
    private DataCenter dc;

    SnippetGenerator(Map.Entry<Integer, String> queryEntry, ModelEnum model, Boolean isPrf) {
        this.queryId = queryEntry.getKey();
        this.query = queryEntry.getValue();
        this.queryTerms = queryEntry.getValue().split(" ");
        this.model = model;
        this.dc = new DataCenter();
        String taskNum = isPrf ? "2" : "1";
        switch (model) {
            case BM25:
                this.queryResultPath = dc.getAbsolutePath()+"/src/main/resources/task"+taskNum+"/BM25Query/";
                this.outputHtmlPath = dc.getAbsolutePath()+"/src/main/resources/task"+taskNum+"/BM25QueryHtmlResult/";
                break;
            case TF_IDF:
                this.queryResultPath = dc.getAbsolutePath()+"/src/main/resources/task"+taskNum+"/TfIdfQuery/";
                this.outputHtmlPath = dc.getAbsolutePath()+"/src/main/resources/task"+taskNum+"/TfIdfQueryHtmlResult/";
                break;
            case Luence:
                this.queryResultPath = dc.getAbsolutePath()+"/src/main/resources/task1/LuceneQuery/";
                this.outputHtmlPath = dc.getAbsolutePath()+"/src/main/resources/task1/LuceneQueryHtmlResult/";
                break;
            case Likelihood:
                this.queryResultPath = dc.getAbsolutePath()+"/src/main/resources/task1/LikelihoodQuery/";
                this.outputHtmlPath = dc.getAbsolutePath()+"/src/main/resources/task1/LikelihoodQueryHtmlResult/";
                break;
            default:
                return;
        }
        this.queryResultPath += queryId.toString() + ".txt";
        this.sourceFilePath = dc.getAbsolutePath() + "/src/main/resources/Source/";
        this.outputHtmlPath += queryId.toString() + ".html";
        titles = new HashMap<>();
        snippets = new HashMap<>();
    }

    public void generate() {
        resultDocs = getDocsFromResultFile(queryResultPath);
        for (String doc:resultDocs) {
            String docPath = sourceFilePath + doc + ".html";
            String title = getTitle(docPath);
            titles.put(doc, title);
            String snippet = getSnippet(docPath, title);
            snippets.put(doc, snippet);
        }
        outputAsHtml();
    }
    public List<String> getDocsFromResultFile(String path) {
        List<String> res = new ArrayList<>();
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                String[] content = line.split(" ");
                if (content.length != 6) {
                    line = br.readLine();
                    continue;
                }
                if (this.model == ModelEnum.Luence) {
                    res.add(content[3]);
                } else {
                    res.add(content[2]);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getTitle(String path) {
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                if (line != null && !line.startsWith("<") && !line.equals("")) {
                    return line;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getSnippet(String path, String title) {
        String snippet = "";
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            Map<String, Boolean> inSnippet = new HashMap<>();
            Integer count = 0;
            Boolean lastLineFlag = true;
            List<String> stopList = this.dc.genSTOP();
            while (line != null && count < queryTerms.length && line.length() < MAX_SNIPPET_LEN) {
                if (line.equals(title) && title != null) {
                    line = br.readLine();
                    continue;
                }
                String[] content = line.split(" ");
                Boolean flag = false;
                for (String word: content) {
                    String term = word;
                    if (DataCenter.hasDigit(term)) {
                        term = term.replaceAll( "[^a-zA-Z0-9-]", " " );
                    }
                    term = term.toLowerCase();
                    if (stopList.contains(term)) {
                        continue;
                    }
                    if (Arrays.asList(queryTerms).contains(term)) {
                        flag = true;
                        if (!inSnippet.containsKey(term)) {
                            inSnippet.put(term, true);
                            count += 1;
                        }
                        line = highLightKeyword(line, word);
                    }
                }
                if (flag) {
                    if (lastLineFlag) {
                        snippet += "......";
                    }
                    snippet += " " + line;
                    lastLineFlag = true;
                } else {
                    lastLineFlag = false;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!snippet.endsWith(".") && snippet != "") {
            snippet += "...";
        }
        return snippet;
    }

    public String highLightKeyword(String str, String keyWord) {
        Integer start = str.indexOf(keyWord);
        Integer end = start + keyWord.length();
        if (start < 0) {
            return str;
        }
        return str.substring(0,start) + "<b>" + keyWord + "</b>" + str.substring(end, str.length());
    }

    public void outputAsHtml() {
        String html = "";
        html += "<html lang=\"en\">\n";
        html += "<head><title> Query ID:"+queryId.toString()+" search result(retrieval model:"+model.toString()+")</title></head>\n";
        html += "<body>\n";
        html += "<h1>Query ID:"+queryId.toString()+" search result(retrieval model:"+model.toString()+")</h1>\n";
        html += "<p>Query term: <i>"+query+"</i></p>\n";
        for (int i=0; i<resultDocs.size(); i++) {
            html += "<div>";
            String doc = resultDocs.get(i);
            html += "<h2>"+titles.get(doc)+"</h2>\n";
            html += "<h3>File:"+doc+"</h3>\n";
            html += "<h3>Rank:"+Integer.toString(i+1)+"</h3>\n";
            String snippet = snippets.get(doc);
            if (snippet != "") {
                html += "<p>"+snippet+"</p>\n";
            } else {
                html += "<p>No snippet currently available</p>\n";
            }
            html += "\n<hr />\n";
        }
        html += "</body>\n</html>";
        this.dc.writeFileData(html, outputHtmlPath);
        System.out.print("Result is stored in "+outputHtmlPath+"\n");
    }
}
