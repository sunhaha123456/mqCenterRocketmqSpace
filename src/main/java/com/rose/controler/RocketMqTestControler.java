package com.rose.controler;

import com.rose.data.to.dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    }
}