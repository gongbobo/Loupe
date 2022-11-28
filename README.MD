ReadMe####
MainActivity中
搜索//todo ，比如//todo 1-2-内部广告位逻辑-进入相册，可以直接在对应的下面写调用广告逻辑；
1-X，2-X，对应文档https://pic6ktmsyi.feishu.cn/docx/Qv0Tdy32VouOBWxAr1ucyk1inpd

TimerUtils时间倒计时工具，可以拓展，目前flag值从1000到1007，特殊1111是启动页；

ScreenLockActivity，这个类是弹框类，支持跨进程，跟文档的写法一致，直接通过FG调用，后续交给sdk去完成广告逻辑，类似于容器的作用；

SplashActivity，启动页，目前是默认倒计时1秒后进入主页，可根据业务控制时长。


