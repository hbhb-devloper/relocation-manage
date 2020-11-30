package com.hbhb.cw.relocation.service.impl;

import com.hbhb.cw.messagehub.vo.MailVO;
import com.hbhb.cw.relocation.rpc.MailApiExp;
import com.hbhb.cw.relocation.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {
    @Resource
    private MailApiExp mailApi;

    @Value("${mail.title}")
    private String title;
    @Value("${mail.content}")
    private String content;

    @Override
    public void postMail(String receiver, String name, String flowName) {
        mailApi.postMail(MailVO.builder()
                .receiver(receiver)
                .title(String.format(title, flowName))
                .content(String.format(content, name, flowName))
                .build());
    }
}
