package com.aihuishou.payflow.mq;

import com.aihuishou.payflow.engine.FlowEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MQConsumer {

    @Value("${rocketmq.ip")
    private String rocketMqIp;

    @Value("${rocketmq.port")
    private String rocketMqPort;


    @Autowired
    private FlowEngine flowEngine;

    @PostConstruct
    public void init() throws MQClientException {
        // 创建消费者实例
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer");

        // 指定NameServer地址，多个地址用分号分隔
        consumer.setNamesrvAddr(rocketMqIp+":"+rocketMqPort);

        // 订阅Topic和Tag
        consumer.subscribe("test_topic", "*");

        // 注册消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            try {
                for (MessageExt message : messages) {
                    String receiveMessage =new String(message.getBody());
                    flowEngine.handleMQMessage(receiveMessage);
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 启动消费者
        consumer.start();
    }


}
