package com.horqian.api.context;


import com.horqian.api.exception.BaseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author bz
 */
@Component
public class UserContext {

    public static Integer getContext() {
        Integer userId;
        try {
            userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e) {
            throw new BaseException("用户未登录");
        }
        return userId;
    }
//
//    public static SysBaseUser baseUserContext() {
//        var jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (jwtUserDetails.getType().equals(UserTypeEnum.BASE.type)){
//            return (SysBaseUser)jwtUserDetails.getUser();
//        }
//        return null;
//    }
//
//    public static SysTradeUser tradeUserContext() {
//        var jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (jwtUserDetails.getType().equals(UserTypeEnum.TRADE.type)){
//            return (SysTradeUser)jwtUserDetails.getUser();
//        }
//        return null;
//
//    }
//
//    public static SysKaUser kaUserContext() {
//        var jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (jwtUserDetails.getType().equals(UserTypeEnum.KA.type)){
//            return (SysKaUser)jwtUserDetails.getUser();
//        }
//        return null;
//    }

}
