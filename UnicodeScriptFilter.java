import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.HashSet;
import java.util.Set;

public class UnicodeScriptFilter {

    // Method to get the Unicode script of a character, excluding COMMON and INHERITED
    private static UnicodeScript getScript(char ch) {
        UnicodeScript script = Character.UnicodeScript.of(ch);
        if (script == UnicodeScript.COMMON || script == UnicodeScript.INHERITED) {
            return null;
        }
        return script;
    }

    public static void main(String[] args) {
        // Check if the input file path is provided as a command line argument


        String inputFilePath = "names.csv";
        String outputFilePath = "out.txt";

        // Try-with-resources statement to ensure the readers and writers are closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                Set<UnicodeScript> scriptsInLine = new HashSet<>();

                // Analyze each character in the line
                for (char ch : line.toCharArray()) {
                    UnicodeScript script = getScript(ch);
                    if (script != null) {
                        scriptsInLine.add(script);
                    }
                }

                if (scriptsInLine.size() == 2 && scriptsInLine.contains(UnicodeScript.HAN)) {
                    if (scriptsInLine.contains(UnicodeScript.HIRAGANA)) {
                        continue;
                    }
                    if (scriptsInLine.contains(UnicodeScript.KATAKANA)) {
                        continue;
                    }
                }

                // Check if the line contains more than one unique script
                if (scriptsInLine.size() > 1) {
                    System.out.println(line);
                    System.out.println(scriptsInLine);
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            // Handle any I/O errors
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }
}
