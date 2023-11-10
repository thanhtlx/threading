import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiThreadedWebCrawler {

    public static void main(String[] args) throws Exception {
        // intit monitor
        long startTime = System.nanoTime();

        ConcurrentHashMap<String, Integer> visitedUrls = new ConcurrentHashMap<>();
        LinkedBlockingDeque<String> urlsQueue = new LinkedBlockingDeque<>();

        String rootUrl = "https://en.wikipedia.org/wiki/Travelling_salesman_problem";

        urlsQueue.addLast(rootUrl);
        visitedUrls.put(rootUrl, 1);

        final int numThreads = 50;

        Runnable r = new WebCrawler(urlsQueue, visitedUrls);

        Thread[] myThreads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            myThreads[i] = new Thread(r);
            myThreads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            myThreads[i].join();
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Visited " + visitedUrls.size() +" Urls  in " + totalTime/1000000 +" ms");

    }
}