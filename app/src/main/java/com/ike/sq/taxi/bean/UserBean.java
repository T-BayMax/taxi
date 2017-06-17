package com.ike.sq.taxi.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public class UserBean implements Parcelable {

    private int status;
    private String userId;
    private String numberId;
    private String nickname;
    private String token;
    private String userPortraitUrl;
    private int sex;
    private String mobile;
    private String address;
    private String birthday;
    private String email;
    private int age;
    private String experience;
    private String creditScore;
    private String contributionScore;
    private String recommendUserId;
    private String claimUserId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(numberId);
        dest.writeString(nickname);
        dest.writeString(token);
        dest.writeString(userPortraitUrl);
        dest.writeInt(sex);
        dest.writeString(mobile);
        dest.writeString(address);
        dest.writeString(birthday);
        dest.writeString(email);
        dest.writeInt(age);
        dest.writeString(experience);
        dest.writeString(creditScore);
        dest.writeString(contributionScore);
        dest.writeString(recommendUserId);
        dest.writeString(claimUserId);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        status = in.readInt();
        userId = in.readString();
        numberId = in.readString();
        nickname = in.readString();
        token = in.readString();
        userPortraitUrl = in.readString();
        sex = in.readInt();
        mobile = in.readString();
        address = in.readString();
        birthday = in.readString();
        email = in.readString();
        age = in.readInt();
        experience = in.readString();
        creditScore = in.readString();
        contributionScore = in.readString();
        recommendUserId = in.readString();
        claimUserId = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNumberId() {
        return numberId;
    }

    public void setNumberId(String numberId) {
        this.numberId = numberId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserPortraitUrl() {
        return userPortraitUrl;
    }

    public void setUserPortraitUrl(String userPortraitUrl) {
        this.userPortraitUrl = userPortraitUrl;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(String creditScore) {
        this.creditScore = creditScore;
    }

    public String getContributionScore() {
        return contributionScore;
    }

    public void setContributionScore(String contributionScore) {
        this.contributionScore = contributionScore;
    }

    public String getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(String recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public String getClaimUserId() {
        return claimUserId;
    }

    public void setClaimUserId(String claimUserId) {
        this.claimUserId = claimUserId;
    }

    public static Creator<UserBean> getCREATOR() {
        return CREATOR;
    }
}
