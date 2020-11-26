package com.hbhb.cw.relocation.rpc;

import com.hbhb.cw.systemcenter.api.DictApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${provider.system-center}", url = "${feign-url}", contextId = "SysDictApi", path = "dict")
public interface SysDictApiExp extends DictApi {
}
