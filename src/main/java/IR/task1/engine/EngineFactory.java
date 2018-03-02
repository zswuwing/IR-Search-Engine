package IR.task1.engine;

import IR.task1.Analyze;
import IR.task1.DataCenter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EngineFactory {
    public EngineFactory(Analyze a){
        this.analysis =a;
    }
    public Map<String,Double> bm25Search(String query){
        BM25 algorithm = new BM25( this.analysis);
        algorithm.search( query );
        return algorithm.getScore();
    }

    public Map<String,Double> bm25SearchWithStore(String query,int queryId, String tablePath,String systemName){
        BM25 algorithm = new BM25( this.analysis);
        algorithm.search(query,queryId, tablePath,systemName );
        return algorithm.getScore();
    }

    public Map<String,Double> tfidfSearch(String query){
        Tf_Idf algorithm = new Tf_Idf( this.analysis );
        algorithm.search( query );
        return algorithm.getScore();
    }

    public Map<String,Double> tfidfSearchWithStore(String query,int queryId, String tablePath,String systemName){
        Tf_Idf algorithm = new Tf_Idf( this.analysis);
        algorithm.search(query,queryId, tablePath,systemName );
        return algorithm.getScore();
    }

    public Map<String,Double> likelihoodSearch(String query){
        Likelihood algorithm = new Likelihood( analysis );
        algorithm.search( query );
        return algorithm.getScore();
    }

    public Map<String,Double> likelihoodSearchWithStore(String query,int queryId, String tablePath,String systemName){
        Likelihood algorithm = new Likelihood( analysis );
        algorithm.search(query,queryId, tablePath,systemName );
        return algorithm.getScore();
    }

    public Map<String,Double> luceneSearch(String query,int queryId,String tablePath,String systemName) throws IOException {
        DataCenter dc=new DataCenter();
        String corpusPath = dc.getAbsolutePath() + "/src/main/resources/Corpus/";
        String indexLocation = dc.getAbsolutePath() + "/src/main/resources/task1/LuceneIndex/";

        Luence indexer = null;
        try {
            indexer = new Luence( indexLocation );
        } catch (Exception ex) {
            System.out.println( "Cannot create index..." + ex.getMessage() );
            System.exit( -1 );
        }
        try {
            System.out
                    .println( "FULL path to add into the index: " + corpusPath );
            System.out
                    .println( "[Acceptable file types: .xml, .html, .html, .txt]" );
            indexer.indexFileOrDirectory( corpusPath );
        } catch (Exception e) {
            System.out.println( "Error indexing " + corpusPath + " : "
                    + e.getMessage() );
        }
        indexer.closeIndex();
        IndexReader reader = DirectoryReader.open( FSDirectory.open( new File(
                indexLocation ) ) );
        IndexSearcher searcher = new IndexSearcher( reader );
        TopScoreDocCollector collector = TopScoreDocCollector.create( 100, true );
        indexer.search( query, searcher, collector,queryId,tablePath,systemName );

        return indexer.getScore();
    }



    private Analyze analysis;
}
