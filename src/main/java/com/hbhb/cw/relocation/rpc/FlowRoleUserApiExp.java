package com.hbhb.cw.relocation.rpc;


import com.hbhb.cw.flowcenter.api.FlowRoleUserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.flow-center}", url = "", path = "/role/user")
public interface FlowRoleUserApiExp extends FlowRoleUserApi {
}
