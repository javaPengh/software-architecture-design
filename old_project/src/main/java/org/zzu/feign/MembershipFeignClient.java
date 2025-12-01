package org.zzu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 会员服务Feign客户端
 */
@FeignClient(name = "membership-service", path = "/api/membership")
public interface MembershipFeignClient {
    
    /**
     * 检查用户是否是会员
     * @param userId 用户ID
     * @return 是否是会员
     */
    @GetMapping("/check/{userId}")
    ResponseEntity<Boolean> checkMembership(@PathVariable("userId") Integer userId);
}
