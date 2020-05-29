package com.zxq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务连接缓存
 * 收集connection
 *
 * @author zhaoxiqing
 */
@Slf4j
@Component
public class ATConnectionCache {
    private AtomicReference<String> groupId = new AtomicReference();

    public String getGroupId() {
        return groupId.get();
    }

    public void setGroupId(String groupId) {
        this.groupId.set(groupId);
    }
}
