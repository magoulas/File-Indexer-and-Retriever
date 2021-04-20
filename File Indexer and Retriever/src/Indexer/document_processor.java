package Indexer;
import gr.uoc.csd.hy463.NXMLFileReader;
import mitos.stemmer.Stemmer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import java.io.*; 

public class document_processor {
	
//	private static final Pattern UNWANTED_SYMBOLS = Pattern.compile("(?:--|[\\[\\]{}()+/\\\\]|,|\\.|;|-|\"|#|@|!|^|$|&|\\*|~)");
//	private static final Pattern UNWANTED_SYMBOLS = Pattern.compile("(\\(|\\)|-|,|\\.|\\:|/|[|])");
	private static final Pattern UNWANTED_SYMBOLS = Pattern.compile("[^a-zA-Z0-9% ]");

	
	public static String unwantedMatching(String string) {
		String clean;
		
		Matcher unwantedMatcher = UNWANTED_SYMBOLS.matcher(string);
		clean = unwantedMatcher.replaceAll("");
		
		return clean;
	}
	
	
	// Generic function to merge two sets in Java
	public static<T> Set<T> mergeSets(Set<T> a, Set<T> b)
	{
		Set<T> set = new HashSet<>(a);
		set.addAll(b);

		return set;
	}
	
	
	public TreeMap<String, Triplet<String, String, String, String>> tokenizer(String path, TreeMap<String,Triplet<String,String,String,String>> stemmed_words,TreeMap<String,String> position) throws UnsupportedEncodingException, IOException 
	{

	File example = new File(path);
	File stopwordsEN = new File(queryevaluator.stopword_folder+"\\stopwordsEn.txt");
	File stopwordsGR = new File(queryevaluator.stopword_folder+"\\stopwordsGr.txt");
	
	StringBuilder content = new StringBuilder();
	try (Stream<String> stream= Files.lines(Paths.get(path), StandardCharsets.UTF_8)){
			stream.forEach(s->content.append(s).append("\n"));
	}
	catch (IOException e) {
		e.printStackTrace();
	}

	
	Stemmer.Initialize();
	
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
 
	final Set<Triplet<String, String, Integer, String>>occurance_list = new HashSet<>();

	NXMLFileReader xmlFile = new NXMLFileReader(example);
	
	String pmcid = xmlFile.getPMCID();       
	String title = xmlFile.getTitle();        
	String abstr = xmlFile.getAbstr();        
	String body = xmlFile.getBody();        
	String journal = xmlFile.getJournal();        
	String publisher = xmlFile.getPublisher();       
	ArrayList<String> authors = xmlFile.getAuthors();        
	HashSet<String> categories =xmlFile.getCategories();
	
	FileOpener.file_names.add(pmcid);

	String authors_string="";
	for(String s : authors) {
		authors_string+= s + " ";
	}
		
	title = unwantedMatching(title).toLowerCase();
	abstr = unwantedMatching(abstr).toLowerCase();
	body = unwantedMatching(body).toLowerCase();
	journal = unwantedMatching(journal).toLowerCase();
	publisher = unwantedMatching(publisher).toLowerCase();
	authors_string = unwantedMatching(authors_string).toLowerCase();

	
	String the_title=title;
	String the_abstr=abstr;
	String the_body=body;
	String the_journal=journal;
	String the_publisher=publisher;
	String the_authors_string=authors_string;
	
	Set<String> stopwordsEN_set = new HashSet<String>(Arrays.asList(stop_string_EN.split(" +"))); // metatrepw to string se set.
	Set<String> stopwordsGR_set = new HashSet<String>(Arrays.asList(stop_string_GR.split(" +"))); // metatrepw to string se set.


	Set<String> title_set = new HashSet<String>(Arrays.asList(title.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
	Set<String> abstr_set = new HashSet<String>(Arrays.asList(abstr.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
	Set<String> body_set = new HashSet<String>(Arrays.asList(body.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
	Set<String> journal_set = new HashSet<String>(Arrays.asList(journal.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
	Set<String> publisher_set = new HashSet<String>(Arrays.asList(publisher.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
	Set<String> authors_set = new HashSet<String>(Arrays.asList(authors_string.split(" +"))); // metatrepw to string se set gia na exw monadikes lekseis.
//	Set<String> merge_set = new HashSet<String>(); // metatrepw to string se set gia na exw monadikes lekseis.

	title_set.removeAll(stopwordsEN_set);
	title_set.removeAll(stopwordsGR_set);
	
	abstr_set.removeAll(stopwordsEN_set);
	abstr_set.removeAll(stopwordsGR_set);
	
//	merge_set = mergeSets(title_set,abstr_set);
	
	body_set.removeAll(stopwordsEN_set);
	body_set.removeAll(stopwordsGR_set);
	
//	merge_set = mergeSets(merge_set,body_set);

	
	journal_set.removeAll(stopwordsEN_set);
	journal_set.removeAll(stopwordsGR_set);
	
//	merge_set = mergeSets(merge_set,journal_set);

	
	publisher_set.removeAll(stopwordsEN_set);
	publisher_set.removeAll(stopwordsGR_set);
	
//	merge_set = mergeSets(merge_set,publisher_set);

	
	authors_set.removeAll(stopwordsEN_set);
	authors_set.removeAll(stopwordsGR_set);
	
//	merge_set = mergeSets(merge_set,authors_set);

	
	categories.removeAll(stopwordsEN_set);
	categories.removeAll(stopwordsGR_set);
	
	int index=0;
	List<String> ss=Arrays.asList(title.split(" +"));
	Iterator<String> liter=ss.iterator();
	
	while(liter.hasNext()) {
		String s=liter.next();
		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;
		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}
	liter=null;
	ss= Arrays.asList(abstr.split(" +"));

	liter=ss.iterator();

	while(liter.hasNext()) {
		String s=liter.next();

		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;

		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}
	liter=null;
	ss=Arrays.asList(body.split(" +"));

	liter=ss.iterator();

	while(liter.hasNext()) {
		String s=liter.next();

		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;

		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}
	liter=null;
	ss= Arrays.asList(journal.split(" +"));

	liter=ss.iterator();

	while(liter.hasNext()) {
		String s=liter.next();

		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;

		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}
	liter=null;
	ss= Arrays.asList(publisher.split(" +"));

	liter=ss.iterator();

	while(liter.hasNext()) {
		String s=liter.next();

		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;

		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}
	liter=null;
	ss=Arrays.asList(authors_string.split(" +"));

	liter=ss.iterator();

	while(liter.hasNext()) {
		String s=liter.next();

		if(s.isEmpty()) {
			index++;
			continue;
		}
		String stem=Stemmer.Stem(s);
		String key =stem+" "+pmcid;

		String get=position.get(key);
		if(get==null) {
			position.put(key,Integer.toString(index));

		}else {
			position.put(key,get+"-"+Integer.toString(index));
		}
		index++;
	}

	
//	merge_set = mergeSets(merge_set,categories);
/*
	merge_set.forEach(kappa->{
		if(kappa.isEmpty()) {
			return;
		}
		for (int i = -1; (i = content.indexOf(kappa, i + 1)) != -1; i++) {
			position.put(Stemmer.Stem(kappa), position.get(kappa)+" "+Integer.toString(i));
		}
	});*/
	title_set.forEach(title_word->{
		int occurance = StringUtils.countMatches(the_title, title_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("title",title_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	abstr_set.forEach(abstr_word->{
		int occurance = StringUtils.countMatches(the_abstr, abstr_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("abstr",abstr_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	body_set.forEach(body_word->{
		int occurance = StringUtils.countMatches(the_body, body_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("body",body_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	journal_set.forEach(journal_word->{
		int occurance = StringUtils.countMatches(the_journal, journal_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("journal",journal_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	publisher_set.forEach(publisher_word->{
		int occurance = StringUtils.countMatches(the_publisher, publisher_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("publisher",publisher_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	authors_set.forEach(authors_word->{
		int occurance = StringUtils.countMatches(the_authors_string, authors_word);
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("authors",authors_word,occurance,pmcid);
		occurance_list.add(cell);
	});
	
	categories.forEach(categories_word->{
		categories_word=categories_word.toLowerCase();
		int occurance = 1;
		Triplet<String, String, Integer, String> cell=new Triplet<String, String, Integer, String>("categories",categories_word,occurance,pmcid);
		occurance_list.add(cell);
	});

	
	
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("body");
		String stemmer_result=Stemmer.Stem(node.getWord());

		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("body |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);                      //thelw to plhthos to orou sto arxeio tou. thelw to max freq twn wrwn sto arxeio.  //thelw plhthos arxeion me ton oro. thelo sunoliko plhthos arxeiwn.
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
				//			 builder.append("-");
							 builder.append(tmp2);
				//			 builder.append(momo.getOccurance().substring(start + tmp.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("title");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("title |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
						//	 builder.append("-");
							 builder.append(tmp2);
						//	 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("abstr");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("abstr |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
						//	 builder.append("-");
							 builder.append(tmp2);
						//	 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("journal");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("journal |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
				//			 builder.append("-");
							 builder.append(tmp2);
				//			 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
				
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("publisher");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("publisher |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
					//		 builder.append("-");
							 builder.append(tmp2);
					//		 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
				
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("authors");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("authors |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
					//		 builder.append("-");

							 builder.append(tmp2);
					//		 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
				
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	occurance_list.forEach(node->{
		boolean i = node.getTag().equals("categories");
		String stemmer_result=Stemmer.Stem(node.getWord());
		String documents = pmcid;
		if(i) {
			Triplet<String, String, String, String> cell=new Triplet<String, String, String, String>("categories |",stemmer_result+" |",node.getOccurance().toString(),"| "+documents);
			if(stemmed_words.isEmpty()) {
				stemmed_words.put(stemmer_result,cell);
			}
			else {
				if(stemmed_words.get(stemmer_result)!=null) {
					Triplet<String,String,String, String> momo= stemmed_words.get(stemmer_result);
					String s=momo.getTag();
					String t=momo.getWord();
					boolean j=s.equals("body");
					j=t.contains(stemmer_result);
					if(j) {
						String id;
						String new_occurance;
						String tmp;
						String tmp2;
						int start;
						int count;
						if(!momo.getPmcid().contains(pmcid)) {
							 id = momo.getPmcid()+" "+pmcid;
							 new_occurance=momo.getOccurance()+"-"+node.getOccurance().toString();
						}else {
							 id = momo.getPmcid();
							 tmp = momo.getOccurance().substring(momo.getOccurance().lastIndexOf("-")+1);
							 count=Integer.parseInt(tmp)+node.getOccurance();
							 tmp2=Integer.toString(count);
							 start=momo.getOccurance().lastIndexOf(tmp);
							 StringBuilder builder = new StringBuilder();
							 builder.append(momo.getOccurance().substring(0, start));
					//		 builder.append("-");

							 builder.append(tmp2);
					//		 builder.append(momo.getOccurance().substring(start + tmp2.length()));
							 new_occurance=builder.toString();
						}
						momo.setOccurance(new_occurance);
						momo.setPmcid(id);
					}
				}
				else {
					stemmed_words.put(stemmer_result,cell);
				}
			}
		}

	});
	
	int m= stemmed_words.size();
	int n= occurance_list.size();
	
	System.out.println("stemmed_words: "+m+" occurance_list "+n+" pmcid: "+pmcid);
	/*stemmed_words.forEach(node->{
		System.out.println(node.getTag()+" "+node.getWord()+" "+node.getOccurance());
	});
	*/
	brEN.close();
	brGR.close();
	
	return stemmed_words;
	}
}
