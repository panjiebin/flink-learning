package com.code.learning.operator;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author panjb
 */
public class IterateStreamDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.socketTextStream("174.35.74.224", 8881);
        SingleOutputStreamOperator<Long> initialStream = source.map(Long::parseLong);

        IterativeStream<Long> iteration = initialStream.iterate();
        //迭代计算
        SingleOutputStreamOperator<Long> iterationBody = iteration.map((MapFunction<Long, Long>) value -> {
            System.out.println("current value: " + value);
            return value - 2;
        });
        // 满足条件则重新作为输入
        DataStream<Long> feedback = iterationBody.filter((FilterFunction<Long>) value -> value > 0);
        iteration.closeWith(feedback);
        // 退出迭代条件
        DataStream<Long> output = iterationBody.filter((FilterFunction<Long>) value -> value <= 0);
        output.print();
        env.execute();
    }
}
