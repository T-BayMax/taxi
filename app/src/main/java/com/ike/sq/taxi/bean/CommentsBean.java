package com.ike.sq.taxi.bean;

import java.io.Serializable;

/**
 * 评论内容
 * Created by T-BayMax on 2017/4/7.
 */

public class CommentsBean implements Serializable{

    private String articleId;
    private String id;             //评论id
    private String  userId;          //评论用户id
    private String nickname;       //评论用户昵称
    private String userPortraitUrl;   //头像
    private String content;         //评论内容
    private String  commentTime;    //评论时间  Y-m-d  H:i:s
    private int likes;           //评论点赞数量
    private int likesStatus;  //1当前用户已点赞  0未点赞

    private String userName;        //用户姓名
    private int starNumber;      //评星数量


    private String fromUserId;      //被回复用户id
    private String fromNickname;   //被回复用户昵称
    private String fromPortraitUrl;     //被回复头像
    private String fromContent;      //被回复的内容

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserPortraitUrl() {
        return userPortraitUrl;
    }

    public void setUserPortraitUrl(String userPortraitUrl) {
        this.userPortraitUrl = userPortraitUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getLikesStatus() {
        return likesStatus;
    }

    public void setLikesStatus(int likesStatus) {
        this.likesStatus = likesStatus;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromNickname() {
        return fromNickname;
    }

    public void setFromNickname(String fromNickname) {
        this.fromNickname = fromNickname;
    }

    public String getFromPortraitUrl() {
        return fromPortraitUrl;
    }

    public void setFromPortraitUrl(String fromPortraitUrl) {
        this.fromPortraitUrl = fromPortraitUrl;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStarNumber() {
        return starNumber;
    }

    public void setStarNumber(int starNumber) {
        this.starNumber = starNumber;
    }
}
