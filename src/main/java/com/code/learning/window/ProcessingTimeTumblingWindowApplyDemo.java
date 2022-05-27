package com.code.learning.window;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理时间语义
 *
 * @author panjb
 */
public class ProcessingTimeTumblingWindowApplyDemo {

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

        // 先keyBy，再按照ProcessingTime划分滚动窗口，然后再调用apply，将窗口内的数据先攒起来，窗口触发后再进行运算
        // 根据官方说明，WindowFunction接口在未来将会被弃用，建议使用ProcessWindowFunction替代
        SingleOutputStreamOperator<Tuple2<String, Integer>> apply = window.apply(new WindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>() {
            // 窗口触发时被调用
            @Override
            public void apply(String key, TimeWindow window, Iterable<Tuple2<String, Integer>> input, Collector<Tuple2<String, Integer>> out) throws Exception {
                System.out.println("end: " + window.getEnd());
                List<Tuple2<String, Integer>> lst = new ArrayList<>();
                for (Tuple2<String, Integer> tpl : input) {
                    lst.add(tpl);
                }
                lst.sort((a, b) -> Integer.compare(b.f1, a.f1));

                for (int i = 0; i < Math.min(3, lst.size()); i++) {
                    out.collect(lst.get(i));
                }
            }
        });

        apply.print();

        env.execute("Tumbling Processing Time Window");
    }
}
