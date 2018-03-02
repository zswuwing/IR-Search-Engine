package IR.task1.engine;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;

import java.util.*;

public class Tf_Idf implements SearchEngine{
    public Tf_Idf(Analyze a){
        this.df=a.getDf();
        this.index=a.getIndex();
        this.dl=a.getDl();
        this.TIScore=new HashMap<>();
        this.N=this.dl.size();
    }

    public void search(String query,int queryId, String tablePath,String systemName ){
        this.search(query);
        this.store(queryId,tablePath,systemName);
    }
    @Override
    public void search(String query){
        String[] queryList=query.split(" ");
        for(String documentId:this.dl.keySet()){
            Double tiScore=0.0;
            for(String word:queryList){
                if(this.index.containsKey( word )){
                    int df=this.df.get(word).size();
                    Map<String ,Integer> fiMap=this.index.get(word);
                    int fi=0;
                    if(fiMap.containsKey(documentId)){
                        fi=fiMap.get(documentId);
                    }
                    int TF=this.dl.get(documentId);
                    Double score=((double)fi/(double)TF)*Math.log(this.N/df);
                    tiScore=tiScore+score;
                }
            }
            this.TIScore.put(documentId,tiScore);
        }
    }

    public void store(int queryId,String tablePath,String systemName){
        if(!this.TIScore.isEmpty()){
            int num=1;
            String res="";
            for(Map.Entry<String, Double> mapping : fm.stringToDoubleRank(TIScore)){
                if(num<=100){
                    res=res+queryId+" Q0 "+mapping.getKey()+" "+num+" "+mapping.getValue()+" "+systemName+"\n";
                    num=num+1;
                }
            }
            fm.writeFileData(res,tablePath+queryId+".txt");
            System.out.print( res +"\n");
        }else{
            System.out.print( "Result is empty!" );
        }
    }

    @Override
    public Map<String, Double> getScore() {
        return this.TIScore;
    }

    @Override
    public ModelEnum getType() {
        return ModelEnum.TF_IDF;
    }


    private Map<String ,ArrayList<String>> df;
    private Map<String, Map<String, Integer>> index;
    private Map<String, Double> TIScore;
    private Map<String,Integer> dl;
    private int N;
    private DataCenter fm=new DataCenter();
}
