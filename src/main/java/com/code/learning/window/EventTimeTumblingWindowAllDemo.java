package com.code.learning.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Duration;

/**
 * 事件时间语义
 *
 * @author panjb
 */
public class EventTimeTumblingWindowAllDemo {

    public static void main(String[] args) throws Exception {

        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);

        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);

        // watermark生成策略
        // withTimestampAssigner() 提取数据中的时间，BoundedOutOfOrdernessWatermarks会记录每个窗口的最大时间戳
        // new Watermark(maxTimestamp - outOfOrdernessMillis - 1)
        // outOfOrdernessMillis即我们设置等待时间
        WatermarkStrategy<String> watermarkStrategy = WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofSeconds(0))
                .withTimestampAssigner((element, timestamp) -> {
                    String[] split = element.split(",");
                    return Long.parseLong(split[0]);
                });

        SingleOutputStreamOperator<String> streamWithWatermark = source.assignTimestampsAndWatermarks(watermarkStrategy);

        SingleOutputStreamOperator<Integer> num = streamWithWatermark.map(element -> {
            String[] split = element.split(",");
            return Integer.parseInt(split[1]);
        });

        // 当一个窗口的最大时间戳小于当前的watermark时，就会触发窗口计算，详见EventTimeTrigger
        AllWindowedStream<Integer, TimeWindow> windowAll = num.windowAll(TumblingEventTimeWindows.of(Time.seconds(5)));

        SingleOutputStreamOperator<Integer> sum = windowAll.sum(0);

        sum.print();

        env.execute("Event Time Tumbling Window");
    }
}
