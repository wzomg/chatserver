package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.pojo.User;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.service.UserService;
import com.zzw.chatserver.utils.ChatServerUtil;
import com.zzw.chatserver.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码
     */
    @GetMapping("/getCode")
    public R getKaptcha(HttpServletResponse response) {
        String kaptchaOwner = ChatServerUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        // 验证码有效时间为 60 s，注意多加8个小时，相差8个时区
        cookie.setMaxAge(60 + 8 * 60 * 60);
        // 整个项目都有效，注意路径设置
        cookie.setPath("/");
        // 发送给客户端
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        String verificationCode = ChatServerUtil.generatorCode();
        // redis 有效时间为 60s
        redisTemplate.opsForValue().set(redisKey, verificationCode, 60, TimeUnit.SECONDS);
        // System.out.println("生成的验证码uuid：" + kaptchaOwner + "验证码为：" + verificationCode);
        return R.ok().data("code", verificationCode);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R register(@RequestBody RegisterRequestVo rVo) {
        Map<String, Object> resMap = userService.register(rVo);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.REGISTER_SUCCESS.getCode()))
            return R.ok().resultEnum(ResultEnum.REGISTER_SUCCESS).data("userCode", resMap.get("userCode"));
        else return R.error().code(code).message((String) resMap.get("msg"));
    }

    /**
     * 添加新的分组
     */
    @PostMapping("/addFenZu")
    public R addNewFenZu(@RequestBody NewFenZuRequestVo requestVo) {
        userService.addNewFenZu(requestVo);
        return R.ok().message("添加新分组成功");
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/getUserInfo")
    public R getUserInfo(String uid) {
        User userInfo = userService.getUserInfo(uid);
        return R.ok().data("userInfo", userInfo);
    }

    /**
     * 修改好友备注信息
     */
    @PostMapping("/modifyFriendBeiZhu")
    public R modifyFriendBeiZhu(@RequestBody ModifyFriendBeiZhuRequestVo requestVo) {
        userService.modifyBeiZhu(requestVo);
        return R.ok().message("修改备注成功！");
    }


    /**
     * 修改好友分组
     */
    @PostMapping("/modifyFriendFenZu")
    public R modifyFriendFenZu(@RequestBody ModifyFriendFenZuRequestVo requestVo) {
        // System.out.println("修改分组的请求参数为：" + requestVo);
        userService.modifyFriendFenZu(requestVo);
        return R.ok().message("修改分组成功！");
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/delFenZu")
    public R deleteFenZu(@RequestBody DelFenZuRequestVo requestVo) {
        userService.deleteFenZu(requestVo);
        return R.ok().message("删除成功！");
    }

    /**
     * 更新分组名（编辑分组）
     */
    @PostMapping("/editFenZu")
    public R editFenZu(@RequestBody EditFenZuRequestVo requestVo) {
        userService.editFenZu(requestVo);
        return R.ok().message("更新成功！");
    }

    /**
     * 在客户端搜索好友
     */
    @PostMapping("/preFetchUser")
    public R searchUser(@RequestBody SearchRequestVo requestVo) {
        // System.out.println("搜索的用户信息为：" + requestVo);
        // System.out.println("当前安全认证信息为：" + SecurityContextHolder.getContext().getAuthentication());
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        List<User> userList = userService.searchUser(requestVo, userId);
        // System.out.println("搜索的用户信息返回的结果为：" + userList);
        return R.ok().data("userList", userList);
    }

    /**
     * 更新用户的重要信息
     */
    @PostMapping("/updateUserInfo")
    public R updateUserInfo(@RequestBody UpdateUserInfoRequestVo requestVo) {
        // System.out.println("更新用户信息的请求参数为：" + requestVo);
        Map<String, Object> resMap = userService.updateUserInfo(requestVo);
        if (resMap.size() > 0) return R.error().code((Integer) resMap.get("code")).message((String) resMap.get("msg"));
        else return R.ok().message("修改成功");
    }

    /**
     * 更新用户的一些配置信息
     */
    @PostMapping("/updateUserConfigure")
    public R updateUserConfigure(@RequestBody UpdateUserConfigureRequestVo requestVo) {
        // System.out.println("更新用户的一些配置信息为：" + requestVo);
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        boolean res = userService.updateUserConfigure(requestVo, userId);
        if (res) {
            User userInfo = userService.getUserInfo(userId); //同时携带用户信息去更新全局的用户信息
            return R.ok().data("userInfo", userInfo);
        } else return R.error();
    }

    /**
     * 更新用户密码
     */
    @PostMapping("/updateUserPwd")
    public R updateUserPwd(@RequestBody UpdateUserPwdRequestVo requestVo) {
        // System.out.println("更新密码的请求参数为：" + requestVo);
        Map<String, Object> resMap = userService.updateUserPwd(requestVo);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode()))
            return R.ok().message((String) resMap.get("msg"));
        else return R.error().code(code).message((String) resMap.get("msg"));
    }
}
