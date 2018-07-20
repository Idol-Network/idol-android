package com.yiju.ldol.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.netease.nim.uikit.bean.GiftMsgAttachment;
import com.yiju.idol.R;
import com.yiju.idol.nim.GiftMsgUtil;

/**
 * Created by Allan_Zhang on 2016/5/30.
 */
public class GiftView extends RelativeLayout {

    private SimpleDraweeView mIvGift;
    private ImageView mBgGift;
    //    private ImageView mIvGiftBig;
    private TextView mTvGift;
    private ImageView ivBgAnim;//背景动画
    private GiftMsgAttachment attachment;//当前礼物
    private GiftMsgAttachment lastAttachment;//上一次的礼物

    private View mNumView;
    private OnAnimListener listener;
    private boolean isAnimating;
    private TextView mTvNick;
    private SimpleDraweeView sdvHead;
    private OnHeadTouchListener headTouchListener;
    //    private FramesSequenceAnimation framesSequenceAnimation;
    private Context mContext;
    private int lastNum;//用于记录上一次礼物数量，当礼物数出现1时，lastNum重置，否则一直累加
    private boolean isPaused;//开始播放大礼物时，将值置为true，连送礼物不继续执行并不删除attachment和lastattachment，等待重新调用startAnim()
    private int level;//当前礼物数量是520还是1314

    public GiftView(Context context) {
        super(context);
        mContext = context;
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        View v = inflate(context, R.layout.gift_view, null);
        mNumView = v.findViewById(R.id.layout_num_view);
        ivBgAnim = (ImageView) v.findViewById(R.id.bg_anim);
        mBgGift = (ImageView) v.findViewById(R.id.bg_gift);
        mIvGift = (SimpleDraweeView) v.findViewById(R.id.iv_gift);
//        mIvGiftBig = (ImageView) v.findViewById(R.id.gift_big);
        mTvNick = (TextView) v.findViewById(R.id.tv_user_nick);
        mTvGift = (TextView) v.findViewById(R.id.tv_gift_num);
        sdvHead = (SimpleDraweeView) v.findViewById(R.id.iv_head);
        sdvHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachment != null && headTouchListener != null) {
                    headTouchListener.onHeadTouch(Integer.valueOf(attachment.getUserId()));
                }
            }
        });
        addView(v);
        //重新设置布局
        v.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    /**
     * 头像点击的监听
     */
    public interface OnHeadTouchListener {
        void onHeadTouch(int userId);
    }

    public void setOnHeadTouchListener(OnHeadTouchListener listener) {
        headTouchListener = listener;
    }

    public GiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnim() {
        isPaused = false;
        int duration = 250;
//        int duration = 300;
        isAnimating = true;
        initViewState(attachment);
        ViewAnimator.animate(this)
                .alpha(0.2f, 1f)
                .translationX(-200f, 0f)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        setVisibility(VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        playGiftAnim();
                    }
                })
                .duration(duration)
                .start();

    }

    private void endingAnim() {
        ViewAnimator.animate(this)
                .alpha(1f, 0f)
                .translationX(0f, -200f)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        mNumView.setVisibility(INVISIBLE);
                        mIvGift.setVisibility(GONE);
                        setVisibility(INVISIBLE);
                        if (isPaused) {
                            //若为暂停状态，则不重置
                            return;
                        }
                        if (attachment != null) {
                            startAnim();
                            return;
                        }
                        listener.onAnimEnd();
                        if (attachment != null) {
                            startAnim();
                        } else if (listener != null) {
                            attachment = null;
                            isAnimating = false;
                        }
                    }
                })
                .startDelay(200)
                .duration(600)
                .start();
    }

    private void playGiftAnim() {
        int duration = 100;
//        int duration = 300;
        ViewAnimator.animate(mIvGift)
                .alpha(0.2f, 1f)
                .translationX(-80f, 0f)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        mIvGift.setVisibility(VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        playTextAnim();
                    }
                })
                .duration(duration)
                .start();
    }

    private void playTextAnim() {
        AnimationDrawable drawable = (AnimationDrawable) ivBgAnim.getBackground();
        if (drawable != null) {
            drawable.stop();
            drawable.start();
        }
        ViewAnimator.animate(mNumView)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        mNumView.setVisibility(VISIBLE);
                    }
                })
                .scale(1.0f, 3.0f)
                .duration(50)
                .thenAnimate(mNumView)
                .duration(100)
                .scale(3.0f, 1.5f)
                .thenAnimate(mNumView)
                .scale(1.5f, 1.5f)
                .duration(200)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        if (listener != null) {
                            if (attachment.getGiftAmount() > 0) {
                                if (isPaused) {//来大礼物，暂停连送
                                    endingAnim();
                                    return;
                                }
                                //若连送的礼物数还没扣除完毕，则继续播放
                                initViewState(attachment);
                                playTextAnim();
                                return;
                            } else if (lastAttachment == null) {
                                listener.onTextAnimFinished();
                            }
                        }
                        if (attachment != null && lastAttachment != null) {
//                            if ((attachment == lastAttachment) || (lastAttachment.getUserId()
//                                    .equals(attachment.getUserId()) && lastAttachment.getGiftType() ==
//                                    attachment.getGiftType() && lastAttachment.getGiftNum() < attachment
//                                    .getGiftNum())) {
                            if (lastAttachment.getUserId()
                                    .equals(attachment.getUserId()) && lastAttachment.getGiftType() ==
                                    attachment.getGiftType() && (lastAttachment.getGiftNum() < attachment
                                    .getGiftNum() || attachment.getViewNum() > 0)) {
                                //同一人连续送礼
                                lastAttachment = null;
                                initViewState(attachment);
                                playTextAnim();
                            } else {
                                //同一人不连续送礼，或不同的人送礼
                                lastAttachment = null;
                                lastNum = 0;//礼物数置0
                                endingAnim();
                            }
                        } else {
                            attachment = null;
                            lastAttachment = null;
                            endingAnim();
                        }
                    }
                })
                .start();
    }

    public void setData(GiftMsgAttachment attachment) {
        this.attachment = attachment;
    }

    private void initViewState(GiftMsgAttachment attachment) {
        if (attachment != null) {
            if (attachment.getViewNum() > 0) {//一次性送礼
                //判断是否为一次性连送
                int giftAmount = attachment.getGiftAmount();
                int viewNum = attachment.getViewNum();
                int df = giftAmount - 10;
                int nextGiftAmont;
                if (giftAmount == viewNum || df < 0) {//attachment第一次用于显示或giftAmount不足10
                    //大于10时，显示的值一次+10
                    nextGiftAmont = giftAmount - 1;
                } else {
                    nextGiftAmont = giftAmount - 10;
                }
                if (giftAmount != viewNum) {
                    //判断是否为第一次播放连送的消息 若为连送的第一条消息，giftnum数不-1
                    int nextGiftNum = attachment.getGiftNum();
                    nextGiftNum += df < 0 ? 1 : 10;
                    attachment.setGiftNum(nextGiftNum);
                }
                attachment.setGiftAmount(nextGiftAmont);//设置amount给GiftMsgAttachment
            }
            //*****************礼物数改为每次自加1*********************
            int num = attachment.getGiftNum();
            if (num == 1) {
                lastNum = 0;
            }
            lastNum++;
//            int animTimes = lastNum;
            int animTimes = num;
            //*******************************************************
//            int animTimes = attachment.getGiftNum();
            GiftMsgUtil giftMsgUtil = GiftMsgUtil.getInstance();
            int resIndex = giftMsgUtil.getResIndex(attachment.getGiftType(), animTimes);
            int bgRes;
            int textColor;
            int animRes;
            switch (resIndex) {
                case 0:
                    animRes = R.drawable.anim_gift_bg_yellow;
                    textColor = ContextCompat.getColor(getContext(), R.color.gift_color_yellow);
                    bgRes = R.drawable.liwutiao_heise;
                    break;
                case 1:
                    animRes = R.drawable.anim_gift_bg_blue;
                    textColor = ContextCompat.getColor(getContext(), R.color.gift_color_blue);
                    bgRes = R.drawable.liwutiao_lanse;
                    break;
                case 2:
                    animRes = R.drawable.anim_gift_bg_purple;
                    textColor = ContextCompat.getColor(getContext(), R.color.gift_color_red);
                    bgRes = R.drawable.liwutiao_zise;
                    break;
                case 3:
                    animRes = R.drawable.anim_gift_bg_red;
                    textColor = ContextCompat.getColor(getContext(), R.color.gift_color_orange);
                    bgRes = R.drawable.liwutiao_hongse;
                    break;
                default:
                    animRes = 0;
                    textColor = ContextCompat.getColor(getContext(), R.color.gift_color_yellow);
                    bgRes = R.drawable.liwutiao_heise;
                    break;
            }
            ivBgAnim.setBackgroundResource(animRes);
            mBgGift.setImageResource(bgRes);//设置背景
            mTvGift.setText("x" + animTimes);
            mTvGift.setTextColor(textColor);
            int giftType = attachment.getGiftType();
            String nick = attachment.getNickname();
            String head = attachment.getHeadUrl();
            mTvNick.setText(nick != null ? nick : "");
            if (!TextUtils.isEmpty(head)) {//若为空设置头像会闪烁
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(head)
                        .setOldController(sdvHead.getController())
                        .build();
                sdvHead.setController(controller);//头像
            }
//            if (giftType == 5) {
//                //TODO 调整文字位置
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mNumView.getLayoutParams();
//                layoutParams.leftMargin = DensityUtil.dip2px(mContext, 230);
//                mNumView.setLayoutParams(layoutParams);
//            }
//            mIvGiftBig.setVisibility(INVISIBLE);
            mIvGift.setImageURI(GiftMsgUtil.getInstance().getIcon3Url(giftType));

            /********************触发全屏特效相关**********************/
            //达到1314触发一次特效，animTimes < 520时，将level置0
            if (animTimes < 1314) {
                level = 0;
            }
            if (level == 0 && listener != null) {
                boolean b = giftMsgUtil.hasBigAnimation(giftType);
                if (b && animTimes >= 1314) {
                    level = 1314;
                    listener.onReachLevel(level, giftType);
                }
            }
        }
    }

    /**
     * 更新数据
     *
     * @param attachment
     */
    public void updateData(GiftMsgAttachment attachment) {
        lastAttachment = this.attachment;
        this.attachment = attachment;
    }

    /**
     * 动画状态
     *
     * @return
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    public void onPause() {
        //只有连送动画没播放完才置为暂停状态
        if (attachment != null && attachment.getGiftAmount() > 0) {
            isPaused = true;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public GiftMsgAttachment getAttachment() {
        return attachment;
    }

    public void setOnAnimFinishListener(OnAnimListener listener) {
        this.listener = listener;
    }

    public interface OnAnimListener {
        /**
         * 文字动画消失,此时可传入同一id的礼物
         */
        void onTextAnimFinished();

        /**
         * 动画视图开始消失，此时可传入礼物
         */
        void onAnimEnd();

        /**
         * 礼物数量达到520或1314
         */
        void onReachLevel(int level, int giftType);
    }
}
