package org.zzu.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockService {
    private final CuratorFramework curatorFramework;
    
    public DistributedLockService(@Value("${zookeeper.connect-string}") String connectString) {
        this.curatorFramework = CuratorFrameworkFactory.newClient(connectString, 
            new ExponentialBackoffRetry(1000, 3));
        this.curatorFramework.start();
    }
    
    public InterProcessMutex acquireLock(String lockPath) throws Exception {
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, lockPath);
        lock.acquire();
        return lock;
    }
    
    public void releaseLock(InterProcessMutex lock) throws Exception {
        if (lock != null && lock.isAcquiredInThisProcess()) {
            lock.release();
        }
    }
}