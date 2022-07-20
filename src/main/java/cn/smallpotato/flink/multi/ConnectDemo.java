package cn.smallpotato.flink.multi;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

/**
 * 连接两个流，两个流类型可以不一致，但是输出类型必须一致。
 * 主要作用：两个流可以共享状态
 * @author small potato
 */
public class ConnectDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> wordStream = env.fromElements("a", "b", "c", "d", "e");
        DataStreamSource<Integer> numStream = env.fromElements(1, 2, 3, 4, 5);

        ConnectedStreams<String, Integer> connectedStreams = wordStream.connect(numStream);

        // 两个流处理逻辑独立，但是输出类型必须一致，唯一好处是可以共享状态！！！
        SingleOutputStreamOperator<String> stream = connectedStreams.map(new CoMapFunction<String, Integer, String>() {

            // 共享状态。。。

            @Override
            public String map1(String word) throws Exception {
                return word.toUpperCase();
            }

            @Override
            public String map2(Integer num) throws Exception {
                return num * 10 + "";
            }
        });
        stream.print();

        env.execute();
    }
}
