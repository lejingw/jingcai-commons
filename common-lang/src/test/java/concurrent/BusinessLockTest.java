package concurrent;

import com.jingcai.apps.common.lang.concurrent.BusinessLock;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

public class BusinessLockTest {
    private Logger logger = LoggerFactory.getLogger(BusinessLockTest.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

    @Test
    public void aaa(){
        System.out.println("------");
    }


    @Ignore("it coast too many time")
    @Test
    public void test1() throws InterruptedException {
        int taskNum = 90;
        CountDownLatch latch = new CountDownLatch(taskNum);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1000);
        ExecutorService executor = new ThreadPoolExecutor(9, 9, 1L, TimeUnit.SECONDS, queue);
        Random random = new Random();
        final BusinessLock lock = new BusinessLock();
        for (int i = 0; i < taskNum; i++) {
            int rint = random.nextInt(10);
            executor.execute(new Task(lock, latch, i, rint));
        }
        latch.await();
        executor.shutdown();
    }

    class Task implements Runnable {
        private final BusinessLock lock;
        private final CountDownLatch latch;
        private final int index;
        private final String content;

        public Task(BusinessLock lock, CountDownLatch latch, int i, int rint) {
            this.lock = lock;
            this.latch = latch;
            index = i;
            content = String.valueOf("content" + rint);
        }

        public void run() {
            lock.lock(content);
            try {
                Random random = new Random();
                int rint = random.nextInt(10) + 1;
                logger.debug("\t" + index + "\t\t" + content + "\t\twait=" + rint + "\t\ttime=" + sdf.format(new Date()));
                Thread.sleep(rint * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("---1---exception:index=" + index + "\t\t" + content);
            } finally {
                lock.unlock();
            }
            latch.countDown();
        }
    }
}