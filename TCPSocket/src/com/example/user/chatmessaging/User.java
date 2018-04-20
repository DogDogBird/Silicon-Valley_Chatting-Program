package com.example.user.chatmessaging;


enum STATUS
{
    OFFLINE, ONLINE, BUSY
}

public class User {
	
	User()
	{
		ID ="";
		PW = "";
		Name = "";
	}
	
	private String ID;
	private String PW;
	private String Name;
	private String PartnerID;
	
	private STATUS status;
	
	public String get_ID()
	{
		return ID;
	}
	public String get_PW()
	{
		return PW;
	}
	public String get_Name()
	{
		return Name;
	}
	
	public STATUS get_status()
	{
		return status;
	}
	public String get_partnerID()
	{
		return PartnerID;
	}
	
	public void set_ID(String _ID)
	{
		ID = _ID;
	}
	public void set_PW(String _PW)
	{
		PW = _PW;
	}
	public void set_Name(String _Name)
	{
		Name = _Name;
	}
	public void set_PartnerID(String _PartnerID)
	{
		PartnerID = _PartnerID;
	}
	
	public void set_Status(STATUS stat)
	{
		status = stat;
	}
}
