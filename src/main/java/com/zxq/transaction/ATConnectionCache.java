package com.zxq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 事务连接缓存
 * 收集connection
 *
 * @author zhaoxiqing
 */
@Slf4j
@Component
public class ATConnectionCache {
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
