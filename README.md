# chatserver

> 网页在线聊天系统后端

## Backend Stack

> `Spring Boot`、`Spring Security`、`Socket.io`、`Redis`、`MongoDB`、`Nginx`、`FastDFS`等。

## Description

- 该系统是基于前后端分离，采用`Spring Boot + Vue + React`开发的网页版聊天系统。

  - 前端使用`Vue.js`和`React`开发，`WebSocket`增强界面实时交互效果，`Element-UI`组件库提高用户体验，`WebRTC`技术实现`1v1`白板协作和语音视频通话功能。
  - 后端使用`Spring Boot` + `Spring Security`框架开发，结合`JWT`实现用户登录和权限认证，`MongoDB`用来存储该系统的所有数据，`Redis`不仅用来存储临时的验证码等待校验请求，还维护了所有在线的客户端及对应的用户信息；`netty-socketio`监听前端`WebSocket`发来的消息并处理后主动推送该消息给在线的目标客户端。
  - 搭建`FastDFS`服务器用来存储上传的图片和文件，搭建`coturn`中继服务器来收集端点信息，使得公网中任意2台不同主机能够进行`P2P`通话或白板协作。通过配置`Nginx`反向代理请求转发到内部端口，隐藏了真实的访问地址，提高了访问安全性。
- 已实现的功能：私聊、群聊、上传图片、文件、`1v1`白板协作和语音视频通话、敏感词过滤、历史消息、表情发送、消息已读提醒、好友分组、好友备注、在线用户头像高亮、添加好友、添加群聊、日程设置等。
- 运行本项目前，需要修改这些服务的连接参数：`Redis`、`MongoDB`、`fastDFS`。

  - `MongoDB`搭建教程：[win10下安装MongoDB](https://www.jianshu.com/p/2ab39e37d0fb) | [Centos7 安装MongoDB](https://www.jianshu.com/p/681d584d9281)

  - `fastDFS`搭建教程：[Centos7.x 搭建FastDFS并通过Nginx配置http或https访问](https://www.jianshu.com/p/e60797e328d3)
  - 修改配置文件中的参数：`fastdfs.nginx.host=文件服务器的域名或IP`
- 打包前先将`ChatServerApplicationTests.java`文件注释掉，然后点击`Maven`->`package`，将生成的`jar`包上传到服务器，执行后台运行该项目的命令：`nohup java -jar chatserver-0.0.1-SNAPSHOT.jar &`，项目运行过程中使用命令：`tail -f nohup.out`实时查看项目运行日志。
- 项目演示地址：[点我，传送门](https://www.bilibili.com/video/bv1Xo4y1C7Bv)

