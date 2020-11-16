package com.hbhb.cw.relocation.rpc;


import com.hbhb.cw.flowcenter.api.FlowApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.flow-center}", url = "", path = "/flow-role")
public interface FlowApiExp extends FlowApi {
}
