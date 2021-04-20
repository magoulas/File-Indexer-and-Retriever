package Indexer;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class queryevaluator {
	public static String index_folder=null;
	public static String stopword_folder=null;
	public static String med_coll_path=null;
	public static void main(String[] args) throws IOException {
		System.out.println("Give the medical collection absolute path. eg: C:\\Users\\strsi\\Desktop\\Java_Workspace\\BackupDocuments\\00");
		Scanner scanner = new Scanner(System.in);
		med_coll_path=scanner.nextLine();

		System.out.println("Give the index folder absolute path. eg: C:\\Users\\strsi\\Desktop\\Java_Workspace\\example_files");
		index_folder=scanner.nextLine();

		System.out.println("Give the stopwords folder absolute path. eg:C:\\Users\\strsi\\Desktop\\Java_Workspace\\example-files\\stopwords");
		stopword_folder=scanner.nextLine();
		
		double startTime = System.nanoTime();		
		
		try {
			FileOpener.indexer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		merger.merge_files();
		
		
		
		Weights w = new Weights();
		w.weights();
		
		double endTime = System.nanoTime();
		double timeElapsed = endTime - startTime;
		System.out.println("Execution time in nanoseconds  : " + timeElapsed);
		System.out.println("Execution time in milliseconds : " + 
				timeElapsed / 1000000);
		System.out.println("Execution time in seconds : " + 
				timeElapsed / 1000000/1000);
		System.out.println("Execution time in minutes : " + 
				timeElapsed / 1000000/1000/60);
		scanner.close();
	}
	
}
