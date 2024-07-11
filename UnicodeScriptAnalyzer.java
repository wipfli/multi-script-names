import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Character.UnicodeScript;

public class UnicodeScriptAnalyzer {

    // Method to get the Unicode script of a character
    private static UnicodeScript getScript(char ch) {
        return Character.UnicodeScript.of(ch);
    }

    public static void main(String[] args) {


        String filePath = "names.csv";
        Map<UnicodeScript, Integer> scriptCounts = new HashMap<>();

        // Try-with-resources statement to ensure the reader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                // Analyze each character in the line
                for (char ch : line.toCharArray()) {
                    UnicodeScript script = getScript(ch);

                    // Update the count for the detected script
                    scriptCounts.put(script, scriptCounts.getOrDefault(script, 0) + 1);
                }
            }
        } catch (IOException e) {
            // Handle any I/O errors
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }

        // Convert the map to a list of entries and sort it by the count values
        List<Map.Entry<UnicodeScript, Integer>> sortedScriptCounts = new ArrayList<>(scriptCounts.entrySet());
        sortedScriptCounts.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Print the sorted results
        System.out.println("Character counts by Unicode script (sorted by count):");
        for (Map.Entry<UnicodeScript, Integer> entry : sortedScriptCounts) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// The output looks like this:

// ➜  multi-script-names java UnicodeScriptAnalyzer.java
// Character counts by Unicode script (sorted by count):
// LATIN: 294756627
// COMMON: 42836269
// CYRILLIC: 19863635
// HAN: 10078507
// ARABIC: 7567898
// HANGUL: 1620444
// KATAKANA: 1479931
// GREEK: 1062089
// THAI: 869068
// HIRAGANA: 568196
// HEBREW: 521654
// MYANMAR: 362502
// GEORGIAN: 346379
// DEVANAGARI: 244096
// BENGALI: 211565
// ARMENIAN: 126585
// LAO: 45920
// ETHIOPIC: 44721
// KHMER: 43829
// TIFINAGH: 22182
// MONGOLIAN: 16111
// INHERITED: 15601
// TIBETAN: 14073
// MALAYALAM: 10775
// GUJARATI: 8679
// TAMIL: 8525
// UNKNOWN: 2604
// KANNADA: 1987
// CANADIAN_ABORIGINAL: 1483
// TELUGU: 850
// GURMUKHI: 752
// SINHALA: 587
// YI: 485
// THAANA: 336
// SYRIAC: 243
// BOPOMOFO: 140
// NKO: 99
// CHEROKEE: 60
// JAVANESE: 37
// ORIYA: 35
// GLAGOLITIC: 31
// COPTIC: 29
// OL_CHIKI: 11
// TAGBANWA: 2
// OGHAM: 2
// RUNIC: 1
// LISU: 1
