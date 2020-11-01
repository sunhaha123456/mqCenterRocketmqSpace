package com.rose.rocketmq;

import com.rose.data.to.dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@RocketMQTransactionListener(txProducerGroup = "tx-test-group")
@Service
public class RocketMqTestTransactionListener implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Long recordId = Long.valueOf(headers.get("recordId") + "");

        try {
            // 执行本地事务，具体是：调一个serverice的方法，记住，service上要写@Transaction事务注解
            TestDto transactionDto = (TestDto) arg; // 获取要执行的事务对象

            // 本地事务需要记录一条是否执行事务成功标志

            log.info("执行本地事务成功！");
            return RocketMQLocalTransactionState.COMMIT;    // xxx
        } catch (Exception e) {
            log.info("执行本地事务失败！");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    // 功能：mq，未收到确认消息时，会调用这个方法，来确认事务是否成功，比如，在xxx处断掉
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        if (transactionId.equals("123456")) { // 当这条事务是执行成功的时候
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}