package concurrent;

import com.jingcai.apps.common.lang.concurrent.BusinessLock;
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
    public void test1() throws InterruptedException {
        int taskNum = 100;
        CountDownLatch latch = new CountDownLatch(taskNum);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
        ExecutorService executor = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.SECONDS, queue);
        Random random = new Random();
        final BusinessLock lock = new BusinessLock();
        for (int i = 0; i < taskNum; i++) {
            int rint = random.nextInt(10);
            executor.execute(new Task(lock, latch, i, rint));
        }
        latch.await();
    }

    @Test
    public void test3() throws InterruptedException {
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("--------------------clean start");
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 2, 2, TimeUnit.SECONDS);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
//        Thread.sleep(100*1000);
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
            if (!lock.lock(content)) return;
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