package com.loupe.project.camare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;

import com.loupe.project.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    private static final String TAG = VerticalSeekBar.class.getSimpleName();
    public static final int ROTATION_ANGLE_CW_90 = 90;//从上到下
    public static final int ROTATION_ANGLE_CW_270 = 270;//从下到上
    private int mRotationAngle = ROTATION_ANGLE_CW_270;//我这里是从下到上
    private IUpEventListener iUpEventListener;

    //用户滑动seekbar手指离开时的回调。（调用mSeekbar.setUpEvent();）
    public void setUpEvent(IUpEventListener iUpEventListener) {
        this.iUpEventListener = iUpEventListener;
    }
    public interface IUpEventListener {
        void upEvent();
    }

    public VerticalSeekBar(Context context) {
        super(context);//注意是super 而不是调用其他构造函数
        initialize(context, null, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, defStyleAttr, defStyleRes);
            final int rotationAngle = a.getInteger(R.styleable.VerticalSeekBar_seekBarRotation, 0);
            if (isValidRotationAngle(rotationAngle)) {
                mRotationAngle = rotationAngle;
            }
            a.recycle();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        if (mRotationAngle == ROTATION_ANGLE_CW_270) {
            //从下到上
            c.rotate(270);
            c.translate(-getHeight(), 0);//注意旋转后需要移动才可显示出来
        } else if (mRotationAngle == ROTATION_ANGLE_CW_90) {
            //从上到下
            c.rotate(90);
            c.translate(0, -getWidth());
        }

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(true);//移动的时候设置按下效果，然后重新绘制thumb
                if (getThumb() != null) {
                    // This may be within the padding region.
                    invalidate(getThumb().getBounds());
                }
                calProgress(event);//这里利用反射将fromuser设置为true
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);//放手的时候取消按下效果，重新绘制thumb
                if (getThumb() != null) {
                    // This may be within the padding region.
                    invalidate(getThumb().getBounds());
                }
                calProgress(event);//这里利用反射将fromuser设置为true
                if (null != iUpEventListener) {
                    iUpEventListener.upEvent();
                }
                break;
        }
        return true;
    }

    private static boolean isValidRotationAngle(int angle) {
        return (angle == ROTATION_ANGLE_CW_90 || angle == ROTATION_ANGLE_CW_270);
    }

    //直接调用seekbar.setProgress()发现thumb图片位置一直不对，解决方法如下 重写setProgress
    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    //fromUser参数一直为false，利用反射
    private void reflectSetProgress(int progress) {
        //反射方法 boolean setProgressInternal(int progress, boolean fromUser, boolean animate)
        try {
            Class clazz = ProgressBar.class;
            Method method = clazz.getDeclaredMethod("setProgressInternal", int.class, boolean.class, boolean.class);
            method.setAccessible(true);
            method.invoke(this, progress, true, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void calProgress(MotionEvent event) {
        if (mRotationAngle == ROTATION_ANGLE_CW_270) {
            //从下到上
            int progress = getMax() - (int) (event.getY() * (getMax() * 2 + 1) / (getHeight() * 2) + 0.5);
            progress = progress > 0 ? progress : 0;
            reflectSetProgress(progress);
        } else if (mRotationAngle == ROTATION_ANGLE_CW_90) {
            //从上到下
            reflectSetProgress((int) (getMax() * event.getY() / getHeight()));
        }
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
