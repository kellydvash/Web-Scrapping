
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Driver {
    static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        int points = 0;
        BaseRobot site = getSiteSelection();
        if (site != null) {
            points += guessCommonWords(site);
            String userText = getHeadlinesText();
            System.out.println("how many time it will appears?:");
            int quantity = scanner.nextInt();
            try {
                points += chuckText(quantity, userText, site);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("you achieved " + points + " points!");
        } else {
            System.out.println("cant access website, sorry.");
        }

    }

    private static int chuckText(int quantity, String userText, BaseRobot site) throws IOException {
        int realQuantity = site.countInArticlesTitles(userText);
        if (Math.abs(quantity - realQuantity) <= 2) {
            return 250;
        }
        return 0;
    }

    private static String getHeadlinesText() {
        String userText = "";
        while (userText.length() < 1 || userText.length() > 20) {
            System.out.println("Enter any text that you think should appear in the headlines on the site,");
            System.out.println("the text should be between 1 and 20 chars:");
            userText = scanner.nextLine();
        }
        return userText;
    }

    private static BaseRobot getSiteSelection() {
        int input = 0;
        while (input < 1 || input > 3) {
            System.out.println("which site do you want to scan?");
            System.out.println("\t1. Mako");
            System.out.println("\t2. Ynet");
            System.out.println("\t3. Walla");
            input = scanner.nextInt();
        }
        scanner.nextLine();
        try {
            switch (input) {
                case 1:
                    return new MakoRobot();

                case 2:
                    return new YnetRobot();
                default:
                    return new WallaRobot();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int guessCommonWords(BaseRobot site) {
        String guess;
        int points = 0;
        try {
            String longestArticle = site.getLongestArticleTitle();
            System.out.println("Please guess what the most common words on the site are?");
            System.out.println("hint:\n" + longestArticle);
            Map<String, Integer> wordsInSite = site.getWordsStatistics();
            for (int i = 1; i <= 5; i++) {
                System.out.println("guess number " + i + ":");
                guess = scanner.nextLine();
                points += wordsInSite.getOrDefault(guess, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

}