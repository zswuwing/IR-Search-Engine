package IR.task1.engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import IR.ModelEnum;
import IR.task1.DataCenter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Task 1
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class Luence implements SearchEngine{
    private static Analyzer sAnalyzer = new SimpleAnalyzer( Version.LUCENE_47 );

    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<File>();

    @Override
    public void search(String query) {

    }

    public void search(String query, IndexSearcher searcher, TopScoreDocCollector collector, int queryId, String tablePath, String systemName) {
        try {
            Query q = new QueryParser( Version.LUCENE_47, "contents",
                    sAnalyzer ).parse( query);
            searcher.search( q, collector );
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            String res="";
            System.out.println( "Found " + hits.length + " hits." );
            res=res+"Found " + hits.length + " hits.\n";
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc( docId );
                String line=queryId+" Q0 "+(i+1)+" "
                        +d.get("path").substring(d.get("path").lastIndexOf("\\")+1).split(".txt")[0]+
                        " "+hits[i].score+" "+systemName+"\n";
                luceneScore.put(d.get("path").substring(d.get("path").lastIndexOf("\\")+1).split(".txt")[0],(double)hits[i].score);
                if(queryId!=-1){
                    System.out.println( line);
                }
                res=res+line;
            }
            DataCenter fm=new DataCenter();
            String fileName=tablePath+queryId+".txt";
            if(queryId!=-1){
                fm.writeFileData(res,fileName);
            }
        } catch (Exception e) {
            System.out.println( "Error searching " + query + " : "
                    + e.getMessage() );
        }
    }


    /**
     * Constructor
     *
     * @param indexDir the name of the folder in which the index should be created
     * @throws IOException when exception creating index.
     */
    public Luence(String indexDir) throws IOException {

        FSDirectory dir = FSDirectory.open( new File( indexDir ) );

        IndexWriterConfig config = new IndexWriterConfig( Version.LUCENE_47,
                sAnalyzer );

        writer = new IndexWriter( dir, config );
    }

    /**
     * Indexes a file or directory
     *
     * @param fileName the name of a text file or a folder we wish to add to the
     *                 index
     * @throws IOException when exception
     */
    public void indexFileOrDirectory(String fileName) throws IOException {
        // ===================================================
        // gets the list of files in a folder (if user has submitted
        // the name of a folder) or gets a single file name (is user
        // has submitted only the file name)
        // ===================================================
        addFiles( new File( fileName ) );

        int originalNumDocs = writer.numDocs();
        int id=1;
        for (File f : queue) {
            FileReader fr = null;
            try {
                Document doc = new Document();

                // ===================================================
                // add contents of file
                // ===================================================
                fr = new FileReader( f );
                doc.add( new TextField( "contents", fr ) );
                doc.add( new StringField( "path", f.getPath(), Field.Store.YES ) );
                doc.add( new StringField( "filename", f.getName(),
                        Field.Store.YES ) );
                writer.updateDocument(new Term("filename",f.getName()),doc);
            } catch (Exception e) {
                System.out.println( "Could not add: " + f );
            } finally {
                fr.close();
            }
        }

        int newNumDocs = writer.numDocs();
        System.out.println( "" );
        System.out.println( "************************" );
        System.out
                .println( (newNumDocs - originalNumDocs) + " documents added." );
        System.out.println( "************************" );

        queue.clear();
    }

    private void addFiles(File file) {

        if (!file.exists()) {
            System.out.println( file + " does not exist." );
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFiles( f );
            }
        } else {
            String filename = file.getName().toLowerCase();
            // ===================================================
            // Only index text files
            // ===================================================
            if (filename.endsWith( ".htm" ) || filename.endsWith( ".html" )
                    || filename.endsWith( ".xml" ) || filename.endsWith( ".txt" )) {
                queue.add( file );
            } else {
                System.out.println( "Skipped " + filename );
            }
        }
    }

    /**
     * Close the index.
     *
     * @throws IOException when exception closing
     */
    public void closeIndex() throws IOException {
        writer.close();
    }

    @Override
    public Map<String, Double> getScore() {
        return this.luceneScore;
    }

    @Override
    public ModelEnum getType() {
        return ModelEnum.Luence;
    }

    private Map<String, Double> luceneScore=new HashMap<>();
    private ScoreDoc[] hits;

}