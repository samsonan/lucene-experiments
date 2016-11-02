package com.samsonan.lucene_experiments;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import static com.samsonan.lucene_experiments.Indexer.CONTENTS;

public class Searcher {

	private IndexSearcher indexSearcher;
	private IndexReader indexReader;
	private QueryParser queryParser;
	private Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		
		Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
		
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		
		queryParser = new QueryParser(Version.LUCENE_44, CONTENTS,
				new StandardAnalyzer(Version.LUCENE_44));
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		//query = queryParser.parse(searchQuery);

		Filter filter = null;
		
		// option 1. find files with "second" as a part of a content
		//query = new TermQuery(new Term(CONTENTS, "second"));

		// option 2. find files without "second" as a part of a content
		query = new BooleanQuery();
		
		Query conditionQuery = new TermQuery(new Term(CONTENTS, "second"));
		MatchAllDocsQuery everyDocClause = new MatchAllDocsQuery();
		
		((BooleanQuery)query).add(everyDocClause , Occur.MUST);
		((BooleanQuery)query).add(conditionQuery, Occur.MUST_NOT);

		//option 3. user filter - find with "second"
//		Query query = new MatchAllDocsQuery();
//		filter = new TermsFilter(new Term(CONTENTS, "second"));

		//option 4. user filter - find withOUT "second"
//		BooleanFilter filter = new BooleanFilter();
//		Query query = new MatchAllDocsQuery();
//		Filter termsFilter = new TermsFilter(new Term(CONTENTS, "second"));
//		filter.add(new FilterClause(termsFilter, BooleanClause.Occur.MUST_NOT));
		
		return indexSearcher.search(query, filter, 10);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
	}
}
