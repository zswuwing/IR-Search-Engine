package IR.task1;

import IR.ModelEnum;
import IR.task1.engine.EngineFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to readFileData and write file
 */
public class DataCenter {
    private String absolutePath;

    public DataCenter(){
        File f=new File("");
        this.absolutePath=f.getAbsolutePath();
    }

    public String readFileData(String filepath) {
        String content = null;
        File file = new File( filepath );
        FileReader reader = null;
        try {
            reader = new FileReader( file );
            char[] chars = new char[(int) file.length()];
            reader.read( chars );
            content = new String( chars );
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void deleteFileData(String deletePath){
        try {
            File file = new File(deletePath);
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(deletePath + "\\" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        deleteFileData(deletePath + "\\" + filelist[i]);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("deleteFileData()  Exception:" + e.getMessage());
        }
    }

    public void writeFileData(String content, String path){
        File file = new File(path);
        try {
            if(!file.exists()){
                file.createNewFile();
            }else {
                this.deleteFileData(path);
                file.createNewFile();
            }
            FileWriter writer;
            try {
                writer = new FileWriter(path);
                writer.write(content);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDir(String dirPath){
        File file = new File(dirPath);
        try {
            if(!file.exists()){
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer,String> genQuery(){
        HashMap<Integer,String> res=new HashMap<>();
        String queryContent=this.readFileData(this.absolutePath+"/src/main/resources/cacm.query.txt");
        queryContent=queryContent.replaceAll("\r|\n", " ");
        String[] queries=queryContent.split("</DOC>");
        for(String query:queries){
            if(query!=""){
                Matcher matcher = Pattern.compile("<DOCNO>(.+?)</DOCNO>").matcher(query);
                int no=-1;
                if (matcher.find()) {
                    no =Integer.parseInt( matcher.group(1).trim());
                }
                if(query.contains("</DOCNO>")){
                    query=query.substring(query.lastIndexOf("</DOCNO>")+8);
                }else {
                    continue;
                }
            res.put(no,filter(query,true,true,true));
            }
        }
        return res;
    }

    public ArrayList<String> genSTOP(){
        String stopContent=this.readFileData(this.absolutePath+"/src/main/resources/common_words");
        return new ArrayList<String>(Arrays.asList(stopContent.split("\r\n")));
    }

    public String filter(String content,Boolean casefold, Boolean punctuation,Boolean stop){
        String res="";
        if(casefold){
            content=casefold(content);
        }
        for(String s:content.split(" ")) {
            s = s.trim();
            if (!s.equals( "" )) {
                if (punctuation) {
                    if (!hasDigit( s )) {
                        s = s.replaceAll( "[^a-zA-Z0-9-]", " " );
                    } else {
                        s = numberWithPun( s );
                    }
                }
                if (s.contains( " " )) {
                    for (String cs : s.split( " " )) {
                        if (!cs.equals( "" )) {
                            res += checkStop(cs,stop);
                        }
                    }
                }else {
                    res+=checkStop(s,stop);
                }
            }
        }
        return res;
    }

    private String checkStop(String s,Boolean stop){
        ArrayList<String> stopList=genSTOP();
        if(stop&&stopList.contains(s)){
            return "";
        }
        return s+" ";
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    static public boolean hasDigit(String content) {
        return content.matches(".*\\d+.*");
    }

    private String casefold(String content){
        return content.toLowerCase();
    }

    public String numberWithPun(String word){
        String res="";
        for(int i=0;i<word.length();i++){
            if(Pattern.matches("\\p{Punct}", word.charAt( i )+"")){
                if(i-1>=0&&i+1<word.length()){
                    if(hasDigit( word.charAt( i-1 )+"" )&&hasDigit( word.charAt( i+1 )+"" )){
                        res=res+word.charAt( i );
                    }
                }
            }else{
                res=res+word.charAt( i );
            }
        }
        return res;
    }


    public Map<Integer,ArrayList<String>> genRelevantList(){
        String path=this.getAbsolutePath()+"/src/main/resources/cacm.rel.txt";
        String[] content=this.readFileData(path).split("\n");
        Map<Integer,ArrayList<String>> res=new HashMap<>();
        ArrayList<String> temp;
        for(String l:content){
            String[] lines=l.split(" ");
            int id= Integer.valueOf(lines[0]);
            String docId=lines[2];
            if(res.containsKey(id)){
                temp=res.get(id);
            }else{
                temp=new ArrayList<>();
            }
            temp.add(docId);
            res.put(id,temp);
        }
        return res;
    }

    public List<Map.Entry<String, Double>> stringToDoubleRank(Map<String,Double> map){
        Map<String,Double> res=new HashMap<>();
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    public ArrayList<Map<Integer,Map<String ,Double>>> getQueryIdToScores(EngineFactory factory,ModelEnum emum){
        ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores=new ArrayList<>();
        for (Map.Entry<Integer, String> entry : this.genQuery().entrySet()) {
            int id = entry.getKey();
            String query = entry.getValue();
            Map<String,Double> score=new HashMap<>();
            if(emum==ModelEnum.BM25){
                score=factory.bm25SearchWithStore(query,id,"","");
            }
            if(emum==ModelEnum.Likelihood){
                score=factory.likelihoodSearch(query);
            }
            if(emum==ModelEnum.TF_IDF){
                score=factory.tfidfSearch(query);
            }
            Map<Integer,Map<String ,Double>> singleQuery=new HashMap<>();
            singleQuery.put(id,score);
            queryidToScores.add(singleQuery);
        }
        return queryidToScores;
    }
    public static void main(String[] args){
        DataCenter dc=new DataCenter();
        dc.genRelevantList();
    }
}
