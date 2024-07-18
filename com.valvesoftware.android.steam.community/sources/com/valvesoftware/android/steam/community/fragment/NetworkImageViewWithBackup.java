package com.valvesoftware.android.steam.community.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import java.util.HashSet;

/* loaded from: classes.dex */
public class NetworkImageViewWithBackup extends ImageView {
    private static final HashSet<String> badImageUrls = new HashSet<>();
    private String mBackupUrl;
    private int mDefaultImageId;
    private int mErrorImageId;
    private ImageLoader.ImageContainer mImageContainer;
    private ImageLoader mImageLoader;
    private String mUrl;

    public NetworkImageViewWithBackup(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NetworkImageViewWithBackup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setImageUrl(String str, String str2, ImageLoader imageLoader) {
        this.mBackupUrl = str2;
        this.mUrl = str;
        this.mImageLoader = imageLoader;
        loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int i) {
        this.mDefaultImageId = i;
    }

    public void setErrorImageResId(int i) {
        this.mErrorImageId = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadImageIfNecessary(boolean z) {
        int width = getWidth();
        int height = getHeight();
        String str = this.mUrl;
        if (badImageUrls.contains(str)) {
            str = !badImageUrls.contains(this.mBackupUrl) ? this.mBackupUrl : null;
        }
        if (width == 0 && height == 0) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            ImageLoader.ImageContainer imageContainer = this.mImageContainer;
            if (imageContainer != null) {
                imageContainer.cancelRequest();
                this.mImageContainer = null;
            }
            setImageBitmap(null);
            return;
        }
        ImageLoader.ImageContainer imageContainer2 = this.mImageContainer;
        if (imageContainer2 != null && imageContainer2.getRequestUrl() != null) {
            if (this.mImageContainer.getRequestUrl().equals(str)) {
                return;
            }
            this.mImageContainer.cancelRequest();
            setImageBitmap(null);
        }
        this.mImageContainer = this.mImageLoader.get(str, new C02191(str, z));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.valvesoftware.android.steam.community.fragment.NetworkImageViewWithBackup$1 */
    /* loaded from: classes.dex */
    public class C02191 implements ImageLoader.ImageListener {
        final /* synthetic */ String val$currentUrl;
        final /* synthetic */ boolean val$isInLayoutPass;

        C02191(String str, boolean z) {
            this.val$currentUrl = str;
            this.val$isInLayoutPass = z;
        }

        @Override // com.android.volley.Response.ErrorListener
        public void onErrorResponse(VolleyError volleyError) {
            NetworkImageViewWithBackup.badImageUrls.add(this.val$currentUrl);
            if (!this.val$currentUrl.equals(NetworkImageViewWithBackup.this.mUrl)) {
                if (NetworkImageViewWithBackup.this.mErrorImageId != 0) {
                    NetworkImageViewWithBackup networkImageViewWithBackup = NetworkImageViewWithBackup.this;
                    networkImageViewWithBackup.setImageResource(networkImageViewWithBackup.mErrorImageId);
                    return;
                }
                return;
            }
            NetworkImageViewWithBackup.this.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.NetworkImageViewWithBackup.1.1
                @Override // java.lang.Runnable
                public void run() {
                    NetworkImageViewWithBackup.this.loadImageIfNecessary(C02191.this.val$isInLayoutPass);
                }
            });
        }

        @Override // com.android.volley.toolbox.ImageLoader.ImageListener
        public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean z) {
            if (z && this.val$isInLayoutPass) {
                NetworkImageViewWithBackup.this.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.NetworkImageViewWithBackup.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        C02191.this.onResponse(imageContainer, false);
                    }
                });
                return;
            }
            if (imageContainer.getBitmap() == null) {
                if (NetworkImageViewWithBackup.this.mDefaultImageId != 0) {
                    NetworkImageViewWithBackup networkImageViewWithBackup = NetworkImageViewWithBackup.this;
                    networkImageViewWithBackup.setImageResource(networkImageViewWithBackup.mDefaultImageId);
                    return;
                }
                return;
            }
            NetworkImageViewWithBackup.this.setImageBitmap(imageContainer.getBitmap());
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        loadImageIfNecessary(true);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        ImageLoader.ImageContainer imageContainer = this.mImageContainer;
        if (imageContainer != null) {
            imageContainer.cancelRequest();
            setImageBitmap(null);
            this.mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
