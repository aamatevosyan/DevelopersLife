package edu.fintech.developerslife.core;

import androidx.annotation.NonNull;

public class Meme {
    public final String id;
    public final String description;
    public final String gifURL;

    public Meme(String id, String description, String gifURL) {
        this.id = id;
        this.description = description;
        this.gifURL = gifURL;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("id: %s, description: %s, gifURL: %s", id, description, gifURL);
    }
}
