package com.rose.rocketmq;

import com.rose.common.util.JsonUtil;
import com.rose.data.to.dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Slf4j
@RocketMQMessageListener(consumerGroup = "consumer-test", topic = "sss-topic")
@Service
public class RocketMqTestListener implements RocketMQListener<TestDto> {

    @Override
    public void onMessage(TestDto testDto) {
        log.info("接受到消息：{}", JsonUtil.objectToJson(testDto));
    }
}