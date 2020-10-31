package com.rose.controler;

import com.rose.data.to.dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
            rocketMQTemplate.convertAndSend("sss-topic", test);
        }
    }
}