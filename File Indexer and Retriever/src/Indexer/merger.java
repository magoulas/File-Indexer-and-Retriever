package Indexer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class merger {
	
	
	static Queue<String> voc_paths;
	static Queue<String> post_paths;
	
	private static final Pattern UNWANTED_SYMBOLS = Pattern.compile("\\|");

	public static String unwantedMatching(String string) {
		String clean;
		
		Matcher unwantedMatcher = UNWANTED_SYMBOLS.matcher(string);
		clean = unwantedMatcher.replaceAll(" ");
		
		return clean;
	}

	public static void listFilesForFolder(File folder) {        
		for (File fileEntry : folder.listFiles()) {            
			if (fileEntry.isDirectory()) {                
				listFilesForFolder(fileEntry);            
				} else { 
					if(fileEntry.getAbsolutePath().contains("VocabularyFile-")) {
					voc_paths.add(fileEntry.getAbsolutePath());
					}
				}        
			}    
		}
	
	public static void listFilesForFolderPost(File folder) {        
		for (File fileEntry : folder.listFiles()) {            
			if (fileEntry.isDirectory()) {                
				listFilesForFolderPost(fileEntry);            
				} else { 
					if(fileEntry.getAbsolutePath().contains("PostingFile-")) {
					post_paths.add(fileEntry.getAbsolutePath());
					}
				}        
			}    
		}
	
	public static void merge_files() throws IOException {
		while(true) {
    	voc_paths=new LinkedList<String>();
    	post_paths=new LinkedList<String>();
    	File folder = new File(queryevaluator.index_folder);
		listFilesForFolder(folder);
		listFilesForFolderPost(folder);
		RandomAccessFile file1;
		RandomAccessFile file2 = null;
		RandomAccessFile post1;
		RandomAccessFile post2;
		int i=0;

		if(voc_paths.size()==1 && post_paths.size()==1) {
			break;
		}
		Iterator<String> voc_iter=voc_paths.iterator();
		Iterator<String> post_iter=post_paths.iterator();

		while(voc_iter.hasNext() && post_iter.hasNext()) {//megethos arxeio mporei to ena na teleiwsei prin to allo.
			System.out.println("loop "+i);
			String path1=voc_iter.next();

			file1=new RandomAccessFile(path1,"rw");


			
			String	path2=voc_iter.next();
			file2=new RandomAccessFile(path2,"rw");

//			System.out.println(file1);
			if(file2!=null)
//			System.out.println(file2);
			FileOpener.new_files_count++;
			RandomAccessFile file_res=new RandomAccessFile(queryevaluator.index_folder+"\\VocabularyFile-"+Integer.toString(FileOpener.new_files_count)+".txt", "rw");
			RandomAccessFile file_post=new RandomAccessFile(queryevaluator.index_folder+"\\PostingFile-"+Integer.toString(FileOpener.new_files_count)+".txt", "rw");
			String p2=post_iter.next();
			post1=new RandomAccessFile(p2,"rw");

			String p3=post_iter.next();
			post2=new RandomAccessFile(p3,"rw");



			String line_file1;
			String line_file2;
			String line_post1 = null;
			String line_post2 = null;
			int index=0;


			
			line_file1=file1.readLine();
			line_file2 = file2.readLine();


			while(line_file1!=null) { // dibazw apo to prwto vocabulary file
				List<String> list_file1=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file1).split(" +")));
				while(list_file1.size()<4) {
					line_file1 = file1.readLine();
					list_file1=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file1).split(" +")));
				}
			//	System.out.println(list_file1+" 34214213412");
				String tag1,word1;//ftiaxnw ta dedomena.
				String tag2,word2;
				int tf1;
				long pointer1=0;
				int tf2;
				long pointer2=0;
				index=0;
				tag1=list_file1.get(index);
				word1=list_file1.get(index+1);
				if(list_file1.size()>4) {
					index=list_file1.size()-4;
				}
				System.out.println(index);
				System.out.println(list_file1);

				tf1=Integer.parseInt(list_file1.get(index+2));
				boolean skip=false;
				if(list_file1.size()>=4) {
					if(pointer1==Long.parseLong(list_file1.get(index+3))){
						skip=true;
					}else {
						pointer1=Long.parseLong(list_file1.get(index+3));
					}
				}
				while(index>0) {
					index--;
				}
				if(!skip) {
					post1.seek(pointer1);
					//line_post1=post1.readLine();
				}
				boolean flag=false;

				while(line_file2!=null) {//diabazw to deftero vocabulary
					List<String> list_file2=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file2).split(" +")));
					System.out.println(list_file2+"    merging");//ftiaxnw ta dedomena.
					while(list_file2.size()<4) {
						line_file2 = file2.readLine();
						list_file2=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file2).split(" +")));
					}
					tag2=list_file2.get(index);
					word2=list_file2.get(index+1);

					if(list_file2.size()>4) {
						index=list_file2.size()-4;
					}
					boolean skippo=false;
					System.out.println(index);
					System.out.println(list_file2);
					System.out.println(list_file2.size());

					tf2=Integer.parseInt(list_file2.get(index+2));
					if(list_file2.size()>=4) {
						if(pointer2==Long.parseLong(list_file2.get(index+3))){
							skippo=true;
						}else {
							pointer2=Long.parseLong(list_file2.get(index+3));
						}				
					}
					while(index>0) {
						index--;
					}
					if(!skippo) {
						post2.seek(pointer2);
						//line_post2=post2.readLine();
					}
					if(tag2=="abstr" && word2.equals(" 33")) {
						System.out.println(list_file2);
						System.exit(0);
					}
					if(word1.compareTo(word2)==0) {//an idies lekseis sta vocabulary
					//	System.out.println(1);

						if(tag1.compareTo(tag2)==0) {// an tag idia prosthetw df kai grafw leksh tag df.
							int k=tf1+tf2;
							long pointerr=file_post.getFilePointer();
							file_res.writeBytes(tag1+" | "+word1+" |"+k+" "+pointerr+"\n");
							for(int g=0; g<tf1;g++) {
						//		System.out.println(2);

								boolean rest=false;
								boolean done=false;

								line_post1=post1.readLine();//diabazw th prwth grammh apo ton pointer toy prwtou vocabulary sto posting

								if(line_post1==null) {
									continue;
								}
								List<String> list_post1=new ArrayList<String>(Arrays.asList(unwantedMatching(line_post1).split(" +")));
								if(list_post1.get(0).contains("null")) {
									continue;
								}
								int doc1=Integer.parseInt(list_post1.get(0));
								if(list_post1.get(1).contains("null")) {
									list_post1.add(1, "0");
								}
								int tfpost1=Integer.parseInt(list_post1.get(1));
								String s1=list_post1.get(2);
								if(!done) {
									long poin;
								for(int f=0 ;f<tf2;f++) {
								//	System.out.println(3);

									poin=post2.getFilePointer();//diabazw th prwth grammh apo ton poiter toy defterou vocabulary
									line_post2=post2.readLine();
									if(line_post2==null) {
										continue;
									}
									List<String> list_post2=new ArrayList<String>(Arrays.asList(unwantedMatching(line_post2).split(" +")));
									if(list_post1.get(0).contains("null")) {
										continue;
									}
									int doc2=Integer.parseInt(list_post2.get(0));
									if(list_post2.get(1).contains("null")) {
										list_post2.add(1, "0");
									}
									int tfpost2=Integer.parseInt(list_post2.get(1));
									String s2=list_post2.get(2);
									if(doc1==doc2) {//an idia documents pros8etw ta postings.
										int h=tfpost1+tfpost2;
										file_post.writeBytes(doc1+" |"+h+" "+s1+"-"+s2+"\n");
									}
									else if(doc1>doc2) {//alliws elegxw pio einai mikrotero kai megalutero.
										file_post.writeBytes(doc2+" |"+tfpost2+" "+s2+"\n");
										continue;
									}else if(doc1<doc2 && !rest) {//logo emfoleumenhs prepei na eleksw an to eksw post1 file exw documents mikrotera apo to mesa post2.
										file_post.writeBytes(doc1+" |"+tfpost1+" "+s1+"\n");
										post2.seek(poin);
										if(g==tf1-1) {
											rest=true;
										}else {
										break;
										}
									}
									if(rest) {//an teleiwsei to eksw posting1 grafw ta upoloipa mesa poisting2.
										for(int d=f; d<tf2; d++) {
											//System.out.println(4);

											line_post2=post2.readLine();
											file_post.writeBytes(line_post2+"\n");
										}
										rest=false;
										done=true;
										break;
									}
								}
								done=true;
								
								}
							}
							pointerr=file_post.getFilePointer();
							line_file1 = file1.readLine();
							line_file2 = file2.readLine();
							flag=true;

						}else {//an tag diaforetika grafw thn mia leksh meta thn allh.
							long pointerr = file_post.getFilePointer();
							file_res.writeBytes(tag1+" | "+word1+" |"+tf1+" "+pointerr+"\n");
							 pointerr = file_post.getFilePointer();
							file_res.writeBytes(tag2+" | "+word2+" |"+tf2+" "+pointerr+"\n");
							for(int g=0; g<tf1;g++) {//diaforetika tag ara diaforetika postings
								//System.out.println(5);

								line_post1=post1.readLine();
								file_post.writeBytes(line_post1+"\n");//diaforetika tag ara diaforetika postings
							}
							for(int g=0; g<tf2;g++) {
							//	System.out.println(6);

								line_post2=post2.readLine();
								file_post.writeBytes(line_post2+"\n");//diaforetika tag ara diaforetika postings
							}
							line_file1 = file1.readLine();
							line_file2 = file2.readLine();
							flag=true;

						}
					}else if (word1.compareTo(word2)>0) {// word1>word2 an word1 meta apo word2 grafw word2 kai proxoraw to inner loop.
						long pointerr = file_post.getFilePointer();
						file_res.writeBytes(tag2+" | "+word2+" |"+tf2+" "+pointerr+"\n");

						for(int g=0; g<tf2;g++) {
						//	System.out.println(7);

							line_post2=post2.readLine();
							file_post.writeBytes(line_post2+"\n");
						}
						line_file2 = file2.readLine();
					}else if(word1.compareTo(word2)<0) { // word1<word2 an word2 meta apo word1 grafw word1 kai bgainw apo inner loop gia na sunexisw sto next line tou inner loop
						if(flag) {
							flag=false;
							break;
						}else{
						long pointerr = file_post.getFilePointer();
						file_res.writeBytes(tag1+" | "+word1+" |"+tf1+" "+pointerr+"\n");

						for(int g=0; g<tf1;g++) {
						//	System.out.println(8);

							line_post1=post1.readLine();
							file_post.writeBytes(line_post1+"\n");
						}
						line_file1 = file1.readLine();

						break;
						}
					}
				
			//	System.out.println(list_file1);

			
				i++;

			//	System.out.println(list_file2);
				}
				if(line_file2==null && line_file1!=null) {
					break;
				}
			}
			//koitazw pio arxeio adeiase prwto kai pio arxeio exei akoma keimeno mesa.
			while(line_file1==null && line_file2!=null) {//deftero arxeio exei keimeno
			//	System.out.println(9);

				List<String> list_file2=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file2).split(" +")));
				String tag2,word2;
				int tf2;
				long pointer2=0;
				index=0;
				tag2=list_file2.get(index);
				word2=list_file2.get(index+1);
				if(list_file2.size()>4) {
					index++;
				}
				tf2=Integer.parseInt(list_file2.get(index+2));
				boolean skip=false;
				if(list_file2.size()>=4) {
					if(pointer2==Long.parseLong(list_file2.get(index+3))){
						skip=true;
					}else {
						pointer2=Long.parseLong(list_file2.get(index+3));
					}
				}
				if(index>0) {
					index--;
				}
				if(!skip) {
					post2.seek(pointer2);
					//line_post1=post1.readLine();
				}
				long pointerr = file_post.getFilePointer();
				file_res.writeBytes(tag2+" | "+word2+" |"+tf2+" "+pointerr+"\n");
				line_post2=post2.readLine();
				for(int g=0; g<tf2;g++) {
					//System.out.println(10);

					line_post2=post2.readLine();
					if(line_post2==null) {
						continue;
					}
					file_post.writeBytes(line_post2+"\n");
				}
				line_file1=file1.readLine();
				line_file2 = file2.readLine();
			}
			while(line_file2==null && line_file1!=null) {//prwto arxeio exei keimeno
				List<String> list_file1=new ArrayList<String>(Arrays.asList(unwantedMatching(line_file1).split(" +")));
				String tag1,word1;
				int tf1;
				long pointer1=0;
				index=0;
				tag1=list_file1.get(index);
				word1=list_file1.get(index+1);
				if(list_file1.size()>4) {
					index++;
				}
				tf1=Integer.parseInt(list_file1.get(index+2));
				boolean skip=false;
				if(list_file1.size()>=4) {
					if(pointer1==Long.parseLong(list_file1.get(index+3))){
						skip=true;
					}else {
						pointer1=Long.parseLong(list_file1.get(index+3));
					}
				}
				if(index>0) {
					index--;
				}
				if(!skip) {
					post1.seek(pointer1);
					//line_post1=post1.readLine();
				}
				long pointerr = file_post.getFilePointer();
				file_res.writeBytes(tag1+" | "+word1+" |"+tf1+" "+pointerr+"\n");
				line_post1=post1.readLine();
				for(int g=0; g<tf1;g++) {
					line_post1=post1.readLine();
					if(line_post1==null) {
						continue;
					}
					file_post.writeBytes(line_post1+"\n");
				}
				line_file1=file1.readLine();
				line_file2 = file2.readLine();
			}
			file_res.close();
			file_post.close();
			file1.close();
			file2.close();
			post1.close();
			post2.close();
			Files.deleteIfExists(Paths.get(path1));
			Files.deleteIfExists(Paths.get(path2));
			Files.deleteIfExists(Paths.get(p2));
			Files.deleteIfExists(Paths.get(p3));
			break;

		}
		
	}
		
	}
	
}
