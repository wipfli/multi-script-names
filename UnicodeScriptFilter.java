import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UnicodeScriptFilter {

    // Method to get the Unicode script of a character, excluding COMMON and INHERITED
    private static UnicodeScript getScript(char ch) {
        UnicodeScript script = Character.UnicodeScript.of(ch);
        if (script == UnicodeScript.COMMON || script == UnicodeScript.INHERITED || script == UnicodeScript.UNKNOWN) {
            return null;
        }
        return script;
    }

    // ZWSP:
    // ​吐尕力阿尕什村 تۇعىلاعاش
    // རི་མདོ་སྒང་གྲོང་ཚོ། (仁多岗)
    public static String cleanEndsAndZWSP(String input) {
        String result = input.strip();
        if (result.endsWith("/") || 
        result.endsWith("-") || 
        result.endsWith(";") || 
        result.endsWith("(") ||
        result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
            result = result.strip();
        }
        if (result.endsWith(")") && !result.contains("(")) {
            result = result.substring(0, result.length() - 1);
            result = result.strip();
        }
        result = result.replace("\u200B", ""); // remove ZWSP
        return result;
    }

    // Tazaγart for "γ"
    // Бежантыхъæу for "æ"
    // Æлбортыхъæу for "Æ"
    public static List<String> foreignScriptFix(List<String> segments) {

        List<String> result = new ArrayList<>();

        if (segments.size() < 2) {
            return segments;
        }
        // Iterate over the list of strings
        for (int i = 0; i < segments.size(); i++) {
            String prev = 0 <= i - 1 ? segments.get(i - 1) : "";
            String current = segments.get(i);
            String next = i + 1 <= segments.size() - 1 ? segments.get(i + 1) : "";

            if (current.equals("γ") || 
            current.equals("æ") ||
            current.equals("Æ")) {
                String segment = prev + current + next;
                if (result.size() == 0) {
                    result.add(segment);
                }
                else {
                    result.set(result.size() - 1, segment);
                }
                i++; // skip next
            }
            else {
                result.add(current);
            }
        }

        return result;
    }

    public static boolean hasRepeatedScript(List<String> segments) {

        Set<UnicodeScript> scripts = new HashSet<>();

        for (String segment : segments) {
            if (segment.length() == 0) {
                continue;
            }
            UnicodeScript script = getScript(segment.charAt(0));
            if (scripts.contains(script)) {
                return true;
            }
            scripts.add(script);
        }

        return false;
    }

    // Дæллаг Сарабукъ
    public static boolean hasLengthOneSegment(List<String> segments) {
        for (String segment : segments) {
            if (segment.length() == 1) {
                return true;
            }
        }
        return false;
    }

    // Quartier 7 / حارة 7
    public static List<String> segmentByScript(String input) {
        List<String> segments = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return segments;
        }

        StringBuilder currentSegment = new StringBuilder();
        Character.UnicodeScript currentScript = Character.UnicodeScript.of(input.charAt(0));

        if (currentScript == Character.UnicodeScript.COMMON || 
        currentScript == UnicodeScript.UNKNOWN || 
        currentScript == UnicodeScript.INHERITED) {
            currentScript = null;
        }
        // TODO: handle special case when first letter is COMMON, INHERITED, or UNKNOWN
        // 31 GD چک


        for (char ch : input.toCharArray()) {
            Character.UnicodeScript script = Character.UnicodeScript.of(ch);

            if (script == Character.UnicodeScript.COMMON || 
            script == UnicodeScript.UNKNOWN || 
            script == UnicodeScript.INHERITED) {
                script = null;
            }

            if (currentScript == null) {
                // handles the start of the string if the first character is not 
                // a defined script (UNKNOWN, INHERITED, COMMON)
                currentScript = script;
                currentSegment.append(ch);
            }
            else {
                if (script == currentScript || script == null) {
                    currentSegment.append(ch);
                } else {
                    if (currentSegment.length() > 0) {
                        String segment = cleanEndsAndZWSP(currentSegment.toString());
                        if (segment.length() > 0) {
                            segments.add(segment);
                        }
                    }
                    currentSegment.setLength(0); // Clear the current segment
                    currentSegment.append(ch);
                    currentScript = script;
                }
            }  
        }

        if (currentSegment.length() > 0) {
            String segment = cleanEndsAndZWSP(currentSegment.toString());
            if (segment.length() > 0) {
                segments.add(segment);
            }
        }

        if (hasRepeatedScript(segments) || hasLengthOneSegment(segments)) {
            segments = new ArrayList<>();
            segments.add(input);
        }
        segments = foreignScriptFix(segments);
        return segments;
    }

    public static void main(String[] args) {
        // Check if the input file path is provided as a command line argument


        String inputFilePath = "names.csv";
        String outputFilePath = "out.txt";

        // Try-with-resources statement to ensure the readers and writers are closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;

            Map<Integer, Integer> counter = new HashMap<>(); // number of segments -> count

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                Set<UnicodeScript> scriptsInLine = new HashSet<>();

                // Дугинка I
                if (line.endsWith(" I") ||
                    line.endsWith("-I") ||
                    line.endsWith(" V") ||
                    line.endsWith("-V")) {
                    line = line.substring(0, line.length() - 2);
                }
                if (line.endsWith(" II") ||
                    line.endsWith("-II") ||
                    line.endsWith(" IV") ||
                    line.endsWith("-IV") ||
                    line.endsWith(" VI") ||
                    line.endsWith("-VI")) {
                    line = line.substring(0, line.length() - 3);
                }
                if (line.endsWith(" III") ||
                    line.endsWith("-III")) {
                    line = line.substring(0, line.length() - 4);
                }
                // Analyze each character in the line
                for (char ch : line.toCharArray()) {
                    UnicodeScript script = getScript(ch);
                    if (script != null) {
                        scriptsInLine.add(script);
                    }
                }

                if (scriptsInLine.size() == 2 && scriptsInLine.contains(UnicodeScript.HAN)) {
                    // さいたま市
                    if (scriptsInLine.contains(UnicodeScript.HIRAGANA)) {
                        continue;
                    }
                    // 金冷シ
                    if (scriptsInLine.contains(UnicodeScript.KATAKANA)) {
                        continue;
                    }
                }
                // つつじケ丘五丁目
                if (scriptsInLine.size() == 3 && 
                scriptsInLine.contains(UnicodeScript.HAN) && 
                scriptsInLine.contains(UnicodeScript.HIRAGANA) && 
                scriptsInLine.contains(UnicodeScript.KATAKANA)) {
                    continue;
                }

                counter.put(scriptsInLine.size(), counter.getOrDefault(scriptsInLine.size(), 0) + 1);

                // Check if the line contains more than one unique script
                if (scriptsInLine.size() > 1) {
                    // if (scriptsInLine.contains(UnicodeScript.TIFINAGH) || 
                    // scriptsInLine.contains(UnicodeScript.MONGOLIAN) || 
                    // scriptsInLine.contains(UnicodeScript.ETHIOPIC)) {
                    //     continue;
                    // }

                    List<String> segments = segmentByScript(line);

                    System.out.println(line);
                    System.out.println(scriptsInLine);
                    
                    for (String segment : segments) {
                        System.out.println("  \"" + segment + "\"");
                    }
                    System.out.println();

                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println(counter);
        } catch (IOException e) {
            // Handle any I/O errors
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }
}

// Βατóς
// [LATIN, GREEK]
//   "Βατóς"

// Mετόχι
// [LATIN, GREEK]
//   "Mετόχι"

// Уæллаг Захъхъор
// [LATIN, CYRILLIC]
//   "Уæллаг Захъхъор"

// Morze Bałtyckie  - Baltijos jūra - Baltijas jūra -  Läänemeri - Itämeri - Östersjön - Østersøen - Ostsee - Балтийское море
// [LATIN, CYRILLIC]
//   "Morze Bałtyckie  - Baltijos jūra - Baltijas jūra -  Läänemeri - Itämeri - Östersjön - Østersøen - Ostsee"
//   "Балтийское море"

// Xaafuun حافون‎
// [LATIN, ARABIC]
//   "Xaafuun"
//   "حافون‎"

// 긴계단 (Gingyedan)
// [LATIN, HANGUL]
//   "긴계단"
//   "Gingyedan"

// "Maţarī, مطري"
// [ARABIC, LATIN]
//   ""Maţarī"
//   "مطري""

// 沼の平(numanodaira)
// [HAN, HIRAGANA, LATIN]
//   "沼の平(numanodaira)"

// كöردوا قرة بلان
// [ARABIC, LATIN]
//   "كöردوا قرة بلان"

// LEM 027 - ΚΟΥΚΟΝΗΣΙ
// [LATIN, GREEK]
//   "LEM 027"
//   "ΚΟΥΚΟΝΗΣΙ"

// 28/10 R چک
// [ARABIC, LATIN]
//   "28/10 R"
//   "چک"

// 第2D區 Area 2D
// [HAN, LATIN]
//   "第2D區 Area 2D"

// Indian Island /  Alənαpayí-Mənəhan
// [LATIN, GREEK]
//   "Indian Island /  Alənαpayí-Mənəhan"

// Cité Portugaise             الحي البرتغالي
// [ARABIC, LATIN]
//   "Cité Portugaise"
//   "الحي البرتغالي"