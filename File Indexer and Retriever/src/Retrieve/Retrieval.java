package Retrieve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mitos.stemmer.Stemmer;

public class Retrieval {
	private static final Pattern UNWANTED_SYMBOLS2 = Pattern.compile("[^a-zA-Z0-9% ]");
	private static String path;
	private static final Pattern UNWANTED_SYMBOLS = Pattern.compile("\\|");
	public static int max_query_frq=0;
	static String stopword_folder;
	public static String unwantedMatching(String string) {
		String clean;
		
		Matcher unwantedMatcher = UNWANTED_SYMBOLS.matcher(string);
		clean = unwantedMatcher.replaceAll(" ");
		
		return clean;
	}
	
	public static String unwantedMatching2(String string) {
		String clean;
		
		Matcher unwantedMatcher = UNWANTED_SYMBOLS2.matcher(string);
		clean = unwantedMatcher.replaceAll("");
		
		return clean;
	}
	
	
	public static TreeMap<String,Integer> query_processing(String query) throws IOException {
		List<String> query_words=new ArrayList<String>(Arrays.asList(unwantedMatching2(query).split(" +")));
		File stopwordsEN = new File(stopword_folder+"\\stopwordsEn.txt");
		File stopwordsGR = new File(stopword_folder+"\\stopwordsGr.txt");
		BufferedReader brEN = new BufferedReader(new FileReader(stopwordsEN));
		BufferedReader brGR = new BufferedReader(new FileReader(stopwordsGR));
		
		String stop_string_EN="";
		String word;
		  while ((word = brEN.readLine()) != null) {
			  stop_string_EN = stop_string_EN.concat(word);
			  stop_string_EN = stop_string_EN.concat(" ");
		  }
		  
		String stop_string_GR=""; 
		  while ((word = brGR.readLine()) != null) {
			  stop_string_GR = stop_string_GR.concat(word);
			  stop_string_GR = stop_string_GR.concat(" ");
		  } 
		Set<String> stopwordsEN_set = new HashSet<String>(Arrays.asList(stop_string_EN.split(" "))); // metatrepw to string se set. Ka8arizw to string kai to kobw sta kena gia na parw tis lekseis.
		Set<String> stopwordsGR_set = new HashSet<String>(Arrays.asList(stop_string_GR.split(" "))); // metatrepw to string se set.

		Set<String> query_set=new HashSet<String>(query_words);


		query_set.removeAll(stopwordsEN_set); //bgazw to 
		query_set.removeAll(stopwordsGR_set);

		Set<String> keys=new HashSet<String>();
				
		TreeMap<String,Integer> words_occurance = new TreeMap<>(); //brhskw ta frequncy tvn leksewn sto query
		int len=query_words.size();
		for(int i =0;i<len;i++) {
			String wordss=query_words.get(i);
			String wordsss=Stemmer.Stem(wordss);

			if(query_set.contains(wordss)) {
				if(words_occurance.isEmpty()) {
					keys.add(wordsss);
					words_occurance.put(wordsss, 1);
				}else if(words_occurance.get(wordsss)==null ) {
					keys.add(wordsss);
					words_occurance.put(wordsss,1);
				}else if(words_occurance.get(wordsss)!=null){
					int count=words_occurance.get(wordsss)+1;
					words_occurance.put(wordsss, count);
				}
			}
		}
		
		for(Iterator<String> i = keys.iterator();i.hasNext();) { //brhskw max freq gia kanonikopoihsh
			String qword=(String) i.next();
			if(words_occurance.get(qword)>max_query_frq) {
				max_query_frq=words_occurance.get(qword);
			}
		}
		
		brEN.close();
		brGR.close();
		return words_occurance;

	}
	
	public static void listFilesForFolder(File folder,String substring) {        
		for (File fileEntry : folder.listFiles()) {            
			if (fileEntry.isDirectory()) {                
				listFilesForFolder(fileEntry,substring);            
				} else { 
					if(fileEntry.getAbsolutePath().contains(substring)) {
					path=fileEntry.getAbsolutePath();
					}
				}        
			}    
		}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Give the index folder absolute path. eg: C:\\Users\\strsi\\Desktop\\Java_Workspace\\example_files");
		Scanner scanner = new Scanner(System.in);
		String index_folder=scanner.nextLine();

		System.out.println("Give the stopwords folder absolute path. eg:C:\\Users\\strsi\\Desktop\\Java_Workspace\\example-files\\stopwords");
		stopword_folder=scanner.nextLine();
		
    	File folder = new File(index_folder);
    	listFilesForFolder(folder,"WeightsFile.txt");
		String wp=path;
    	listFilesForFolder(folder,"DocumentsFile.txt");
    	String dp=path;
    	listFilesForFolder(folder,"VocabularyFile-");
    	String vp=path;
    	listFilesForFolder(folder,"PostingFile-");
    	String pp=path;
    	listFilesForFolder(folder,"IdfFile.txt");
    	String ip=path;
		RandomAccessFile file_weights=new RandomAccessFile(wp, "rw");
		RandomAccessFile file_distance=new RandomAccessFile(dp, "rw");
		RandomAccessFile file_voc=new RandomAccessFile(vp, "rw");
		RandomAccessFile file_post=new RandomAccessFile(pp, "rw");
		RandomAccessFile file_idf=new RandomAccessFile(ip, "rw");

		
		TreeMap<String,Integer> word_df=new TreeMap<>();
		TreeMap<String,Long> word_pointer=new TreeMap<>();
		TreeMap<String,Double> weights=new TreeMap<>();
		TreeMap<String,Double> idfi_tree=new TreeMap<>();
		TreeMap<String,Double> distance_tree=new TreeMap<>();

		ArrayList<String> docs=new ArrayList<String>(1);
		
		Stemmer.Initialize();
		
		String voc_line=file_voc.readLine();
		while(voc_line!=null) { //apo to vocabulary file
			List<String> voc_list=new ArrayList<String>(Arrays.asList(unwantedMatching(voc_line).split(" +")));
			String tag_word=voc_list.get(0)+" "+voc_list.get(1);
			word_df.put(tag_word, Integer.parseInt(voc_list.get(2)));
			word_pointer.put(tag_word, Long.parseLong(voc_list.get(3)));
			voc_line=file_voc.readLine();
		}
		
		String weight_line=file_weights.readLine();
		while(weight_line!=null) {//apo to weight file
			List<String> weight_list=new ArrayList<String>(Arrays.asList(unwantedMatching(weight_line).split(" +")));
			String word_doc=weight_list.get(0)+" "+weight_list.get(1);

			weights.put(word_doc, Double.parseDouble(weight_list.get(2)));
			weight_line=file_weights.readLine();
			
		}
		
		String doc_line=file_distance.readLine();
		while(doc_line!=null) {//apo to document file
			List<String> doc_list=new ArrayList<String>(Arrays.asList(unwantedMatching(doc_line).split(" +")));
			String doc=doc_list.get(0);
			//idfi_tree.put(doc, Double.parseDouble(doc_list.get(2)));
			distance_tree.put(doc, Double.parseDouble(doc_list.get(1)));
			docs.add(doc);
			doc_line=file_distance.readLine();
		}
		
		String idf_line=file_idf.readLine();
		while(idf_line!=null) {
			List<String> idf_list=new ArrayList<String>(Arrays.asList(unwantedMatching(idf_line).split(" +")));
			idfi_tree.put(idf_list.get(0), Double.parseDouble(idf_list.get(1)));
			idf_line=file_idf.readLine();

		}
		
		System.out.println("Give me query: ");

		String query;
		while((query=scanner.nextLine())!=null) {
		TreeMap<String,Integer> query_words_occurance = new TreeMap<>();
		query_words_occurance=query_processing(query);
		
		Set<String> query_words=new HashSet<String>();
		query_words=query_words_occurance.keySet();

		TreeMap<Double,String> rankings=new TreeMap<>(Collections.reverseOrder());
		
		double query_dist=0;
		double dist_sum=0;
		for(Iterator<String> qword=query_words.iterator();qword.hasNext();) {//upologismos uporizou gia to mhkws dianusmatos query
			String qwords=(String) qword.next();
			if(idfi_tree.get(qwords)!=null){
				double qtf=query_words_occurance.get(qwords)/max_query_frq;
				double idf=idfi_tree.get(qwords);
				double wiq=qtf*idf;
				dist_sum+=Math.pow(wiq, 2);
			}
		}
		query_dist=Math.sqrt(dist_sum);
		for(int i = 0 ; i<docs.size();i++) {//upologismos ari8mith sto cosine 
			String doc=docs.get(i);
			double wsum=0;
			double doc_dist=distance_tree.get(doc);
			for(Iterator<String> qword=query_words.iterator();qword.hasNext();) {
				String qwords=(String) qword.next();
				String word_doc=qwords+" "+doc;
				if(weights.get(word_doc)!=null) {
					double wij=weights.get(word_doc);
					double qtf=query_words_occurance.get(qwords)/max_query_frq;
					double idf=idfi_tree.get(qwords);
					double wiq=qtf*idf;
					wsum+=wiq*wij;
				}
			}
			double result=wsum/(doc_dist*query_dist);
			if(wsum!=0) {
			rankings.put(Math.cos(result), doc);
			}
		}

		for(Entry<Double, String> entry:rankings.entrySet()) {
			System.out.println("Score: "+entry.getKey()+" Document: "+entry.getValue());
		}
		System.out.println("Give me query: ");

		}
		scanner.close();

		
		file_idf.close();
		file_weights.close();
		file_distance.close();
		file_voc.close();
		file_post.close();
	}
}
 