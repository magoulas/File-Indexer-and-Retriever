package Indexer;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class FileOpener {
	public static TreeMap<String,String> positions;
	public static ArrayList<String> file_paths;
	public static ArrayList<String> file_names;
	public static double files_count=0;
	static int new_files_count=0;
	static Iterator<String> stopper;

	
	
	public static void listFilesForFolder(File folder) {        
		for (File fileEntry : folder.listFiles()) {            
			if (fileEntry.isDirectory()) {                
				listFilesForFolder(fileEntry);            
				} else { 
					if(fileEntry.getAbsolutePath().contains(".nxml")) {
					file_paths.add(fileEntry.getAbsolutePath());
					}
				}        
			}    
		}
	
	 static int wordcount(String string)  
     {  
       int count=0;  
   
         char ch[]= new char[string.length()];     
         for(int i=0;i<string.length();i++)  
         {  
             ch[i]= string.charAt(i);  
             if( ((i>0)&&(ch[i]!=' ')&&(ch[i-1]==' ')) || ((ch[0]!=' ')&&(i==0)) )  
                 count++;  
         }  
         return count;  
     } 
	
    public static void indexer() throws IOException {

    	while(true) {
    	positions = new TreeMap<String,String>();
		long pos;
		
    	File folder = new File(queryevaluator.med_coll_path);
    	file_paths=new ArrayList<String>(1);
    	file_names=new ArrayList<String>(1);
    	

    	document_processor token=new document_processor();
    	//key=word String
    	TreeMap<String,Triplet<String,String,String,String>> stemmed_words=new TreeMap<String,Triplet<String,String,String,String>>();
    	TreeMap<Integer,Freq<String,String,String>> term_freq_per_doc=new TreeMap<Integer,Freq<String,String,String>>();
    	TreeMap<String,Integer> df_tree=new TreeMap<String,Integer>();
    	//TreeMap<String,Integer> idf_tree=new TreeMap<String,Integer>();
    	
    	listFilesForFolder(folder);
    	
    	Iterator<String>itr1=file_paths.iterator();
    	if(stopper!=null) {
    		itr1=stopper;
    	}
    	long stop=0;
		while(itr1.hasNext()) {
    		try {
    			if(stop==150) {//xwrizw ta arxeia se 500ades
    				stopper=itr1;
    				break;
    			}
				stemmed_words=token.tokenizer(itr1.next(), stemmed_words,positions);
				stopper=itr1;
				stop++;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	};
    	new_files_count++;
		RandomAccessFile file=new RandomAccessFile(queryevaluator.index_folder+"\\VocabularyFile-"+Integer.toString(new_files_count)+".txt", "rw");

		RandomAccessFile file_post=new RandomAccessFile(queryevaluator.index_folder+"\\PostingFile-"+Integer.toString(new_files_count)+".txt", "rw");
    	
    	files_count+=file_names.size();
    	
    	int index=0;
    	if(!stemmed_words.isEmpty()) {
    		for(String word: stemmed_words.keySet()){//pernw df , document, stemmed word. H stemmed_words perixei ola ta dedomena.
    			try {
    				int df = wordcount(stemmed_words.get(word).getPmcid())-1;
    				df_tree.put(stemmed_words.get(word).getWord(), df);
					String[] tf_occurance=stemmed_words.get(word).getOccurance().split("-");
					String[] tf_doc=stemmed_words.get(word).getPmcid().split(" +");
    				pos=file_post.getFilePointer();
					file.writeBytes(stemmed_words.get(word).getTag()+" "+stemmed_words.get(word).getWord()+df+" "+pos+"\n");

					for(int i = 0; i<tf_occurance.length;i++) {
						//System.out.println("stemmed word: "+stemmed_words.get(word).getWord()+" tf_occurance: "+tf_occurance[i]+" tf_doc: "+tf_doc[i+1]);
						Freq<String,String,String> node_data = new Freq<String,String,String>(tf_doc[i+1],stemmed_words.get(word).getWord(),tf_occurance[i]);
						String pos1 = positions.get(word+" "+tf_doc[i+1]);
						file_post.writeBytes(tf_doc[i+1]+" |"+tf_occurance[i]+" "+pos1+"\n");
						//file_post.writeBytes(pos+" "+tf_doc[i+1]+" |"+tf_occurance[i]+"\n");
						term_freq_per_doc.put(index, node_data);
						index++;
					}		
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			//System.out.println(node.getTag()+" "+node.getWord()+" "+node.getOccurance()+" "+node.getPmcid());
    			}
    		
    	}
    	file_post.close();

    	file.close();
		
		if(!stopper.hasNext()) {
			break;
		}
		positions=null;
    }
    	
    }
}
