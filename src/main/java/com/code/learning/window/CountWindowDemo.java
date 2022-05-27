package com.code.learning.window;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

/**
 * @author panjb
 */
public class CountWindowDemo {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);
        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);

        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndCount = source.map(line -> {
            String[] wordAndCnt = line.split(",");
            return Tuple2.of(wordAndCnt[0], Integer.parseInt(wordAndCnt[1]));
        }).returns(Types.TUPLE(Types.STRING, Types.INT));

        // 按照数据条数划分窗口（数据达到指定条数，触发窗口）
        // 对 keyBy 之后的 KeyedStream 划分窗口，是多并行的，即有多个subtask执行窗口中的逻辑
        // 等价于 wordAndCount.keyBy(t -> t.f0).countWindow(5)
        WindowedStream<Tuple2<String, Integer>, String, GlobalWindow> window = wordAndCount.keyBy(t -> t.f0)
                .window(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(5)));

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = window.sum(1);

        sum.print();

        env.execute("Count Window Demo");
    }
}
