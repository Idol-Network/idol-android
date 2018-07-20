package com.yiju.ldol.listener;

import com.twitter.sdk.android.core.models.User;

public interface TwitterLoginCallback {
    void Success(User data, String authToken);

    void onFailure(String message);
}
