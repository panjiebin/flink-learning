package com.code.learning.window;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理时间语义
 *
 * @author panjb
 */
public class ProcessingTimeTumblingWindowReduceDemo {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);

        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndCount = source.map(line -> {
            String[] wordAndCnt = line.split(",");
            return Tuple2.of(wordAndCnt[0], Integer.parseInt(wordAndCnt[1]));
        }).returns(Types.TUPLE(Types.STRING, Types.INT));

        KeyedStream<Tuple2<String, Integer>, String> keyedStream = wordAndCount.keyBy(t -> t.f0);

        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(30)));

        // 先keyBy，再按照ProcessingTime划分滚动窗口，然后再调用reduce，实时进行聚合，即增量聚合，区别于apply的全量聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> reduce = window.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                value1.f1 = value1.f1 + value2.f1;
                return value1;
            }
        });

        reduce.print();

        env.execute("Tumbling Processing Time Window");
    }
}
