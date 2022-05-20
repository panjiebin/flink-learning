/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.code.learning;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author panjb
 */
public class StreamingWordCount {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		ParameterTool parameterTool = ParameterTool.fromArgs(args);
		String host = parameterTool.get("host");
		int port = parameterTool.getInt("port");

		DataStreamSource<String> source = env.socketTextStream(host, port);
		SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOne = source
				.flatMap((String line, Collector<Tuple2<String, Integer>> collector) -> {
					String[] words = line.split("\\s+");
					for (String word : words) {
						collector.collect(Tuple2.of(word, 1));
					}
				})
				.returns(TypeInformation.of(new TypeHint<>() {}));
		SingleOutputStreamOperator<Tuple2<String, Integer>> sum = wordAndOne.keyBy(t -> t.f0).sum(1);

		sum.print();

		env.execute("Word Count");
	}
}
