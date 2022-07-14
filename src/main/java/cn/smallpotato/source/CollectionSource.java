package cn.smallpotato.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;
import java.util.List;

/**
 * @author small potato
 */
public class CollectionSource {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        List<String> words = Arrays.asList("a", "b", "c");
        DataStreamSource<String> fromCollection = env.fromCollection(words);

        DataStreamSource<String> fromElements = env.fromElements("a", "b", "c");


    }
}
