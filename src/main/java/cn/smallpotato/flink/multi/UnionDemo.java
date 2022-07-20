package cn.smallpotato.flink.multi;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 将两个或多个类型一样的流合并成一个流
 * @author small potato
 */
public class UnionDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> wordStream = env.fromElements("a", "b", "c", "d", "e");
        DataStreamSource<String> wordStream2 = env.fromElements("A", "B", "C");

        // union多个流的类型必须一致；connect两个流可以类型不一致，输出类型必须一致
        DataStream<String> dataStream = wordStream.union(wordStream2);

        dataStream.print();

        env.execute();
    }
}
