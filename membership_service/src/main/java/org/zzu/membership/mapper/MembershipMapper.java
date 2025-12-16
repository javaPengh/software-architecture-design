package org.zzu.membership.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MembershipMapper {
    
    /**
     * 根据用户ID判断是否是会员
     * @param userId 用户ID
     * @return 是否是会员
     */
    boolean isUserMember(@Param("userId") Long userId);

    void createTestTable();

    int insertWriteTest(@Param("note") String note);
}
