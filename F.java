import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.opennlp.tools.tokenize.SimpleTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFProcessor {

    public static void main(String[] args) {
        String pdfPath = "/pf";
        String outputPathJson = "/pn";
        String outputPathData = "/pata";

        try {
            String content = readPDF(pdfPath);
            List<String> tokens = tokenizeText(content);
            Map<String, Integer> wordRankings = createWordRankings(tokens);

           
            writeToJson(outputPathJson, wordRankings);

            
            writePickle(outputPathData, wordRankings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readPDF(String pdfPath) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        return pdfTextStripper.getText(document);
    }

    private static List<String> tokenizeText(String text) {
        return SimpleTokenizer.INSTANCE.tokenize(text);
    }

    private static Map<String, Integer> createWordRankings(List<String> tokens) {
        Map<String, Integer> wordCount = new HashMap<>();
        for (String token : tokens) {
            wordCount.put(token, wordCount.getOrDefault(token, 0) + 1);
        }
        return sortByValueDescending(wordCount);
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
       
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    private static void writeToJson(String outputPath, Map<String, Integer> data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(new File(outputPath), data);
    }

    
}
