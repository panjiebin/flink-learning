package cn.smallpotato.flink.multi;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 侧流输出，可以实现：流复制，分流
 * @author small potato
 */
public class SideOutputDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Long> source = env.fromSequence(1, 20);
        // 实现奇偶数分流
        OutputTag<Long> oddTag = new OutputTag<>("odd", Types.LONG);
        OutputTag<Long> evenTag = new OutputTag<>("even", Types.LONG);
        SingleOutputStreamOperator<Long> mainStream = source.process(new ProcessFunction<>() {
            @Override
            public void processElement(Long aLong, Context context, Collector<Long> collector) throws Exception {
                if (aLong % 2 == 0) {
                    context.output(evenTag, aLong);
                } else {
                    context.output(oddTag, aLong);
                }
                // 数据仍然放入主流，如果不放入，主流中就不会有数据
//                collector.collect(aLong);
            }
        });
        DataStream<Long> oddStream = mainStream.getSideOutput(oddTag);
        DataStream<Long> evenStream = mainStream.getSideOutput(evenTag);

        mainStream.print("main");
        oddStream.print("odd");
        evenStream.print("even");

        env.execute("Side Output Job");
    }
}
