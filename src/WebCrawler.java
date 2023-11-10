import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler implements Runnable {

    private ConcurrentHashMap<String, Integer> visitedUrls;
    private LinkedBlockingDeque<String> urlsQueue;
    private final int maxDepth = 4;
    private final int maxUrlsPerPage = 10;

    public WebCrawler(LinkedBlockingDeque<String> deque,
                      ConcurrentHashMap<String, Integer> hashmap) {
        visitedUrls = hashmap;
        urlsQueue = deque;
    }

    private void parseAndAddUrls(String rawHtml, int depth) {
        String urlPattern = "((\\/wiki\\/)+[^\\s\\.\\#\\:\"]+[\\w])\"";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHtml);

        int cntUrlsPerPage = 0;

        while (matcher.find()) {
            String newUrl = matcher.group(1);
            newUrl = "https://en.wikipedia.org" + newUrl;

            if (!visitedUrls.containsKey(newUrl)) {
                urlsQueue.addLast(newUrl);
                visitedUrls.put(newUrl, depth+1);
                cntUrlsPerPage += 1;

                if (cntUrlsPerPage >= maxUrlsPerPage) {
                    break;
                }
            }
        }
    }

    private void crawl() {
        try {
            String url = urlsQueue.takeFirst();
            int depth;

            if (url != null && (depth = visitedUrls.get(url)) < maxDepth) {
                URL urlObject = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()));

                String inputLine = in.readLine();
                String rawHtml = "";

                while(inputLine  != null){
                    rawHtml += inputLine;
                    inputLine = in.readLine();
                }

                in.close();
                parseAndAddUrls(rawHtml, depth);
            }
        } catch (Exception e) {}
    }

    @Override
    public void run() {
        crawl();
    }
}