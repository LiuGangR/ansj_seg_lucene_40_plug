package org.ansj.lucene4;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class AnsjAnalysisTest {

	@Test
	public void test() throws IOException, ParseException {
		Analyzer analyzer = new AnsjAnalysis();
		String sentence = "我从小就不由自主地认为自己长大以后一定得成为一个象我父亲一样的画家, 可能是父母潜移默化的影响。其实我根本不知道作为画家意味着什么，我是否喜欢，最重要的是否适合我，我是否有这个才华。其实人到中年的我还是不确定我最喜欢什么，最想做的是什么？我相信很多人和我一样有同样的烦恼。毕竟不是每个人都能成为作文里的宇航员，科学家和大教授。知道自己适合做什么，喜欢做什么，能做好什么其实是个非常困难的问题。"
				+ "幸运的是，我想我的孩子不会为这个太过烦恼。通过老大，我慢慢发现美国高中的一个重要功能就是帮助学生分析他们的专长和兴趣，从而帮助他们选择大学的专业和未来的职业。我觉得帮助一个未成形的孩子找到她未来成长的方向是个非常重要的过程。"
				+ "美国高中都有专门的职业顾问，通过接触不同的课程，和各种心理，个性，兴趣很多方面的问答来帮助每个学生找到最感兴趣的专业。这样的教育一般是要到高年级才开始， 可老大因为今年上计算机的课程就是研究一个职业走向的软件项目，所以她提前做了这些考试和面试。看来以后这样的教育会慢慢由电脑来测试了。老大带回家了一些试卷，我挑出一些给大家看看。这门课她花了2个多月才做完，这里只是很小的一部分。"
				+ "在测试里有这样的一些问题："
				+ "你是个喜欢动手的人吗？ 你喜欢修东西吗？你喜欢体育运动吗？你喜欢在室外工作吗？你是个喜欢思考的人吗？你喜欢数学和科学课吗？你喜欢一个人工作吗？你对自己的智力自信吗？你的创造能力很强吗？你喜欢艺术，音乐和戏剧吗？  你喜欢自由自在的工作环境吗？你喜欢尝试新的东西吗？ 你喜欢帮助别人吗？你喜欢教别人吗？你喜欢和机器和工具打交道吗？你喜欢当领导吗？你喜欢组织活动吗？你什么和数字打交道吗？";

		System.out.println("start: " + (new Date()).getTime());
		long before = System.currentTimeMillis();

		RAMDirectory ramDirectory = new RAMDirectory();

		// 建立索引
		IndexWriterConfig ic = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		IndexWriter iwriter = new IndexWriter(ramDirectory, ic);
		Document doc = new Document();
		Field field = new TextField("text", sentence, Store.YES);
		doc.add(field);
		iwriter.addDocument(doc);
		iwriter.commit();
		iwriter.close();

		AnsjAnalysis aas = new AnsjAnalysis();
		// 读取索引
		QueryParser parser = new QueryParser(Version.LUCENE_40, "text", aas);
		Query query = parser.parse(sentence);
		System.out.println(query);
		DirectoryReader open = DirectoryReader.open(ramDirectory);
		IndexSearcher indexSearcher = new IndexSearcher(open);

		TopDocs docs = indexSearcher.search(query, null, 100);

		System.out.println(docs.totalHits);

		for (int i = 0; i < docs.totalHits; i++) {
			System.out.println(indexSearcher.doc(docs.scoreDocs[i].doc).get("text"));
		}

		long now = System.currentTimeMillis();
		System.out.println("time: " + (now - before) / 1000.0 + " s");
	}
}
