package indexing;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.FSDirectory;

/**
 * Indexer
 */
public class Indexer {

    private final IndexWriter writer;

    private final Analyzer analyzer;
    private int docs = 0;

    public Indexer(String location) throws IOException {

        Path p = Paths.get(location);
        analyzer = new EnglishAnalyzer();
        Directory dir = new NIOFSDirectory(p);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, config);
    }

    private void addDoc(String text, String doc_id) throws IOException {
        Document doc = new Document();
        HashMap<String, Integer> map = new HashMap();
        //index options 
        FieldType vec_field = new FieldType();
        vec_field.setIndexOptions(vec_field.indexOptions().DOCS_AND_FREQS_AND_POSITIONS);
        vec_field.setStored(true);
        vec_field.setStoreTermVectors(false);
        vec_field.setOmitNorms(false);

        doc.add(new Field("text", text, vec_field));
        doc.add(new StringField("doc_id", doc_id, Field.Store.YES));

        writer.addDocument(doc);

    }

    private void indexTrec(String files) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(files));
        String line;

        String[] terms;
        String doc_id = null;
        boolean index = false;
        StringBuilder doc = null;

        while ((line = br.readLine()) != null) {

            if (line.startsWith("</DOC>")) {
                index = false;
                addDoc(doc.toString(), doc_id);
            }

            if (line.contains("<DOC>")) {
                doc = new StringBuilder();
            }

            if (line.contains("<DOCNO>")) {
                doc_id = line.substring(7, line.lastIndexOf("</DOCNO>"));
            }

            terms = line.split(" ");

            for (String word : terms) {
                if (index) {
                    if (!((word.contains("<") && word.contains("<")))) {
                        doc.append(word).append(" ");
                    }
                }
            }

            if ((line.contains("<TITLE>")) || line.contains("<REFERENCE>") || line.contains("<BODY>")
                    || line.contains("<ABSTRACT>")) {
                index = true;
            }
        }
        br.close();

    }

    public void indexTrecs(File dirFile) {
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                indexTrecs(f);
            } else {
                try {
                    System.err.println("indexing " + f.getPath());
                    indexTrec(f.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() throws IOException {
        this.writer.close();
        System.out.println("writer close    ");
    }

    public static void main(String[] args) throws IOException {
        String trec_files = "../data/pmc-text";
        Indexer indexer = new Indexer("../data/index");
        File dirFile = new File(trec_files);
        indexer.indexTrecs(dirFile);

        indexer.close();
    }

}