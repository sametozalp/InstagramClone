package com.ozalp.instagram;

public class Post {
    String email;
    String comment;
    String downloadUri;
    String username;
    String date;
    String profilePhoto;

    public Post (String email, String comment, String downloadUri, String username, String date, String profilePhoto){
        this.email = email;
        this.comment = comment;
        this.downloadUri = downloadUri;
        this.username = username;
        this.date = date;
        this.profilePhoto = profilePhoto;

    }
}
