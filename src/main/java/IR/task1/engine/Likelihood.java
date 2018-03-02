package IR.task1.engine;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;

/*
 * Jelinek Mercer Smoothing
 */
public class Likelihood implements SearchEngine{
    public Likelihood(Analyze a){
        this.df=a.getDf();
        this.index=a.getIndex();
        this.dl=a.getDl();
        this.tf=a.getTf();
        this.LHScore =new HashMap<>();
        this.N=this.dl.size();
        this.lambda=0.35;
        this.setC();

    }

    public void search(String query,int queryId, String tablePath,String systemName ){
        this.search(query);
        this.store(queryId,tablePath,systemName);
    }
    @Override
    public void search(String query){
        String[] queryList=query.split(" ");
        for(String documentId:this.dl.keySet()){
            Double lhScore=0.0;
            for(String word:queryList){
                if(this.index.containsKey( word )){
                    int tf=0;
                    if(this.tf.containsKey( word )){
                        tf=this.tf.get(word);
                    }
                    Map<String ,Integer> fiMap=this.index.get(word);
                    int fqiD=0;
                    if(fiMap.containsKey(documentId)){
                        fqiD=fiMap.get(documentId);
                    }
                    int D=this.dl.get(documentId);
                    Double first=(1-this.lambda)*((double)fqiD/(double)D);
                    Double sceond=this.lambda*(double)tf/(double) this.C;
                    Double score=Math.log(first+sceond);
                    lhScore=lhScore+score;
                }
            }
            this.LHScore.put(documentId,lhScore);
        }
    }

    public void store(int queryId,String tablePath,String systemName){
        if(!this.LHScore.isEmpty()){
            int num=1;
            String res="";
            for(Map.Entry<String, Double> mapping : fm.stringToDoubleRank(LHScore)){
                if(num<=100){
                    res=res+queryId+" Q0 "+mapping.getKey()+" "+num+" "+mapping.getValue()+" "+systemName+"\n";
                    num=num+1;
                }
            }
            System.out.print( tablePath );
            fm.writeFileData(res,tablePath+queryId+".txt");
            System.out.print( res +"\n");
        }else{
            System.out.print( "Result is empty!" );
        }
    }

    public void setC(){
        this.C=0;
        for(Map.Entry<String,Integer> entry:this.tf.entrySet()){
            this.C=this.C+entry.getValue();
        }

    }

    @Override
    public Map<String, Double> getScore() {
        return this.LHScore;
    }

    @Override
    public ModelEnum getType() {
        return ModelEnum.Likelihood;
    }

    private Map<String ,ArrayList<String>> df;
    private Map<String, Map<String, Integer>> index;
    private Map<String, Double> LHScore;
    private Map<String,Integer> dl;
    private Map<String,Integer> tf;
    private int N;
    private int C;
    private Double lambda;
    private DataCenter fm=new DataCenter();
}
