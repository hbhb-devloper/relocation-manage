package com.hbhb.cw.relocation.rpc;

import com.hbhb.cw.messagehub.api.MailApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "message-hub", path = "mail")
public interface MailApiExp extends MailApi {

}
