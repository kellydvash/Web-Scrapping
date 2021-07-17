import java.io.IOException;
import java.util.Map;

public interface MapOrder {
    String accessSite(String site) throws IOException;

    default Map<String, Integer> getWordsIntoMap(String[] wordsOfArticle, Map<String, Integer> map) {
        int value;
        for (String word : wordsOfArticle) {
            if (map.containsKey(word)) {
                value = map.get(word) + 1;
            } else {
                value = 1;
            }
            map.put(word, value);
        }
        return map;
    }
    default String correctWords(String siteText) {
        siteText = siteText.replaceAll("[-–•<>@&_%():,.?0-9]", " ");
        siteText = siteText.replaceAll("\"\\s|\\s\"", " ");
        siteText = siteText.replaceAll("\\s+", " ");
        return siteText;
    }

}