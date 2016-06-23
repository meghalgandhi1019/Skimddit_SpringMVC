package com.crunchify.controller;

public class UserLoginForm {

	public String username;
	public String passcode;
	public String cpasscode;
	
	public String getusername()
    {
        return username;
    }

    public void setusername(String username)
    {
        this.username = username;
    }

    public String getpasscode()
    {
    	return passcode;
    }
    
    public void setpasscode(String passcode)
    {
    	this.passcode=passcode;
    }
    
    public String getcpasscode()
    {
    	return cpasscode;
    }
    
    public void setcpasscode(String cpasscode)
    {
    	this.cpasscode=cpasscode;
    }
}
