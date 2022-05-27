package com.code.learning.window;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * 处理时间语义
 *
 * @author panjb
 */
public class ProcessingTimeTumblingWindowDemo {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);

        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndCount = source.map(line -> {
            String[] wordAndCnt = line.split(",");
            return Tuple2.of(wordAndCnt[0], Integer.parseInt(wordAndCnt[1]));
        }).returns(Types.TUPLE(Types.STRING, Types.INT));

        // 按照ProcessingTime划分滚动窗口
        // keyed stream
        // 滚动窗口，10秒滚动一次
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = wordAndCount.keyBy(t -> t.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = window.sum(1);

        sum.print();

        env.execute("Tumbling Processing Time Window");
    }
}
