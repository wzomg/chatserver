package com.zzw.chatserver.service;

import com.zzw.chatserver.common.ConstValueEnum;
import com.zzw.chatserver.common.ResultEnum;
import com.zzw.chatserver.dao.AccountPoolDao;
import com.zzw.chatserver.dao.UserDao;
import com.zzw.chatserver.pojo.AccountPool;
import com.zzw.chatserver.pojo.User;
import com.zzw.chatserver.pojo.vo.*;
import com.zzw.chatserver.utils.ChatServerUtil;
import com.zzw.chatserver.utils.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private AccountPoolDao accountPoolDao;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Map<String, Object> register(RegisterRequestVo rVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = null;
        String msg = null;
        String userCode = null;
        if (!rVo.getRePassword().equals(rVo.getPassword())) {
            code = ResultEnum.INCORRECT_PASSWORD_TWICE.getCode();
            msg = ResultEnum.INCORRECT_PASSWORD_TWICE.getMessage();
        }
        User existUser = userDao.findUserByUsername(rVo.getUsername());
        if (existUser != null) {
            code = ResultEnum.USER_HAS_EXIST.getCode();
            msg = ResultEnum.USER_HAS_EXIST.getMessage();
        } else {
            //生成用户唯一标识账号code
            AccountPool accountPool = new AccountPool();
            accountPool.setType(ConstValueEnum.USERTYPE);//类型：用户
            accountPool.setStatus(ConstValueEnum.ACCOUNT_USED); //已使用状态
            accountPoolDao.save(accountPool);
            //=================================
            String newPass = bCryptPasswordEncoder.encode(rVo.getPassword());//加密
            User user = new User();
            user.setUsername(rVo.getUsername());
            user.setPassword(newPass);
            user.setCode(String.valueOf(accountPool.getCode() + ConstValueEnum.INITIAL_NUMBER));
            user.setPhoto(rVo.getAvatar());
            user.setNickname(ChatServerUtil.randomNickname());
            userDao.save(user);
            userCode = user.getCode();
            if (user.getUserId() != null) {
                code = ResultEnum.REGISTER_SUCCESS.getCode();
                msg = ResultEnum.REGISTER_SUCCESS.getMessage();
            } else {
                code = ResultEnum.REGISTER_FAILED.getCode();
                msg = ResultEnum.REGISTER_FAILED.getMessage();
            }
        }
        map.put("code", code);
        map.put("msg", msg);
        map.put("userCode", userCode);
        return map;
    }

    public void addNewFenZu(NewFenZuRequestVo requestVo) {
        User userInfo = userDao.findById(new ObjectId(requestVo.getUserId())).orElse(null);
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        if (!friendFenZuMap.containsKey(requestVo.getFenZuName())) {
            friendFenZuMap.put(requestVo.getFenZuName(), new ArrayList<>());
            Update update = new Update();
            update.set("friendFenZu", friendFenZuMap);
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
            mongoTemplate.findAndModify(query, update, User.class);
        }
    }

    public User getUserInfo(String userId) {
        return userDao.findById(new ObjectId(userId)).orElse(null);
    }


    public void modifyBeiZhu(ModifyFriendBeiZhuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, String> friendBeiZhuMap = userInfo.getFriendBeiZhu();
        friendBeiZhuMap.put(requestVo.getFriendId(), requestVo.getFriendBeiZhuName());
        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendBeiZhu", friendBeiZhuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    public void modifyFriendFenZu(ModifyFriendFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        boolean flag = false;
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        // System.out.println("分组map：" + friendFenZuMap);
        for (Map.Entry<String, ArrayList<String>> item : friendFenZuMap.entrySet()) {
            Iterator<String> iterator = item.getValue().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(requestVo.getFriendId())) {
                    //原来已经在某个分组就从中去掉
                    if (!item.getKey().equals(requestVo.getNewFenZuName())) iterator.remove();
                    flag = true;//没必要再循环了
                    break;
                }
            }
            if (flag) break;
        }
        friendFenZuMap.get(requestVo.getNewFenZuName()).add(requestVo.getFriendId());
        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    public void deleteFenZu(DelFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        friendFenZuMap.remove(requestVo.getFenZuName());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    public void editFenZu(EditFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        ArrayList<String> oldFenZuUsers = friendFenZuMap.get(requestVo.getOldFenZu());
        friendFenZuMap.remove(requestVo.getOldFenZu());
        friendFenZuMap.put(requestVo.getNewFenZu(), oldFenZuUsers);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    public List<User> searchUser(SearchRequestVo requestVo, String uid) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where(requestVo.getType()).regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE))
                        .and("uid").ne(uid)
        ).with(Sort.by(Sort.Direction.DESC, "_id"))
                .skip(requestVo.getPageIndex() * requestVo.getPageSize())
                .limit(requestVo.getPageSize());
        return mongoTemplate.find(query, User.class);
    }

    public void updateOnlineTime(long onlineTime, String uid) {
        Update update = new Update();
        update.set("onlineTime", onlineTime);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));
        mongoTemplate.upsert(query, update, User.class);
    }

    public Map<String, Object> updateUserInfo(UpdateUserInfoRequestVo requestVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = null;
        String msg = null;
        Update update = new Update();
        boolean flag = false;
        //需要特别判断数字型字段，否则查询的时候会出错
        if (requestVo.getField().equals("sex")) {
            String sexStr = (String) requestVo.getValue();
            if (!ChatServerUtil.isNumeric(sexStr)) { //非数字
                code = ResultEnum.ERROR_SETTING_GENDER.getCode();
                msg = ResultEnum.ERROR_SETTING_GENDER.getMessage();
                flag = true;
            } else {
                Integer sex = Integer.valueOf(sexStr);
                if (sex != 0 && sex != 1 && sex != 3) {
                    code = ResultEnum.ERROR_SETTING_GENDER.getCode();
                    msg = ResultEnum.ERROR_SETTING_GENDER.getMessage();
                    flag = true;
                } else
                    update.set(requestVo.getField(), sex);
            }
        } else if (requestVo.getField().equals("age")) {
            String age = requestVo.getValue().toString();
            if (!ChatServerUtil.isNumeric(age)) {
                code = ResultEnum.ERROR_SETTING_AGE.getCode();
                msg = ResultEnum.ERROR_SETTING_AGE.getMessage();
                flag = true;
            } else update.set(requestVo.getField(), Integer.valueOf(age));
        } else if (requestVo.getField().equals("email")) {
            String email = (String) requestVo.getValue();
            if (!ChatServerUtil.isEmail(email)) {
                code = ResultEnum.ERROR_SETTING_EMAIL.getCode();
                msg = ResultEnum.ERROR_SETTING_EMAIL.getMessage();
                flag = true;
            } else update.set(requestVo.getField(), email);
        } else update.set(requestVo.getField(), requestVo.getValue());
        if (!flag) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
            mongoTemplate.upsert(query, update, User.class);
        } else {
            map.put("code", code);
            map.put("msg", msg);
        }
        return map;
    }

    public Map<String, Object> updateUserPwd(UpdateUserPwdRequestVo requestVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = null;
        String msg = null;
        if (!requestVo.getReNewPwd().equals(requestVo.getNewPwd())) { //两次密码不一致
            code = ResultEnum.INCORRECT_PASSWORD_TWICE.getCode();
            msg = ResultEnum.INCORRECT_PASSWORD_TWICE.getMessage();
        } else {
            User userInfo = this.getUserInfo(requestVo.getUserId());
            if (!bCryptPasswordEncoder.matches(requestVo.getOldPwd(), userInfo.getPassword())) {//使用matches方法（参数1：不经过加密的密码，参数2：已加密密码）
                code = ResultEnum.OLD_PASSWORD_ERROR.getCode();
                msg = ResultEnum.OLD_PASSWORD_ERROR.getMessage();
            } else {
                String bCryptNewPwd = bCryptPasswordEncoder.encode(requestVo.getNewPwd());
                //更新旧密码
                Update update = new Update();
                update.set("password", bCryptNewPwd);
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
                mongoTemplate.upsert(query, update, User.class);
                code = ResultEnum.SUCCESS.getCode();
                msg = "更新成功，请牢记你的新密码";
            }
        }
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    //更新用户配置
    public boolean updateUserConfigure(UpdateUserConfigureRequestVo requestVo, String uid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));
        Update update = new Update();
        update.set("opacity", requestVo.getOpacity())
                .set("blur", requestVo.getBlur())
                .set("bgImg", requestVo.getBgImg())
                .set("customBgImgUrl", requestVo.getCustomBgImgUrl())
                .set("notifySound", requestVo.getNotifySound())
                .set("color", requestVo.getColor())
                .set("bgColor", requestVo.getBgColor());
        return mongoTemplate.upsert(query, update, User.class).getModifiedCount() > 0;
    }

    //获取所有用户信息
    public List<User> getUserList() {
        return userDao.findAll();
    }

    // 根据注册时间获取用户
    public List<User> getUsersBySignUpTime(String lt, String rt) {
        Query query = new Query();
        query.addCriteria(Criteria.where("signUpTime").gte(DateUtil.parseDate(lt, DateUtil.yyyy_MM))
                .lte(DateUtil.parseDate(rt, DateUtil.yyyy_MM)));
        return mongoTemplate.find(query, User.class);
    }

    public void changeUserStatus(String uid, Integer status) {
        Update update = new Update();
        update.set("status", status);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));
        mongoTemplate.findAndModify(query, update, User.class);
    }
}
