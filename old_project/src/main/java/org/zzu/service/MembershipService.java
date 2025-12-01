package org.zzu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zzu.feign.MembershipFeignClient;

/**
 * 会员服务
 */
@Service
public class MembershipService {

    private final MembershipFeignClient membershipFeignClient;

    @Autowired
    public MembershipService(MembershipFeignClient membershipFeignClient) {
        this.membershipFeignClient = membershipFeignClient;
    }

    /**
     * 检查用户是否是会员
     * @param userId 用户ID
     * @return 是否是会员
     */
    public boolean isUserMember(Integer userId) {
        try {
            ResponseEntity<Boolean> response = membershipFeignClient.checkMembership(userId);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            // 调用失败时记录日志并返回false
            System.err.println("调用会员服务失败: " + e.getMessage());
            return false;
        }
    }
}
