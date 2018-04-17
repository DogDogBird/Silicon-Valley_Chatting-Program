package com.example.user.chatmessaging;
import java.io.Serializable;

public class ChattingMessage implements Serializable
{
    private String senderID;
    private String receiverID;
    private String msg;

    public void setSenderID(String _senderID)
    {
        senderID = _senderID;
    }
    public void setReceiverID(String _receiverID)
    {
        receiverID = _receiverID;
    }
    public void setMsg(String _msg)
    {
        msg = _msg;
    }
    public String getSenderID()
    {
        return senderID;
    }
    public String getReceiverID()
    {
        return receiverID;
    }
    public String getMsg()
    {
        return msg;
    }
}
