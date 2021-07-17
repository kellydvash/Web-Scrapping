import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class YnetRobot extends BaseRobot implements MapOrder {
    private Map<String, Integer> map = new HashMap<>();
    private final ArrayList<String> sitesUrl;

    public YnetRobot() throws IOException {
        super("https://www.ynet.co.il/home/0,7340,L-8,00.html");
        String url;
        sitesUrl = new ArrayList<>();
        Document ynet = Jsoup.connect(getRootWebsiteUrl()).get();
        for (Element slotTitle : ynet.getElementsByClass("TopStory1280Componenta basic")) {
            Element element = slotTitle.getElementsByClass("slotTitle").get(0);
            url = element.child(0).attributes().get("href");
            sitesUrl.add(url);
        }
        for (Element teasers : ynet.getElementsByClass("YnetMultiStripComponenta oneRow multiRows")) {
            for (Element textDiv : teasers.getElementsByClass("textDiv")) {
                url = textDiv.child(1).attributes().get("href");
                sitesUrl.add(url);
            }
        }
        for (Element slotsContent : ynet.getElementsByClass("MultiArticleRowsManualComponenta").get(0).getElementsByClass("slotsContent")) {
            for (Element slotTitle_medium : slotsContent.getElementsByClass("slotTitle medium")) {
                url = slotTitle_medium.child(0).attributes().get("href");
                sitesUrl.add(url);
            }
            for (Element slotTitle_small : slotsContent.getElementsByClass("slotTitle small")) {
                url = slotTitle_small.child(0).attributes().get("href");
                sitesUrl.add(url);
            }
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

    @Override
    public int countInArticlesTitles(String text) throws IOException {
        int count = 0;
        Document ynet = Jsoup.connect(getRootWebsiteUrl()).get();
        for (Element container : ynet.getElementsByClass("layoutContainer")) {
            for (Element title_small : container.getElementsByClass("slotTitle small")) {
                if (title_small.text().contains(text)) {
                    count++;
                }
            }
            for (Element title_medium : container.getElementsByClass("slotTitle medium")) {
                if (title_medium.text().contains(text)) {
                    count++;
                }
            }
        }
        if (ynet.getElementsByClass("slotSubTitle").get(0).text().contains(text)) {
            count++;
        }
        if (ynet.getElementsByClass("slotTitle").get(0).text().contains(text)) {
            count++;
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
            //title
            String title = article.getElementsByClass("mainTitle").text();
            //article body
            StringBuilder siteTextBuilder = new StringBuilder();
            for (Element text_editor_paragraph_rtl : article.getElementsByClass("text_editor_paragraph rtl")) {
                siteTextBuilder.append(text_editor_paragraph_rtl.getElementsByTag("span").text());
            }
            if (longest < siteTextBuilder.length()) {
                longest = siteTextBuilder.length();
                longestArticleTitle = title;
            }
        }
        return longestArticleTitle;
    }

    @Override
    public String accessSite(String site) throws IOException {
        Document article;
        String siteText = "";
        StringBuilder siteTextBuilder = new StringBuilder(siteText);
        article = Jsoup.connect(site).get();
        siteTextBuilder.append(article.getElementsByClass("mainTitle").text());
        siteTextBuilder.append(" ");
        siteTextBuilder.append(article.getElementsByClass("subTitle").text());
        siteTextBuilder.append(" ");
        for (Element text_editor_paragraph_rtl : article.getElementsByClass("text_editor_paragraph rtl")) {
            siteTextBuilder.append(" ");
            siteTextBuilder.append(text_editor_paragraph_rtl.getElementsByTag("span").text());
        }
        return siteTextBuilder.toString();
    }
}