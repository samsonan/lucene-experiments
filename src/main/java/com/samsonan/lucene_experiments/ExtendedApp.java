package com.samsonan.lucene_experiments;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import static com.samsonan.lucene_experiments.Indexer.FILE_PATH;

public class ExtendedApp {

	String indexDir = "C:\\working.folder\\workspace\\lucene-experiments\\data\\index";
	String dataDir = "C:\\working.folder\\workspace\\lucene-experiments\\data\\data";
	
	Indexer indexer;
	Searcher searcher;

	public static void main(String[] args) {
		ExtendedApp tester;
		try {
			tester = new ExtendedApp();
			tester.createIndex();
			tester.search("second");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void createIndex() throws IOException {
		
		indexer = new Indexer(indexDir);

		long startTime = System.currentTimeMillis();
		int numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		
		indexer.close();
		
		System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
	}

	private void search(String searchQuery) throws IOException, ParseException {
		
		searcher = new Searcher(indexDir);
		
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();

		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(FILE_PATH));
		}
		
		searcher.close();
	}

}
