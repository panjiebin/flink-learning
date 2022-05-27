package com.code.learning.window;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.*;
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
public class EventTimeTumblingWindowDemo {

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

        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndCnt = streamWithWatermark.map(element -> {
            String[] split = element.split(",");
            return Tuple2.of(split[1], Integer.parseInt(split[2]));
        }).returns(Types.TUPLE(Types.STRING, Types.INT));


        KeyedStream<Tuple2<String, Integer>, String> keyedStream = wordAndCnt.keyBy(t -> t.f0);

        // 当一个窗口的最大时间戳小于当前的watermark时，就会触发窗口计算，详见EventTimeTrigger
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(5)));

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = window.sum(1);

        sum.print();

        env.execute("Event Time Tumbling Window");
    }
}
