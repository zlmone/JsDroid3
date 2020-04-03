package com.yhao.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by yhao on 17-11-14.
 * https://github.com/yhaolpz
 */

public class FloatPhone extends FloatView {

    private final Context mContext;

    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private int mX, mY;
    private boolean isRemove = false;
    private PermissionListener mPermissionListener;

    FloatPhone(Context applicationContext, PermissionListener permissionListener) {
        mContext = applicationContext;
        mPermissionListener = permissionListener;
        mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.windowAnimations = 0;
    }

    @Override
    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    @Override
    public void setView(View view) {
        mView = view;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = mX = xOffset;
        mLayoutParams.y = mY = yOffset;
    }


    @Override
    public void init() {
        if (SdkVersion.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            req();
        } else if (Miui.rom()) {
            if (SdkVersion.SDK_INT >= Build.VERSION_CODES.M) {
                req();
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                Miui.req(mContext, new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        mWindowManager.addView(mView, mLayoutParams);
                        if (mPermissionListener != null) {
                            mPermissionListener.onSuccess();
                        }
                    }

                    @Override
                    public void onFail() {
                        if (mPermissionListener != null) {
                            mPermissionListener.onFail();
                        }
                    }
                });
            }
        } else {
            req();
        }
    }

    private void req() {
        if (SdkVersion.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        FloatActivity.request(mContext, new PermissionListener() {
            @Override
            public void onSuccess() {
                mWindowManager.addView(mView, mLayoutParams);
                if (mPermissionListener != null) {
                    mPermissionListener.onSuccess();
                }
            }

            @Override
            public void onFail() {
                if (mPermissionListener != null) {
                    mPermissionListener.onFail();
                }
            }
        });
    }

    @Override
    public void dismiss() {
        isRemove = true;
        try {
            mWindowManager.removeView(mView);
        } catch (Exception e) {
        }
    }

    @Override
    public void updateXY(int x, int y) {
        if (isRemove) return;
        mLayoutParams.x = mX = x;
        mLayoutParams.y = mY = y;
        try {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
        }
    }

    @Override
    public void updateX(int x) {
        if (isRemove) return;
        mLayoutParams.x = mX = x;
        try {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
        }
    }

    @Override
    public void updateY(int y) {
        if (isRemove) return;
        mLayoutParams.y = mY = y;
        try {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
        }
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }

    @Override
    public void addWindowFlag(int flag) {
        if (isRemove) return;
        mLayoutParams.flags = mLayoutParams.flags | flag;
        try {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
        }

    }

    @Override
    public void removeWindowFlag(int flag) {
        if (isRemove) return;
        try {
            mLayoutParams.flags = mLayoutParams.flags & (~flag);
        } catch (Exception e) {
        }
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    @Override
    public void updateLayoutParams() {
        if (isRemove) {
            return;
        }
        try {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
        }
    }


}
