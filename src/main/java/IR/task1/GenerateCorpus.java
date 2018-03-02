package IR.task1;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateCorpus {
    private String dirPath;
    private String corpusPath;
    private DataCenter fm=new DataCenter();

    public GenerateCorpus(String sourcePath){
        this.dirPath=sourcePath;
        this.corpusPath=fm.getAbsolutePath()+"/src/main/resources/Corpus/";
    }

    public GenerateCorpus(String sourcePath,String corpusPath){
        this.dirPath=sourcePath;
        this.corpusPath=corpusPath;
    }

    public void generate(Boolean casefold, Boolean punctuation,Boolean stop){
        File[] files=new File(this.dirPath).listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        int process=0;
        for(File file:files){
            if(file.getName().endsWith(".html")){
                String htmlContent=fm.readFileData(file.getAbsolutePath());
                String content=delHTMLTag(htmlContent);
                content=fm.filter( content,casefold,punctuation,stop );
                String filePath=this.corpusPath+getFileName(file.getName())+".txt";
                fm.writeFileData(content,filePath);
            }
            process=process+1;
            int print=process%100;
            if(print==0){
                System.out.print("Analysis Process: "+process +" files.\n");
            }
        }
    }

    private String delHTMLTag(String content){
        Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(content);
        content = m_html.replaceAll("");
        return content.trim();
    }

    private String getFileName(String filePath) {
        int dot = filePath.lastIndexOf('.');
        if ((dot >-1) && (dot < (filePath.length()))) {
            return filePath.substring(0, dot);
        }
        return filePath;
    }
}
