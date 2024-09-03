package com.hmdp.utils;

/**
 * @author
 * @version:1.0
 * @description
 * @create 2024-03-02
 */
public interface ILock {
    //尝试获取锁
    boolean tryLock(long timeoutSec);
    
    void unlock();
}
