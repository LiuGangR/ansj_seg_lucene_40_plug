package org.ansj.lucene4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public class Test {
	public static void main(String[] args) throws IOException, Exception {

		HashSet<String> stopWords = new HashSet<String>();
//		LoadStopWords(stopWords, new File("library\\stopword-list.txt"));
		File indexDir = new File("index");
		File docDir = new File("/Users/ansj/Desktop/搜索组分享/文本分类语料库/军事249");

		Analyzer analyzer = new AnsjAnalysis(stopWords, true);
		// System.out.println((analyzer.(null,
		// StringReader("international crim"))).toString());
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		IndexWriter standardWriter = new IndexWriter(FSDirectory.open(indexDir), iwc);
		for (File currentFile : docDir.listFiles()) {
			if (currentFile.isFile()) {
				Document doc = new Document();
				doc.add(new StringField("name", currentFile.getName(), Store.YES));
				doc.add(new StringField("path", currentFile.getAbsolutePath(), Store.YES));
				doc.add(new TextField("content", new BufferedReader(new InputStreamReader(new FileInputStream(currentFile),"GBK"))));
				standardWriter.addDocument(doc);
				System.out.println("Index file " + currentFile.getAbsolutePath() + " finished.");
			}
		}
		standardWriter.commit() ;
		standardWriter.close();
		
		
		File filePath = new File("index");
		Directory dir = FSDirectory.open(filePath);
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		// Query termQuery = new TermQuery(new Term("content","村上村树"));
		Analyzer aas = new AnsjAnalysis(stopWords, true);
		QueryParser parser = new QueryParser(Version.LUCENE_40, "content", aas);
		Query query = parser.parse("的");
		TopDocs top = searcher.search(query, 10);
		ScoreDoc[] docs = top.scoreDocs;
		System.out.println(docs.length);
		for (int i = 0; i < docs.length; i++) {
			System.out.println(searcher.doc(docs[i].doc).get("path") + "------" + docs[i].score);
		}

	}

	public static void LoadStopWords(HashSet<String> stopWords, File stopWordFile) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(stopWordFile));
		String currentLine = "";
		while ((currentLine = bufferedReader.readLine()) != null) {
			currentLine = currentLine.trim();
			stopWords.add(currentLine);
		}
	}
}
