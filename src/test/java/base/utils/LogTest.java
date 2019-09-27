package base.utils;

import org.apache.log4j.Logger;

public class LogTest {
     static  Logger  log = Logger.getLogger(LogTest.class);
    public static void main(String[] args) {
        
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
    }

}
