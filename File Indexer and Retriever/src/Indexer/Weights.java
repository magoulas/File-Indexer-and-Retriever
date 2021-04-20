package Indexer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Weights {

	public void weights() throws IOException {
		RandomAccessFile file_weights=new RandomAccessFile(queryevaluator.index_folder+"\\WeightsFile.txt", "rw");
		RandomAccessFile file_distance=new RandomAccessFile(queryevaluator.index_folder+"\\DocumentsFile.txt", "rw");
		//RandomAccessFile file_voc=new RandomAccessFile("C:\\Users\\strsi\\Desktop\\Java_Workspace\\example-files\\VocabularyFile-"+Integer.toString(3)+".txt", "rw");
		//RandomAccessFile file_post=new RandomAccessFile("C:\\Users\\strsi\\Desktop\\Java_Workspace\\example-files\\PostingFile-"+Integer.toString(3)+".txt", "rw");
		RandomAccessFile file_idf=new RandomAccessFile(queryevaluator.index_folder+"\\IdfFile.txt", "rw");

		RandomAccessFile file_voc=new RandomAccessFile(queryevaluator.index_folder+"\\VocabularyFile-"+Integer.toString(FileOpener.new_files_count)+".txt", "rw");
		RandomAccessFile file_post=new RandomAccessFile(queryevaluator.index_folder+"\\PostingFile-"+Integer.toString(FileOpener.new_files_count)+".txt", "rw");
		
		TreeMap<Integer,word_doc_tf<String,Long,Long>> holy_grail=new TreeMap<>(); // krataei to word,doc,tf
		
		TreeMap<Long,Long> max_freq = new TreeMap<>();
		TreeMap<String,Integer> dfi = new TreeMap<>();//word-df


		int index=0;
		String voc_line=file_voc.readLine();
		while(voc_line != null){
			List<String> voc_list=new ArrayList<String>(Arrays.asList(merger.unwantedMatching(voc_line).split(" +")));
			if(voc_list.size()<4) {
				voc_line=file_voc.readLine();
				continue;
			}
			String word=voc_list.get(1);
			int df=Integer.parseInt(voc_list.get(2));
			long pointer=Long.parseLong(voc_list.get(3));
			file_post.seek(pointer);
			String post_line=file_post.readLine();
			long i = 0;
			System.out.println(word);
			while(post_line!=null && i<df) {
				List<String> post_list=new ArrayList<String>(Arrays.asList(merger.unwantedMatching(post_line).split(" +")));
				long doc=Long.parseLong(post_list.get(0));
				long tf=Long.parseLong(post_list.get(1));
				word_doc_tf<String,Long,Long> node=new word_doc_tf<String,Long,Long>(word,doc,tf); //data tuple
				holy_grail.put(index,node);
				index++;
				if(max_freq.isEmpty()) {
					max_freq.put(doc, tf);
				}else if(max_freq.get(doc)!=null) {
					if(max_freq.get(doc)<tf) {
						max_freq.put(doc, tf);
					}
				}else if(max_freq.get(doc)==null) {
					max_freq.put(doc, tf);
				}

				post_line=file_post.readLine();
				i++;
			}
			//if post_line==null && i<df thn gamhsa
			dfi.put(word, df);
			voc_line=file_voc.readLine();
		}

	    Set set = holy_grail.entrySet();
	    
	    // Get an iterator
	    Iterator it = set.iterator();
		TreeMap<Long,Double> distance = new TreeMap<>();//word-df
		TreeMap<String,Double> idfi_tree=new TreeMap<>();
	    // Display elements
	    while(it.hasNext()) {
	      Map.Entry me = (Map.Entry)it.next();
	      word_doc_tf<String,Long,Long> node = (word_doc_tf<String, Long, Long>) me.getValue();
	      String word=node.getWord();
	      Long doc=node.getDoc();
	      double tf=node.getTf();
	      double max_tf=max_freq.get(doc);
	      double tfij=tf/max_tf;
	      double df=dfi.get(word);
//	      Long idfi=(long) (Math.log(FileOpener.files_count/df)/Math.log(2));
	      double idfi=Math.log(FileOpener.files_count/df)/Math.log(2);
	      if(idfi_tree.isEmpty()) {
	    	  idfi_tree.put(word, idfi);
	      }else if(idfi_tree.get(word)==null) {
	    	  idfi_tree.put(word, idfi);
	      }
	      System.out.println("data: "+doc+" tf: "+tf+" max: "+max_tf+" tfij: "+tfij+" idfi: "+idfi+" df: "+df);
	      double wij=tfij*idfi;
	      double tetragwno=Math.pow(wij, 2);
	      file_weights.writeBytes(word+" "+doc+" "+wij+"\n");
	      if(distance.isEmpty()) {
	    	  distance.put(doc,tetragwno);
	      }else if(distance.get(doc)==null) {
	    	  distance.put(doc,tetragwno);
	      }else if(distance.get(doc)!=null) {
	    	  double sum=distance.get(doc)+tetragwno;
	    	  distance.put(doc, sum);
	      }
	    }
	    Set sett = distance.entrySet();
	    
	    // Get an iterator
	    Iterator itt = sett.iterator();

	    // Display elements
	    while(itt.hasNext()) {
		      Map.Entry mee = (Map.Entry)itt.next();
		      Iterator i = FileOpener.file_paths.iterator();
		      String n = null;
		      while(i.hasNext()) {
		    	   n= (String) i.next();
		    	  if(n.contains(mee.getKey().toString())) {
		    		 break; 
		    	  }
		      }
		      file_distance.writeBytes(mee.getKey()+" "+Math.sqrt((double) mee.getValue())+" "+n+"\n");
	    	
	    }
	    Set idf_set=idfi_tree.entrySet();
	    Iterator idf_it=idf_set.iterator();
	    
	    while(idf_it.hasNext()) {
	    	Map.Entry me=(Map.Entry) idf_it.next();
	    	file_idf.writeBytes(me.getKey()+" "+me.getValue()+"\n");
	    }
	    
	    
	    
	    file_idf.close();
    	file_distance.close();
    	file_weights.close();
    	file_voc.close();
    	file_post.close();
	}
}
/*
int k=0;
for(Iterator<String> fname=file_names.listIterator();fname.hasNext();){
	double sum=0;
	String name = fname.next();
	for(int i =0;i<indexx;i++){
		if(term_freq_per_doc.get(i).getPmcid().contains(name)) {
			double freqij= Integer.parseInt(term_freq_per_doc.get(i).gettf());      				
			double tfij=freqij/max_freq.get(term_freq_per_doc.get(i).getPmcid());
			double df_tree_double = df_tree.get(term_freq_per_doc.get(i).getWord());
			double idfi=Math.log(files_count/df_tree_double)/Math.log(2);
			double wij=tfij*idfi;
			sum+=Math.pow(wij, 2);
			//term_freq_per_doc.get(i).setTf(Double.toString(wij));
			try {
				file_tf.writeBytes(term_freq_per_doc.get(i).getPmcid()+" "+term_freq_per_doc.get(i).getWord()+" "+term_freq_per_doc.get(i).gettf()+" "+wij+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	try {
		file_distance.writeBytes(name+" ");
		file_distance.writeBytes(file_paths.get(k)+" ");
		file_distance.writeBytes(Math.sqrt(sum)+"\n");
		k++;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("doc: "+name+" dist: "+Math.sqrt(sum));



*/














