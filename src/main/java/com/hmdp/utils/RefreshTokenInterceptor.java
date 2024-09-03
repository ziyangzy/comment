package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * @author
 * @version:1.0
 * @description
 * @create 2024-02-02
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {
    
    private StringRedisTemplate stringRedisTemplate;
    
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
//        //1获取session 
//        HttpSession session = request.getSession();
        //获取请求头的token 
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)){
            return true;
        }
//        //2获取用户在登录的login时存入session的用户
//        Object user = session.getAttribute("user");
        //通过token获取redis中的用户信息  entries可能返回空的map
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash()
                .entries(key);
        //3判断用户是否存在
        if (userMap.isEmpty()){
            return true;
        }
        //hash转userDto
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap,new UserDTO(),false);
        //5存在 保存用户信息到UserHolder类的ThreadLocal  每个请求都是一个线程存ThreadLocal里，user存线程内部
        UserHolder.saveUser(userDTO);
        //刷新token时间
        stringRedisTemplate.expire(key,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }
    
    //页面回显完后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        //移除用户 
        UserHolder.removeUser();
    }
}
