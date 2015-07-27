package logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lejing on 15/7/27.
 */
public class LogbackTest {
    private final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    @Before
    public void test_normal() {
        logger.debug("say : {}", "hello world!");
    }

    @After
    public void test_normal2() {
        logger.debug("say : {}", "hello world!");
    }

    @Test
    public void test_manual_conf() throws IOException, JoranException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String path2 = LogbackTest.class.getResource("").getPath();

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(path2 + "logback_conf.xml");
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        logger.debug("path = {}", path);
        logger.debug("path2 = {}", path2);
    }

}
