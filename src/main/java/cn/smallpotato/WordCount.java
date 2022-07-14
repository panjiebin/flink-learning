package cn.smallpotato;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author small potato
 */
public class WordCount {

	public static void main(String[] args) throws Exception {

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<Tuple2<String, Integer>> sum = env.socketTextStream("localhost", 9999)
				.flatMap(new Splitter())
				.keyBy(tpl -> tpl.f0)
				.sum(1);

		sum.print();

		env.execute("Word Count Job");
	}

	public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

		@Override
		public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
			String[] words = s.split("\\s+");
			for (String word : words) {
				collector.collect(Tuple2.of(word, 1));
			}
		}
	}
}
