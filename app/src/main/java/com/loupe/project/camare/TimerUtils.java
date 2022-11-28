package com.loupe.project.camare;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 静态内部类单例，可拓展
 * <p>
 * 倒计时工具类，每创建一个倒计时，需要传入一个对应的flag、接口回调事件
 */
public class TimerUtils {

    private TimerUtils() {
    }

    private static class Holder {
        static TimerUtils instance = new TimerUtils();
    }

    public static TimerUtils getInstance() {
        return Holder.instance;
    }

    private Timer timerHome, timerPhone, timerWifi, timerScreenOn, timerUsb, timerLow, timerBlue, timerPackage;
    private int currentHomeNs = 0, currentPhoneNs = 0, currentWifiNs = 0, currentScreenOnNs = 0, currentUsbNs = 0, currentLowNs = 0, currentBlueNs = 0, currentPackageNs = 0;

    private Timer timerSplash;
    private int currentSplashNs = 0;

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1000) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onHomeTimerBack();
                        if (timerHome != null) {
                            timerHome.cancel();
                            timerHome = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1000);
                        }
                    }
                }
            } else if (message.what == 1001) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onPhoneTimeBack();
                        if (timerPhone != null) {
                            timerPhone.cancel();
                            timerPhone = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1001);
                        }
                    }
                }

            } else if (message.what == 1002) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onWifiTimerBack();
                        if (timerWifi != null) {
                            timerWifi.cancel();
                            timerWifi = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1002);
                        }
                    }
                }
            } else if (message.what == 1003) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onScreenOnTimerBack();
                        if (timerScreenOn != null) {
                            timerScreenOn.cancel();
                            timerScreenOn = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1003);
                        }
                    }
                }
            } else if (message.what == 1004) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onUsbTimerBack();
                        if (timerUsb != null) {
                            timerUsb.cancel();
                            timerUsb = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1004);
                        }
                    }
                }
            } else if (message.what == 1005) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onLowTimerBack();
                        if (timerLow != null) {
                            timerLow.cancel();
                            timerLow = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1005);
                        }
                    }
                }
            } else if (message.what == 1006) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onBlueTimerBack();
                        if (timerBlue != null) {
                            timerBlue.cancel();
                            timerBlue = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1006);
                        }
                    }
                }
            } else if (message.what == 1007) {
                if (message.arg1 <= 0) {
                    if (onTimerFinishInterface != null) {
                        onTimerFinishInterface.onPackageTimerBack();
                        if (timerPackage != null) {
                            timerPackage.cancel();
                            timerPackage = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1007);
                        }
                    }
                }
            } else if (message.what == 1111) {
                if (message.arg1 <= 0) {
                    if (onSplashFinishInterface != null) {
                        onSplashFinishInterface.onSplashTimerBack();
                        if (timerSplash != null) {
                            timerSplash.cancel();
                            timerSplash = null;
                        }
                        if (handler != null) {
                            handler.removeMessages(1111);
                        }
                    }
                }
            }
            return false;
        }
    });

    /**
     * flag
     * 1000 Home键按下；1001 来电接通；1002 WIFI连接/关闭；1003 屏幕解锁；1004 数据线插入/拔出；1005 低电量；1006 蓝牙打开/关闭/连接设备；1007 应用安装/卸载
     * 1111 启动页倒计时
     * Ns 倒计时时间
     * delay 延时执行
     * period 倒计时间隔
     */
    public void startTimer(final int flag, int ns, long delay, long period) {
        if (flag == 1000) {
            currentHomeNs = ns;
            if (timerHome == null) {
                timerHome = new Timer();
            }
            timerHome.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentHomeNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1001) {
            currentPhoneNs = ns;
            if (timerPhone == null) {
                timerPhone = new Timer();
            }
            timerPhone.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentPhoneNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1002) {
            currentWifiNs = ns;
            if (timerWifi == null) {
                timerWifi = new Timer();
            }
            timerWifi.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentWifiNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1003) {
            currentScreenOnNs = ns;
            if (timerScreenOn == null) {
                timerScreenOn = new Timer();
            }
            timerScreenOn.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentScreenOnNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1004) {
            currentUsbNs = ns;
            if (timerUsb == null) {
                timerUsb = new Timer();
            }
            timerUsb.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentUsbNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1005) {
            currentLowNs = ns;
            if (timerLow == null) {
                timerLow = new Timer();
            }
            timerLow.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentLowNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1006) {
            currentBlueNs = ns;
            if (timerBlue == null) {
                timerBlue = new Timer();
            }
            timerBlue.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentBlueNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1007) {
            currentPackageNs = ns;
            if (timerPackage == null) {
                timerPackage = new Timer();
            }
            timerPackage.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentPackageNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        } else if (flag == 1111) {
            currentSplashNs = ns;
            if (timerSplash == null) {
                timerSplash = new Timer();
            }
            timerSplash.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = flag;
                    message.arg1 = currentSplashNs--;
                    handler.sendMessage(message);
                }
            }, delay, period);
        }
    }

    /**
     * 接口回调
     */
    public interface OnTimerFinishInterface {
        void onHomeTimerBack();

        void onPhoneTimeBack();

        void onWifiTimerBack();

        void onScreenOnTimerBack();

        void onUsbTimerBack();

        void onLowTimerBack();

        void onBlueTimerBack();

        void onPackageTimerBack();
    }

    public OnTimerFinishInterface onTimerFinishInterface;

    public void setOnTimerFinishInterface(OnTimerFinishInterface onTimerFinishInterface) {
        this.onTimerFinishInterface = onTimerFinishInterface;
    }

    public interface OnSplashFinishInterface {
        void onSplashTimerBack();
    }

    public OnSplashFinishInterface onSplashFinishInterface;

    public void setOnSplashFinishInterface(OnSplashFinishInterface onSplashFinishInterface) {
        this.onSplashFinishInterface = onSplashFinishInterface;
    }

    public void cancelTimer() {
        if (timerHome != null) {
            timerHome.cancel();
            timerHome = null;
        }
        if (timerPhone != null) {
            timerPhone.cancel();
            timerPhone = null;
        }
        if (timerWifi != null) {
            timerWifi.cancel();
            timerWifi = null;
        }
        if (timerScreenOn != null) {
            timerScreenOn.cancel();
            timerScreenOn = null;
        }

        if (handler != null) {
            handler.removeMessages(1000);
            handler.removeMessages(1001);
            handler.removeMessages(1002);
            handler.removeMessages(1003);
            handler.removeMessages(1004);
            handler.removeMessages(1005);
            handler.removeMessages(1006);
            handler.removeMessages(1007);
            handler.removeMessages(1111);
        }
    }

}
