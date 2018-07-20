package com.yiju.ldol.utils;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;
import com.yiju.idol.listener.TwitterLoginCallback;

import retrofit2.Call;

/**
 * Created by puya on 2016/12/28.
 */

public class TwitterPlaform {

    private Activity mContext;
    private TwitterAuthClient twitterAuthClient;

    public TwitterPlaform(Activity context) {
        mContext = context;
        twitterAuthClient = new TwitterAuthClient();
    }

    public void login(TwitterLoginCallback callback) {
        twitterAuthClient.authorize(mContext, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> session) {
                //登录成功
                if (session != null && session.data != null) {
//                    TwitterSession twitter = session.data;
//                    String authToken = String.valueOf(twitter.getAuthToken());
//                    long userId = twitter.getUserId();
//                    Log.d("tang", "authToken:" + authToken + "\nuserId:" + userId + "\nusername:" + twitter.getUserName());
                    TwitterApiClient client = new TwitterApiClient(session.data);
                    AccountService accountService = client.getAccountService();
                    Call<User> show = accountService.verifyCredentials(false, false, false);
                    show.enqueue(new Callback<User>() {
                        @Override
                        public void success(Result<User> result) {
                            if (callback != null && result != null && result.data != null) {
                                callback.Success(result.data, String.valueOf(session.data.getAuthToken()));
                            }
//                            if (result != null && result.data != null) {
//                                User user = result.data;
//                                Log.d("tang", "createdAt:" + user.createdAt);
//                                Log.d("tang", "description:" + user.description);
//                                Log.d("tang", "email:" + user.email);
//                                Log.d("tang", "idStr:" + user.idStr);
//                                Log.d("tang", "id:" + user.id);
//                                Log.d("tang", "lang:" + user.lang);
//                                Log.d("tang", "location:" + user.location);
//                                Log.d("tang", "name:" + user.name);
//                                Log.d("tang", "profileBackgroundImageUrlHttps:" + user.profileBackgroundImageUrlHttps);
//                                Log.d("tang", "profileBackgroundImageUrl:" + user.profileBackgroundImageUrl);
//                                Log.d("tang", "profileBannerUrl:" + user.profileBannerUrl);
//                                Log.d("tang", "profileImageUrl:" + user.profileImageUrl);
//                                Log.d("tang", "profileImageUrlHttps:" + user.profileImageUrlHttps);
//                                Log.d("tang", "profileLinkColor:" + user.profileLinkColor);
//                                Log.d("tang", "screenName:" + user.screenName);
//                                Log.d("tang", "timeZone:" + user.timeZone);
//                                Log.d("tang", "url:" + user.url);
//                                Log.d("tang", "withheldScope:" + user.withheldScope);
//                                Log.d("tang", "contributorsEnabled:" + user.contributorsEnabled);
//                                Log.d("tang", "favouritesCount:" + user.favouritesCount);
//                                Log.d("tang", "statusesCount:" + user.statusesCount);
//                                Log.d("tang", "verified:" + user.verified);
//                            }
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            if (callback != null) {
                                callback.onFailure(exception.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void failure(TwitterException e) {
                //授权失败
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public TwitterAuthClient getTwitterAuthClient() {
        return twitterAuthClient;
    }
}