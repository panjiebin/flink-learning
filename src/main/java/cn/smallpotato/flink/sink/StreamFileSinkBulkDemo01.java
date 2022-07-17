package cn.smallpotato.flink.sink;

import cn.smallpotato.flink.source.EventLog;
import cn.smallpotato.flink.source.MySourceFunction;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroWriterFactory;
import org.apache.flink.formats.avro.AvroWriters;
import org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

/**
 * 生成bulk列式存储文件
 * <P>方式一：{@link AvroWriters#forGenericRecord(Schema)}
 *
 * @author small potato
 */
public class StreamFileSinkBulkDemo01 {

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

        // 构建schema (属于avro 的API)
        Schema schema = SchemaBuilder.builder()
                .record("DataRecord")
                .namespace("cn.smallpotato.flink.avro")
                .doc("用户行为事件数据模式")
                .fields()
                .requiredLong("guid")
                .requiredString("sessionId")
                .requiredString("eventId")
                .requiredLong("timestamp")
                .name("eventInfo")
                .type()
                .map()
                .values()
                .type("string")
                .noDefault()
                .endRecord();


        AvroWriterFactory<GenericRecord> writerFactory = AvroWriters.forGenericRecord(schema);
        FileSink<GenericRecord> fileSink = FileSink.forBulkFormat(new Path("D:/bulk-sink/"), writerFactory)
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

        source.map((MapFunction<EventLog, GenericRecord>) e -> {
                    GenericData.Record record = new GenericData.Record(schema);
                    record.put("guid", e.getGuid());
                    record.put("sessionId", e.getSessionId());
                    record.put("eventId", e.getEventId());
                    record.put("timestamp", e.getTimestamp());
                    record.put("eventInfo", e.getEventInfo());
                    return record;
                })
                // 需要显示指定AvroTypeInfo，否则无法序列化成功
                .returns(new GenericRecordAvroTypeInfo(schema))
                .sinkTo(fileSink);

        env.execute();
    }
}
