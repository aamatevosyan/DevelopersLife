package edu.fintech.developerslife.core;

import com.bumptech.glide.load.model.GlideUrl;

import java.net.URL;

public class CustomGlideUrl extends GlideUrl {
    private String id;
    public CustomGlideUrl(URL url, String id) {
        super(url);
        this.id = id;
    }

    @Override
    public String getCacheKey() {
        return id;
    }
}
