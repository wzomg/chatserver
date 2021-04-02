package com.zzw.chatserver.controller;

import com.zzw.chatserver.common.R;
import com.zzw.chatserver.filter.SensitiveFilter;
import com.zzw.chatserver.pojo.FeedBack;
import com.zzw.chatserver.pojo.vo.SystemUserResponseVo;
import com.zzw.chatserver.service.SysService;
import com.zzw.chatserver.utils.FastDFSUtil;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sys")
public class SysController {

    @Resource
    private SysService sysService;

    @Value("${fastdfs.nginx.host}")
    private String nginxHost;

    @Resource
    private SensitiveFilter sensitiveFilter;


    /**
     * 获取注册时的头像列表
     */
    @GetMapping("/getFaceImages")
    @ResponseBody
    public R getFaceImages() {
        //String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/face";
        //System.out.println(path);
        ArrayList<String> files = new ArrayList<>();
        //File file = new File(path);
        /*for (File item : Objects.requireNonNull(file.listFiles())) {
            files.add(item.getName());
        }*/
        for (int i = 1; i <= 22; i++) {
            files.add("face" + i + ".jpg");
        }
        files.add("ronaldo1.jpg");
        return R.ok().data("files", files);
    }

    /**
     * 获取系统用户
     */
    @GetMapping("/getSysUsers")
    @ResponseBody
    public R getSysUsers() {
        List<SystemUserResponseVo> sysUsers = sysService.getSysUsers();
        // System.out.println("系统用户有：" + sysUsers);
        return R.ok().data("sysUsers", sysUsers);
    }

    /**
     * 上传文件
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public R uploadFile(MultipartFile file) throws IOException, MyException {
        //根据扩展名来设置消息类型：emoji/text/img/file/sys/whiteboard/video/audio
        String filePartName = FastDFSUtil.uploadFile(file);
        String filePath = nginxHost + filePartName;
        // System.out.println("在服务器的文件名为：" + filePartName);
        return R.ok().data("filePath", filePath);
    }

    //获取文件的真实地址，主要用于防盗链
    /*@GetMapping("/getRealFilePath")
    public R getRealFilePath(String fileId) throws UnsupportedEncodingException, NoSuchAlgorithmException, MyException {
        String fileUrl = FastDFSUtil.getToken(fileId);
        System.out.println("返回的真实路径为：" + fileUrl);
        return R.ok().data("realFilePath", fileUrl);
    }*/

    /**
     * 提供文件下载
     */
    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam("fileId") String fileId,
                             @RequestParam("fileName") String fileName,
                             HttpServletResponse resp) {
        try {
            byte[] bytes = FastDFSUtil.downloadFile(fileId);
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = resp.getOutputStream();
            IOUtils.write(bytes, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 搜索好友或加过的群聊列表
     */
    /*@GetMapping("/topSearch")
    @ResponseBody
    public R topSearch(String keyword) {

        return R.ok();
    }*/

    /**
     * 用户反馈
     */
    @PostMapping("/addFeedBack")
    @ResponseBody
    public R addFeedBack(@RequestBody FeedBack feedBack) {
        // System.out.println("反馈请求参数为：" + feedBack);
        sysService.addFeedBack(feedBack);
        return R.ok().message("感谢您的反馈！");
    }

    /**
     * 过滤发送的消息
     */
    @GetMapping("/filterMessage")
    @ResponseBody
    public R filterMessage(String message) {
        String filterContent = sensitiveFilter.filter(message);
        return R.ok().data("message", filterContent);
    }
}