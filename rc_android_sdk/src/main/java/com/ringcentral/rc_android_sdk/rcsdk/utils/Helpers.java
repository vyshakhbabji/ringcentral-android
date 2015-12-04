/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ringcentral.rc_android_sdk.rcsdk.utils;

import com.ringcentral.rc_android_sdk.rcsdk.platform.Platform;
import com.squareup.okhttp.Callback;

/**
 * Created by vyshakh.babji on 12/4/15.
 */
public class Helpers {

    Platform platform;

    public Helpers(Platform platform) {
        this.platform = platform;
    }
    public void callLog(final Callback callback) {

        try {
            final String url = "/restapi/v1.0/account/~/call-log";
            platform.ensureAuthentication();
            platform.sendRequest("get", url, null, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
