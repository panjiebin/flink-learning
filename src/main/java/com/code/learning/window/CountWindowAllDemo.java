package com.code.learning.window;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

/**
 * @author panjb
 */
public class CountWindowAllDemo {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);
        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);

        SingleOutputStreamOperator<Integer> num = source.map(Integer::parseInt);

        // 按照数据条数划分窗口（数据达到指定条数，触发窗口）
        // 对于 non-keyed stream，原始的 stream 不会被分割为多个逻辑上的 stream，
        // 所以所有的窗口计算会被同一个 task 完成，也就是 parallelism 为 1。
        // 等价于
        // num.windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(5)))
        // 其实countWindowAll()，内部就是如此
        AllWindowedStream<Integer, GlobalWindow> window = num.countWindowAll(5);

        SingleOutputStreamOperator<Integer> sum = window.sum(0);

        sum.print();

        env.execute("Count Window All Demo");
    }
}
