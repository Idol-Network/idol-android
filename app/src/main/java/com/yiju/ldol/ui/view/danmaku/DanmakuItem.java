package com.yiju.ldol.ui.view.danmaku;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessageExtension;
import com.yiju.idol.R;
import com.yiju.idol.base.App;
import com.yiju.idol.utils.DensityUtil;

import java.util.Map;

/**
 * Created by Allan_Zhang on 2016/12/5.
 */

public class DanmakuItem extends HorizontalScrollView {

    private Context mContext;
    private SimpleDraweeView mSdvLevel;
    private SimpleDraweeView mSdvHead;
    private TextView mTvMsg;
    private TextView mTvNick;
    private boolean isAnimating;
    private boolean canPlayNext;//可以播放下一条弹幕，不会与该弹幕重叠
    private LinearInterpolator interpolator = new LinearInterpolator();
    private Paint mPaint;

    public DanmakuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        canPlayNext = true;
        mPaint = new Paint();
        init();
    }

    private void init() {
        View v = inflate(mContext, R.layout.item_danmaku, null);
        mSdvHead = (SimpleDraweeView) v.findViewById(R.id.sdv_head_danmaku);
        mTvMsg = (TextView) v.findViewById(R.id.tv_msg_danmaku);
        mSdvLevel = (SimpleDraweeView) v.findViewById(R.id.sdv_level_danmaku);
        mTvNick = (TextView) v.findViewById(R.id.tv_nick_danmaku);
        addView(v);
        //重新设置布局
        v.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
    }

    public void setData(ChatRoomMessage message) {
        //获取消息扩展字段
        Map<String, Object> remoteExtension = message.getRemoteExtension();
        String prefixPicUri = null;
        if (remoteExtension != null) {
            String prefixPic = (String) remoteExtension.get("prefix_pic");
            if (!TextUtils.isEmpty(prefixPic)) {
                prefixPicUri = getPrefixPicUri(prefixPic);
            }
        }
        ChatRoomMessageExtension extension = message.getChatRoomMessageExtension();
        String nickName;
        String avatar;
        if (extension != null) {
            nickName = extension.getSenderNick();
            avatar = extension.getSenderAvatar();
        } else {
            //自己发送的消息
            nickName = getNickName();
            avatar = getAvatar();
        }
        if (nickName != null) {
            mTvNick.setText(nickName);
        }
        mTvMsg.setText(message.getContent());
        //设置布局的宽度
        mPaint.setTextSize(mTvMsg.getTextSize());
        float textWidth = mPaint.measureText(message.getContent());
        float extraWidth = DensityUtil.dip2px(83);//除去文字部分的长度
        mPaint.setTextSize(mTvNick.getTextSize());
        float textWidth2 = 0;
        if (nickName != null) {
            textWidth2 = mPaint.measureText(nickName);
        }
        int finalWidth = (int) (Math.max(textWidth, textWidth2) + extraWidth);//最终长度
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.width = finalWidth;
        setLayoutParams(layoutParams);

        if (prefixPicUri != null) {
            mSdvLevel.setImageURI(prefixPicUri);
        }
        if (avatar != null) {
            mSdvHead.setImageURI(avatar);
        }
        this.setTag(message);//存储UserId
        playAnim(finalWidth);
    }

    /**
     * 拼接图片uri
     *
     * @param tag
     * @return
     */
    private String getPrefixPicUri(String tag) {
        return "http://pic1.grtstar.cn/image/static/" + tag + "_3x.png";
    }

    private String getNickName() {
        return App.getApp().getNickName();
    }

    private String getAvatar() {
        return App.getApp().getUser().avatar;
    }

    /**
     * 需要把之前计算的长度传过来 因为第一次播放动画是getMeasuredWidth()长度不正确
     *
     * @param finalWidth
     */
    public void playAnim(int finalWidth) {
        float screenWidth = DensityUtil.getScreenWidth();
        long leftDuration = 5000;//跑一屏的时间是5秒
        long firstDuration = (long) (finalWidth / screenWidth * leftDuration);//计算弹幕尾部出现的时间

        ViewAnimator.animate(this)
                .translationX(finalWidth, 0f)
                .duration(firstDuration)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        isAnimating = true;
                        canPlayNext = false;
                        DanmakuItem.this.setVisibility(VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        //设置间隔，避免重叠
                        canPlayNext = true;
                        if (listener != null) {
                            //可以播放下一个动画的回调
                            listener.onCanPlayNext();
                        }
                    }
                })
                .interpolator(interpolator)
                .thenAnimate(this)
                .translationX(0f, -screenWidth)
                .duration(leftDuration)
                .interpolator(interpolator)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        isAnimating = false;
                        DanmakuItem.this.setVisibility(INVISIBLE);
                        DanmakuItem.this.setTag(null);
                        if (listener != null) {
                            //可以播放下一个动画的回调
                            listener.onAnimEnd();
                        }
                    }
                })
                .start();
    }

    private OnAnimCanPlayListener listener;

    public void setOnAnimCanPlayListener(OnAnimCanPlayListener listener) {
        this.listener = listener;
    }

    public interface OnAnimCanPlayListener {
        void onCanPlayNext();

        void onAnimEnd();
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public boolean isCanPlayNext() {
        return canPlayNext;
    }

}
