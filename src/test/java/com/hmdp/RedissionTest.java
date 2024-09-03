package com.hmdp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @version:1.0
 * @description
 * @create 2024-03-03
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissionTest {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedissonClient redissonClient2;
    @Resource
    private RedissonClient redissonClient3;
    
    private RLock lock;
    
    @BeforeEach
     void setUp(){
        RLock lock1 = redissonClient.getLock("order");
        RLock lock2 = redissonClient2.getLock("order");
        RLock lock3 = redissonClient3.getLock("order");
        //创建联锁
        lock = redissonClient.getMultiLock(lock1,lock2,lock3);
    }
    
    @Test
    public void method1() throws  InterruptedException{
        RLock lock1 = redissonClient.getLock("order");
        RLock lock2 = redissonClient2.getLock("order");
        RLock lock3 = redissonClient3.getLock("order");
        //创建联锁
        lock = redissonClient.getMultiLock(lock1,lock2,lock3);
    
        //尝试获取锁
        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock){
            log.error("获取锁失败 。。。1");
            return;
        }try {
            log.info("获取锁成功。。。1");
            method2();
            log.info("开始执行任务。。。1");
        }finally {
            log.warn("准备释放锁。。。。1");
            lock.unlock();
        }
    }
    @Test
    public void method2() throws  InterruptedException{
        RLock lock1 = redissonClient.getLock("order");
        RLock lock2 = redissonClient2.getLock("order");
        RLock lock3 = redissonClient3.getLock("order");
        //创建联锁
        lock = redissonClient.getMultiLock(lock1,lock2,lock3);
    
        //尝试获取锁
        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock){
            log.error("获取锁失败 。。。2");
            return;
        }try {
            log.info("获取锁成功。。。2");
            
            log.info("开始执行任务。。。2");
        }finally {
            log.warn("准备释放锁。。。。2");
            lock.unlock();
        }
    }
}
