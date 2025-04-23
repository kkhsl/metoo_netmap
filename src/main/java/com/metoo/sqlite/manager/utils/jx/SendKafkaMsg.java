package com.metoo.sqlite.manager.utils.jx;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class SendKafkaMsg {

    public boolean send(String data) {
        // topic
        // 分组
        // 账号 密码
        String topicName = "internal.space"; // Kafka 主题名称  offset、key、value
        String bootstrapServers = "134.224.183.248:9092"; // Kafka 服务器地址
        String groupId = "org26bfc072super"; // 消费者组 ID

        // 配置生产者属性
        Properties props = new Properties();
        props.put("group.id", groupId);
        props.put("bootstrap.servers", bootstrapServers);// kafka服务地址
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 黑马
        /**
         * 0：生产者在成功将消息发送给网络之后就会认为消息已经发送成功，不等待任何来自服务端的响应
         * 1：生产者在成功将消息发送给 Leader 后会收到 Leader 的确认，然后就会认为消息发送成功。
         *      这种情况下，如果 Leader 接收消息但还没有将消息写入到本地磁盘，那么消息可能会丢失。
         * acks=all 或 acks=-1：生产者在成功将消息发送给 Leader 并且所有的 follower 副本都收到了消息后才会收到确认，然后认为消息发送成功。
         *                          这是最安全的设置，因为它可以保证即使 Leader 宕机，也不会丢失消息
         */
        props.put("acks", "all");// 表示当生产者生产数据到kafka中，kafka中会以什么样的策略返回，写入策略和返回机制

        // 创建生产者实例 key value 都可以为空
        Producer<String, String> producer = new KafkaProducer<>(props);

        // 国密算法解密
//        String data = JXDataUtils.getEncryptedData();


        System.out.println("data" + data);
        // 发送一条测试消息

        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, data);
//        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, null,"Hello Kafka!");

        Future<RecordMetadata> future = producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println("消息发送成功，offset: " + metadata.offset());
                } else {
                    System.err.println("消息发送失败：" + exception.getMessage());
                }
            }
        });


        try {
            RecordMetadata recordMetadata = future.get();// 阻塞直到发送完成
            String result = recordMetadata.toString();
            log.info("kafka:{}", result);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭生产者
            if(producer != null){
                producer.close();
            }
        }
        return false;
    }
}
