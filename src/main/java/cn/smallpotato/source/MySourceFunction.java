package cn.smallpotato.source;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author small potato
 */
public class MySourceFunction implements SourceFunction<EventLog> {

    private volatile boolean flag = true;

    private final String[] events = {"appLaunch", "pageLoad", "adShow", "adClick", "itemCollect", "putBack", "wakeUp", "appClose"};

    @Override
    public void run(SourceContext<EventLog> context) throws Exception {

        Map<String, String> eventInfo = new HashMap<>();
        EventLog log = new EventLog();
        while (flag) {
            log.setGuid(RandomUtils.nextLong(1, 1000));
            log.setSessionId(RandomStringUtils.randomAlphabetic(1024).toUpperCase());
            log.setTimestamp(Instant.now().toEpochMilli());
            log.setEventId(events[RandomUtils.nextInt(0, events.length)]);
            eventInfo.clear();
            eventInfo.put(RandomStringUtils.randomAlphabetic(1), RandomStringUtils.randomAlphabetic(2));
            log.setEventInfo(eventInfo);
            context.collect(log);
            Thread.sleep(RandomUtils.nextInt(0, 5));
        }
    }

    @Override
    public void cancel() {
        this.flag = false;
    }
}
