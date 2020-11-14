package com.hbhb.cw.relocation.rpc;

import com.hbhb.cw.flow.manage.api.FlowApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.cw-backend}", url = "${feign-url}", contextId = "FlowApi", path = "flow-role")
public interface FlowApiExp extends FlowApi {
}
