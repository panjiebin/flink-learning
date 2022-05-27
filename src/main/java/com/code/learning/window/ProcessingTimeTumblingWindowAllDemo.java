package com.code.learning.window;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * 处理时间语义
 *
 * @author panjb
 */
public class ProcessingTimeTumblingWindowAllDemo {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);

        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);
        SingleOutputStreamOperator<Integer> num = source.map(Integer::parseInt);

        // 按照ProcessingTime划分滚动窗口
        // non-keyed stream, parallelism=1
        // 滚动窗口，10秒滚动一次
        AllWindowedStream<Integer, TimeWindow> window = num.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        SingleOutputStreamOperator<Integer> sum = window.sum(0);

        sum.print();

        env.execute("Tumbling Processing Time Window");
    }
}
