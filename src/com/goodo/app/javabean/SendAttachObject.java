package com.goodo.app.javabean;

import java.io.Serializable;

public class SendAttachObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String AttachName;
	private String AttachUrl;
	private String AttachID;
	private String AttachSize;
	
	public void setAttachName(String AttachName){
		this.AttachName = AttachName;
	}
	public String getAttachName(){
		return AttachName;
	}
	public void setAttachUrl(String AttachUrl){
		this.AttachUrl = AttachUrl;
	}
	public String getAttachUrl(){
		return AttachUrl;
	}
	public void setAttachID(String AttachID){
		this.AttachID = AttachID;
	}
	public String getAttachID(){
		return AttachID;
	}
	public void setAttachSize(String AttachSize){
		this.AttachSize = AttachSize;
	}
	public String getAttachSize(){
		return AttachSize;
	}
	
}
