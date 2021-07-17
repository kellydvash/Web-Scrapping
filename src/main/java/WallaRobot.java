import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WallaRobot extends BaseRobot implements MapOrder {
    Map<String, Integer> map;
    ArrayList<String> sitesUrl;

    public WallaRobot() throws IOException {
        super("https://www.walla.co.il/");
        map = new HashMap<>();
        sitesUrl = new ArrayList<>();

        Document walla = Jsoup.connect(getRootWebsiteUrl()).get();
        for (Element teasers : walla.getElementsByClass("with-roof with- timer ")) {
            sitesUrl.add(teasers.child(0).attributes().get("href"));
        }

        Element section = walla.getElementsByClass("css-1ugpt00 css-a9zu5q css-rrcue5 ").get(0);
        for (Element smallTeasers : section.getElementsByTag("a")) {
            sitesUrl.add(smallTeasers.attributes().get("href"));
        }
    }

    @Override
    public Map<String, Integer> getWordsStatistics() throws IOException {
        for (String site : sitesUrl) {
            String siteText;
            siteText = accessSite(site);
            siteText = correctWords(siteText);
            String[] wordsOfArticle = siteText.split(" ");
            map = getWordsIntoMap(wordsOfArticle, map);
        }
        return map;
    }

    public String accessSite(String site) throws IOException {
        Document article;
        String siteText = "";
        StringBuilder siteTextBuilder = new StringBuilder(siteText);
        article = Jsoup.connect(site).get();
        Element titleSection = article.getElementsByClass("item-main-content").get(0);
        siteTextBuilder.append(titleSection.getElementsByTag("h1").get(0).text());
        siteTextBuilder.append(" ");
        for (Element subTitle : article.getElementsByClass("css-onxvt4")) {
            siteTextBuilder.append(" ");
            siteTextBuilder.append(subTitle.text());
        }
        return siteTextBuilder.toString();
    }

    @Override
    public int countInArticlesTitles(String text) throws IOException {
        int count = 0;
        Document walla = Jsoup.connect(getRootWebsiteUrl()).get();
        String titleFromWeb;
        for (Element teasers : walla.getElementsByClass("with-roof ")) {
            titleFromWeb = teasers.getElementsByTag("h2").text();
            if (titleFromWeb.contains(text)) {
                count++;
            }
        }

        Element section = walla.getElementsByClass("css-1ugpt00 css-a9zu5q css-rrcue5 ").get(0);
        for (Element smallTeasers : section.getElementsByTag("a")) {
            titleFromWeb = smallTeasers.getElementsByTag("h3").text();
            if (titleFromWeb.contains(text)){
                count++;
            }
        }
        return count;
    }

    @Override
    public String getLongestArticleTitle() throws IOException {
        Document article;
        String longestArticleTitle = "";
        int longest = 0;
        for (String site : sitesUrl) {
            article = Jsoup.connect(site).get();
            Element titleSection = article.getElementsByClass("item-main-content").get(0);
            String title = titleSection.getElementsByTag("h1").get(0).text();
            StringBuilder siteTextBuilder = new StringBuilder();
            for (Element articleBody : article.getElementsByClass("css-onxvt4")) {
                siteTextBuilder.append(articleBody.text());
                siteTextBuilder.append(" ");
            }
            if (longest < siteTextBuilder.length()) {
                longest = siteTextBuilder.length();
                longestArticleTitle = title;
            }
        }
        return longestArticleTitle;
    }
}