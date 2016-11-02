package com.samsonan.lucene_experiments;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	public final static String CONTENTS = "contents";
	public final static String FILE_NAME = "filename";
	public final static String FILE_PATH = "filepath";

	private IndexWriter writer;

	public Indexer(String indexDirectoryPath) throws IOException {
		// this directory will contain the indexes
		Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));

		// create the indexer
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_44,
				new StandardAnalyzer(Version.LUCENE_44));
		writer = new IndexWriter(indexDirectory, writerConfig);
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	@SuppressWarnings("deprecation")
	private Document getDocument(File file) throws IOException {
		Document document = new Document();

		// index file contents
//		Field contentField = new Field(CONTENTS, new FileReader(file));

		String content = readFile(file.getAbsolutePath());
		System.out.println("content: "+content);
		
		Field contentField = new Field(CONTENTS, content, Field.Store.NO, Field.Index.ANALYZED);
		
		// index file name
		Field fileNameField = new Field(FILE_NAME, file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		// index file path
		Field filePathField = new Field(FILE_PATH, file.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED);

		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}

	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();

		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file);
			}
		}
		return writer.numDocs();
	}

	static String readFile(String path) throws IOException {
		Charset encoding = Charset.defaultCharset();
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
