package com.aihuishou.payflow.mq;

import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NormalMessageProducer {

    @Value("${rocketmq.ip")
    private String rocketMqIp;

    @Value("${rocketmq.port")
    private String rocketMqPort;

    @SneakyThrows
    public void sendMessage(String sendMsg, int level) {
        // 创建生产者实例
        DefaultMQProducer producer = new DefaultMQProducer("test_producer");
        // 指定NameServer地址，多个地址用分号分隔
        producer.setNamesrvAddr(rocketMqIp + ":" + rocketMqPort);
        // 启动生产者
        producer.start();
        try {
            // 创建消息实例，指定Topic、Tag和消息体
            Message message = new Message("test_topic", "*", sendMsg.getBytes());
            if (level > 0) {
                message.setDelayTimeLevel(level);
            }
            // 发送消息
            producer.send(message);
        } finally {
            // 关闭生产者
            producer.shutdown();
        }
    }
}
