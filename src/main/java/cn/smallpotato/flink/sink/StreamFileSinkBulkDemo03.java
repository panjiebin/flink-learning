package cn.smallpotato.flink.sink;

import cn.smallpotato.flink.source.EventLog;
import cn.smallpotato.flink.source.MySourceFunction;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroWriterFactory;
import org.apache.flink.formats.avro.AvroWriters;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

/**
 * 生成bulk列式存储文件
 * <P>方式三：{@link AvroWriters#forReflectRecord(Class)}
 *
 * @author small potato
 */
public class StreamFileSinkBulkDemo03 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 开启checkpoint
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///D:/projects/learning/flink-learning/ckpt");

        DataStreamSource<EventLog> source = env.addSource(new MySourceFunction());

        /*
        * 输出为列格式
        * 要定义parquet文件，需要先定义schema
        */
        AvroWriterFactory<EventLog> writerFactory = AvroWriters.forReflectRecord(EventLog.class);
        FileSink<EventLog> fileSink = FileSink.forBulkFormat(new Path("D:/bulk-sink/"), writerFactory)
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                .withRollingPolicy(OnCheckpointRollingPolicy.build())
                .withBucketCheckInterval(5)
                .withOutputFileConfig(
                        OutputFileConfig.builder()
                                .withPartPrefix("sp")
                                .withPartSuffix(".parquet")
                                .build()
                )
                .build();
        source.sinkTo(fileSink);
        env.execute();
    }
}
