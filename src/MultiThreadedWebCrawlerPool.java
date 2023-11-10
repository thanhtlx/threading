import java.util.Map;
import java.util.concurrent.*;

import static java.lang.Thread.MAX_PRIORITY;

public class MultiThreadedWebCrawlerPool {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        ConcurrentHashMap<String, Integer> visitedUrls = new ConcurrentHashMap<>();
        LinkedBlockingDeque<String> urlsQueue = new LinkedBlockingDeque<>();

        String rootUrl = "https://en.wikipedia.org/wiki/Travelling_salesman_problem";

        urlsQueue.addLast(rootUrl);
        visitedUrls.put(rootUrl, 1);

        int numCores = 5;
        int numThreads = 50;

        Runnable r = new WebCrawler(urlsQueue, visitedUrls);

        ExecutorService pool = Executors.newFixedThreadPool(numCores);

        for (int i = 0; i < numThreads; i++) {
            pool.execute(r);
        }

        pool.shutdown();
        pool.awaitTermination(MAX_PRIORITY, TimeUnit.HOURS);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Visited " + visitedUrls.size() +" Urls  in " + totalTime/1000000 +" ms");
    }
}