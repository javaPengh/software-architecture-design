package org.zzu.membership.service;

import org.zzu.membership.mapper.MembershipMapper;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipMapper membershipMapper;

    public MembershipService(MembershipMapper membershipMapper) {
        this.membershipMapper = membershipMapper;
    }

    public boolean isUserMember(Long userId) {
        // 这里简单返回true或false作为示例
        // 实际项目中应该调用mapper查询数据库
        return membershipMapper.isUserMember(userId);
    }

    public boolean readTest(Long userId) {
        return membershipMapper.isUserMember(userId);
    }

    public int writeTest(String note) {
        membershipMapper.createTestTable();
        return membershipMapper.insertWriteTest(note);
    }
}
