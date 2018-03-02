package IR.task1.engine;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;

import java.util.*;

/**
 * Task 2: Calculate the BM25 score for given query
 */
public class BM25 implements SearchEngine {

    public BM25(Analyze a){
        this.index=a.getIndex();
        this.df=a.getDf();
        this.dl=a.getDl();
        this.N=this.dl.size();
        this.BMScore=new HashMap<>();
        this.relevantList=dc.genRelevantList();
    }

    public void search(String query,int queryId, String tablePath,String systemName ){
        this.queryId=queryId;
        this.search(query);
        if(!tablePath.equals("")){
            this.store(queryId,tablePath,systemName);
        }
    }

    @Override
    public void search(String query){
        String[] queryList=query.split(" ");
        Double avgdl=this.getAvgdl();
        ArrayList<String> rList=new ArrayList<>();
        if(relevantList.containsKey(this.queryId)){
            rList=relevantList.get(this.queryId);
        }
        Double R=(double)rList.size();
        for(String documentId:this.dl.keySet()){
            Double bmScore=0.0;
            Map<String,Double> qfiMap=new HashMap<>();
            for(String word:queryList){
                if(qfiMap.containsKey(word)){
                    Double preNum=qfiMap.get(word);
                    qfiMap.put(word,preNum+1);
                }else {
                    qfiMap.put(word,1.0);
                }
            }
            for(String word:queryList){
                if(this.index.containsKey( word )){
                    double qfi=qfiMap.get(word)/queryList.length;
                    int ni=this.df.get(word).size();
                    ArrayList<String> riList=this.df.get(word);
                    riList.retainAll(rList);
                    Double ri=(double)riList.size();
                    int dl=this.dl.get(documentId);
                    Map<String ,Integer> fiMap=this.index.get(word);
                    int fi=0;
                    if(fiMap.containsKey(documentId)){
                        fi=fiMap.get(documentId);
                    }
                    Double K=this.k1*(1-this.b+this.b*dl/avgdl);
                    Double first=Math.log(((ri+0.5)/(R-ri+0.5))/((ni-ri+0.5)/(this.N-ni-R+ri+0.5)));
                    Double second=(this.k1+1)*fi/(K+fi);
                    Double third=(this.k2+1)*qfi/(this.k2+qfi);
                    bmScore=bmScore+first*second*third;
                }
            }
            this.BMScore.put(documentId,bmScore);
        }
    }


    public double getAvgdl(){
        Double sum=0.0;
        for(String key:this.dl.keySet()){
            sum=sum+this.dl.get(key);
        }
        return sum/this.dl.size();
    }


    public void store(int queryId, String tablePath,String systemName){
        if(!this.BMScore.isEmpty()){
            int num=1;
            String res="";
            for(Map.Entry<String, Double> mapping : dc.stringToDoubleRank(BMScore)){
                if(num<=100){
                    res=res+queryId+" Q0 "+mapping.getKey()+" "+num+" "+mapping.getValue()+" "+systemName+"\n";
                    num=num+1;
                }
            }
            this.dc.writeFileData(res,tablePath+queryId+".txt");
            System.out.print( res +"\n");
        }
    }

    @Override
    public Map<String, Double> getScore() {
        return this.BMScore;
    }

    @Override
    public ModelEnum getType() {
        return ModelEnum.BM25;
    }

    private Map<String, Map<String, Integer>> index;
    private Map<String,Integer> dl;
    private Map<String ,ArrayList<String>> df;
    private Double k1=1.2;
    private Double k2=100.0;
    private Double b=0.75;
    private Map<String, Double> BMScore;
    private int N;
    private Map<Integer,ArrayList<String>> relevantList;
    private DataCenter dc=new DataCenter();
    private int queryId;
}
