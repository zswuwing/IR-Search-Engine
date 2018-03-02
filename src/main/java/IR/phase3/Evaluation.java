package IR.phase3;

import IR.ModelEnum;
import IR.task1.Analyze;
import IR.task1.DataCenter;
import IR.task1.engine.EngineFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Evaluation {
    public Evaluation(){
        this.relevantList=dc.genRelevantList();
    }

    public void evaluate(ArrayList<Map<Integer,Map<String ,Double>>> queryidToScores, String path){
        ArrayList<Double> aplist=new ArrayList<>();
        ArrayList<Double> rrlist=new ArrayList<>();

        for(Map<Integer,Map<String ,Double>> queryidToScore:queryidToScores){
            for(Map.Entry<Integer,Map<String ,Double>> mapping : queryidToScore.entrySet()){
                int rank=1;
                int retrieve=0;
                Double p5=0.0;
                Double p20=0.0;
                Double rr=0.0;
                int queryId=mapping.getKey();
                ArrayList<Tuple> precision=new ArrayList<>();
                ArrayList<Tuple> recall=new ArrayList<>();
                ArrayList<Double> avgP=new ArrayList<>();
                Map<String ,Double> scores=mapping.getValue();
                ArrayList<String> rl;
                if(!this.relevantList.containsKey(queryId)){
                    System.out.print("query ID : "+queryId+" has no relevance judgment!\n");
                    break;
                }else{
                    rl=this.relevantList.get(queryId);
                }

                for(Map.Entry<String, Double> docidToScore : dc.stringToDoubleRank(scores)){
                    if(rank<=100){
                        String docId=docidToScore.getKey();
                        if(rl.contains(docId)){
                            retrieve=retrieve+1;
                            if(retrieve==1){
                                rr=(double)retrieve/(double)rank;
                            }
                            avgP.add((double)retrieve/(double)rank);
                        }
                        if(rank==5){
                            p5=(double)retrieve/(double)rank;
                        }
                        if(rank==20){
                            p20=(double)retrieve/(double)rank;
                        }
                        precision.add(new Tuple<>(docId,(double)retrieve/(double)rank));
                        recall.add(new Tuple(docId,(double)retrieve/(double)rl.size()));
                    }else {
                        break;
                    }
                    rank=rank+1;
                }
                // MAP
                aplist.add(getAverage(avgP));
                // MRR
                rrlist.add(rr);
                this.storeQueryEvaluation(path+queryId+".txt",p5,p20,precision,recall,rl);
            }
        }
        Double MAP=this.getAverage(aplist);
        Double MRR=this.getAverage(rrlist);
        System.out.print("MAP : "+ MAP+" MRR : "+MRR+"\n");
        dc.writeFileData("MAP : "+ MAP+" MRR : "+MRR,path+"Summary.txt");
    }

    private Double getAverage(ArrayList<Double> list){
        if(list.size()==0){
            return 0.0;
        }
        Double p=0.0;
        for(double pValue: list){
            p=p+pValue;
        }
        return p/(double)list.size();
    }

    private void storeQueryEvaluation(String path,Double p5,Double p20,ArrayList<Tuple> p,ArrayList<Tuple> r,ArrayList<String> rl){
        String content="P@5 : "+p5+" P@20 : "+p20+"\n";
        content=content+String.format("%-5s%-10s%-5s%-10s%-8s", "Rank","Doc","R/N","Precision","Recall")+"\n";
        for(int i=0;i<p.size();i++){
            String re="N";
            if(rl.contains(p.get(i).first)){
                re="R";
            }
            content+=String.format("%-5d%-10s%-5s%-10.3f%-8.3f",(i+1),p.get(i).first,re,p.get(i).second,r.get(i).second)+"\n";
        }
        System.out.print(content);
        dc.writeFileData(content,path);
    }

    private Map<Integer,ArrayList<String>> relevantList=new HashMap<>();
    private DataCenter dc=new DataCenter();
}
