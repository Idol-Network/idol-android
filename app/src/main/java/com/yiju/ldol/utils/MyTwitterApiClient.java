package com.yiju.ldol.utils;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public CustomService getCustomService() {
        return getService(CustomService.class);
    }

    // example users/show service endpoint
    public interface CustomService {

        @GET("/1.1/users/show.json")
        Call<User> show(@Query("user_id") long id);
    }

    public interface LoginCallback {
        void onSuccess();

        void onFailure();
    }

    TwitterAuthClient mTwitterAuthClient;

    public void loginTwitter(Activity activity, final LoginCallback callback) {
        if (mTwitterAuthClient == null) {
            mTwitterAuthClient = new TwitterAuthClient();
        }
        mTwitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String name = result.data.getUserName();
                long userId = result.data.getUserId();
                getTwitterUserInfo(userId, callback);
            }

            @Override
            public void failure(TwitterException e) {
                callback.onFailure();
            }
        });
    }

    private void getTwitterUserInfo(final long userId, final LoginCallback callback) {
        final TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterApiClient client = new TwitterApiClient(activeSession);
        AccountService accountService = client.getAccountService();
        Call<User> show = accountService.verifyCredentials(false, false, false);
        show.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User data = result.data;
                String profileImageUrl = data.profileImageUrl.replace("_normal", "");
                String name = data.name;
                callback.onSuccess();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
                callback.onFailure();
            }
        });
    }
}
