package com.code.learning.patition;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * 随机分区
 *
 * @author panjb
 */
public class ShufflePartition {

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration();
        cfg.set(RestOptions.PORT, 8081);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(cfg);
        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);
        source.shuffle()
                .map(new RichMapFunction<String, String>() {
                    @Override
                    public String map(String value) throws Exception {
                        return value + " > " + getRuntimeContext().getIndexOfThisSubtask();
                    }
                })
                .setParallelism(3)
                .addSink(new RichSinkFunction<>() {
                    @Override
                    public void invoke(String value, Context context) throws Exception {
                        System.out.println(value + " > " + getRuntimeContext().getIndexOfThisSubtask());
                    }
                });

        env.execute();
    }
}