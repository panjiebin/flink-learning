package cn.smallpotato.sink;

import cn.smallpotato.source.EventLog;
import cn.smallpotato.source.MySourceFunction;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonClassDescription;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

/**
 * @author small potato
 */
public class StreamFileSinkDemo01 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 开启checkpoint
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///D:/projects/learning/flink-learning/ckpt");

        DataStreamSource<EventLog> source = env.addSource(new MySourceFunction());

        FileSink<String> fileSink = FileSink.forRowFormat(new Path("D:/file-sink/"), new SimpleStringEncoder<String>("utf-8"))
                // 文件滚动策略：间隔10s，或文件大小达到1M
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofSeconds(10L))
                                .withMaxPartSize(new MemorySize(1024 * 1024))
                                .build()
                )
                // 分桶策略（划分子文件夹的策略）
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                .withBucketCheckInterval(5)
                .withOutputFileConfig(
                        OutputFileConfig.builder()
                                .withPartPrefix("sp")
                                .withPartSuffix(".txt")
                                .build()
                )
                .build();
        source.map(JSON::toJSONString).sinkTo(fileSink);

        env.execute();
    }
}
