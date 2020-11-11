package com.rose.controler;

import com.rose.data.to.dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rocketmq")
public class RocketMqTestControler {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 功能：发送普通消息
     * 备注：消息会持久化存储，name server 和 broker server 停掉后，消息也不会丢失
     */
    @GetMapping("/sendMsg")
    public void sendMsg() {
        for(int i=0;i<100;i++){
            TestDto test = new TestDto();
            test.setParam1("111");
            test.setParam2("222");
            test.setParam3("333");
            rocketMQTemplate.convertAndSend("test-topic", test);
        }
    }

    /**
     * 功能：发送延迟消息
     * 备注：RocketMQ 不支持任意时间自定义的延迟消息，仅支持内置预设值的延迟时间间隔的延迟消息
     *      预设值的延迟时间间隔为：1s、 5s、 10s、 30s、 1m、 2m、 3m、 4m、 5m、 6m、 7m、 8m、 9m、 10m、 20m、 30m、 1h、 2h
     *      也就是一共18个级别    ：1    2    3     4    。。。                                                               18
     */
    @GetMapping("/sendDelayMsg")
    public void sendDelayMsg() {
        //SendResult result = rocketMQTemplate.syncSend("test-topic", MessageBuilder.withPayload("瓜田李下 delay").build(),7000,3);
        TestDto test = new TestDto();
        test.setParam1("这是一个延迟消息");
        test.setParam2("222");
        test.setParam3("333");
        // timeout：该参数，不是可控制的多长的延迟时间，而是指的是，多长时间，没发送成功，就当做发送失败
        // delayLevel：该参数，才是真正的超时设置
        SendResult result = rocketMQTemplate.syncSend("test-topic", MessageBuilder.withPayload(test).build(),2000,4);
        System.out.println("发送状态：" + result.getSendStatus());
    }

    /**
     * 功能：发送事务消息
     */
    @GetMapping("/sendTransactMsg")
    public void sendTransactMsg() {
        TestDto msgDto = new TestDto();
        msgDto.setParam1("111111");
        msgDto.setParam2("222222");
        msgDto.setParam3("333333");

//        String transactionId = UUID.randomUUID().toString();
        String transactionId = "123456";

        TestDto transactionDto = new TestDto();
        transactionDto.setParam1("111111");
        transactionDto.setParam2("222222");
        transactionDto.setParam3("333333");

        rocketMQTemplate.sendMessageInTransaction("tx-test-group", "tx-test-topic",
                MessageBuilder.withPayload(msgDto).
                        setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId).
                        setHeader("recordId", 111L).
                        build(),
                transactionDto);

        // 编写其他业务
    }
}