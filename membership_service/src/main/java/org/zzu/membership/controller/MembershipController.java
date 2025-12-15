package org.zzu.membership.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zzu.membership.service.MembershipService;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/check/{userId}")
    @SentinelResource(
        value = "checkMembership",
        blockHandler = "handleCheckMembershipBlock",
        fallback = "checkMembershipFallback"
    )
    public ResponseEntity<Boolean> checkMembership(@PathVariable Long userId) {
        boolean isMember = membershipService.isUserMember(userId);
        return ResponseEntity.ok(isMember);
    }
    
    /**
     * 限流处理函数
     */
    public ResponseEntity<Boolean> handleCheckMembershipBlock(Long userId, BlockException ex) {
        // 返回限流提示信息，这里返回false表示非会员（也可以根据业务需求调整）
        return ResponseEntity.status(429).header("X-RateLimit-Remaining", "0").body(false);
    }
    
    /**
     * 降级处理函数
     */
    public ResponseEntity<Boolean> checkMembershipFallback(Long userId, Throwable e) {
        // 返回降级后的默认值，这里返回false表示非会员
        return ResponseEntity.ok(false);
    }

    @GetMapping("/checkPing")
    public ResponseEntity<String> checkPing() {
        return ResponseEntity.ok("请求通的");
    }

    @GetMapping("/secure")
    @RequiresRoles("member")
    public ResponseEntity<String> memberOnly() {
        return ResponseEntity.ok("member角色可访问");
    }

    @GetMapping("/rw/read/{userId}")
    public ResponseEntity<Boolean> readTest(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.readTest(userId));
    }

    @PostMapping("/rw/write")
    public ResponseEntity<Integer> writeTest(@RequestParam(defaultValue = "write-test") String note) {
        int affected = membershipService.writeTest(note);
        return ResponseEntity.ok(affected);
    }
}
