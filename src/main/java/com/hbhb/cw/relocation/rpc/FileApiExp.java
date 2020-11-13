package com.hbhb.cw.relocation.rpc;

import com.hbhb.cw.systemcenter.api.SysFileApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${provider.system-center}", url = "${feign-url}", contextId = "SysFileApi", path = "file")
public interface FileApiExp extends SysFileApi {
}
