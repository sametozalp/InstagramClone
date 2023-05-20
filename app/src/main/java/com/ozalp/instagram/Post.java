package com.ozalp.instagram;

import java.util.Date;

public class Post {
    String email;
    String comment;
    String downloadUri;
    String username;
    String date;

    public Post (String email, String comment, String downloadUri, String username, String date){
        this.email = email;
        this.comment = comment;
        this.downloadUri = downloadUri;
        this.username = username;
        this.date = date;

    }
}
