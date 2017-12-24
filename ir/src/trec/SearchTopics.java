package trec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.nio.file.Paths;

/**
 * SearchTopics
 */
public class SearchTopics {

    public static Param trainBM25Param(String queriesFile, String indexFolder, String qrelsFile)
            throws DocumentException, IOException, ParseException {
        System.out.println("training bm25 params k1 and b ... ...");

        String tmpFile = "../data/tmp.txt";
        Param pa = new Param(1, 1); // initial k1=1, b=1

        float NDCG = 0;
        float k1 = (float)0.5;
        while (k1 <= 5.0) {
            float b = (float)0;
            while (b <= 1.0) {
                MyQueryTopics(queriesFile, indexFolder, tmpFile, new Param(k1, b));

                String command = "perl ../data/sample_eval.pl " + qrelsFile + " " + tmpFile;
                System.out.println(command);
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = br.readLine();
                line = br.readLine();

                System.out.println("line : " + line);
                System.out.println("ret[4]: " + line.split("\t")[4]);
                float ret = Float.parseFloat(line.split("\t")[4]);

                if (ret > NDCG) {
                    pa.set(k1, b);
                    NDCG = ret;
                }
                System.out.println(k1 + " " + b + "ret=" + ret + " NDCG=" + NDCG);
                b += 0.1;
            }
            k1 += 0.2;
        }

        return pa;
    }

    public static void MyQueryTopics(String queriesFile, String indexFolder, String resultFile, Param pa)
            throws DocumentException, IOException, ParseException {
        ArrayList<MyQuery> arrayQ = parseFile(queriesFile);
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexFolder)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("text", analyzer);

        searcher.setSimilarity(new BM25Similarity(pa.k1, pa.b));
        FileWriter fw = new FileWriter(new File(resultFile));

        for (MyQuery q : arrayQ) {
            TopDocs results = searcher.search(parser.parse(q.getSummary()), 1000);
            ScoreDoc[] hits = results.scoreDocs;
            int i = 0;
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String docID = doc.get("doc_id");
                String line = q.getNum() + " Q0 " + docID + " " + (i++) + " " + hit.score + " " + "\n";
                fw.write(line);
            }
        }
        fw.close();
    }

    public static ArrayList<MyQuery> parseFile(String filename) throws DocumentException, IOException, ParseException {
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(new File(filename));
        List<Element> topics = document.getRootElement().elements();

        ArrayList<MyQuery> arrayQ = new <MyQuery>ArrayList();

        for (Element topic : topics) {
            String type = topic.attributeValue("type");
            String num = topic.attributeValue("number");
            String summary = topic.element("summary").getText().trim();
            summary += " " + type;
            summary = summary.replace(".", " ").replace(",", " ").replace("'", " ");

            arrayQ.add(new MyQuery(type.trim(), num.trim(), summary.trim()));
        }
        return arrayQ;
    }

    public static void main(String[] args) {
        try {
            String queries14 = "../data/topics2014.xml";
            String queries15 = "../data/topics2015A.xml";
            String indexFolder = "../data/index";
            String qrelsFile14 = "../data/qrels2014.txt";
            String queryResult15A = "../data/queryResult15A.txt";
            Param pa = trainBM25Param(queries14, indexFolder, qrelsFile14);
            System.out.println("training param k1=" + pa.k1 + " b=" + pa.b);
            MyQueryTopics(queries15, indexFolder, queryResult15A, pa);
        } catch (Exception e) {
            //TODO: handle exception
        }

    }
}

/**
 * MyQuery
 */
class MyQuery {

    private String type;
    private String num;
    private String summary;

    public MyQuery(String type, String num, String summary) {
        this.type = type;
        this.num = num;
        this.summary = summary;
    }

    /**
     * @return the num
     */
    public String getNum() {
        return num;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}

/**
 * Param
 */
class Param {
    public float k1;
    public float b;

    public Param(float k1, float b) {
        this.k1 = k1;
        this.b = b;
    }

    public void set(float k1, float b) {
        this.k1 = k1;
        this.b = b;
    }
}