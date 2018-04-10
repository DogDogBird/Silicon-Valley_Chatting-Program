package com.example.user.chatmessaging;

import java.util.List;
import java.util.Stack;

enum Type
{
    OFFLINE, ONLINE, BUSY
}

public class User {
    private String userID;
    private String userPassword;
    private String userName;
    private Stack <String> messageList;
    private List<User> friendList;
    private Type type;

    public String get_userID()
    {
        return userID;
    }
    public String get_userPassword()
    {
        return userPassword;
    }
    public String get_userName()
    {
        return userName;
    }
    public Stack<String> get_messageList()
    {
        return messageList;
    }
    public List<User> get_friendList()
    {
        return friendList;
    }
    public Type get_type()
    {
        return type;
    }
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    public void setUserPassword(String userPassword)
    {
        this.userID = userPassword;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public void setMessageList(Stack<String> messageList)
    {
        this.messageList = messageList;
    }
    public void setFriendList(List<User> friendList)
    {
        this.friendList = friendList;
    }
    public void setType(Type type)
    {
        this.type = type;
    }
}
