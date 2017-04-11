package com.goodo.app.javabean;

public class ScheduleObject {
	private String ID;
	private String Date;
	private String IsAllDay;
	private String BeginTime;
	private String EndTie;
	private String Work;
	private String Type;
	
	public void setID(String ID){
		this.ID = ID;
	}
	public String getID(){
		return ID;
	}
	
	public void setDate(String Date){
		this.Date = Date;
	}
	public String getDate(){
		return Date;
	}
	
	public void setIsAllDay(String IsAllDay){
		this.IsAllDay = IsAllDay;
	}
	public String getIsAllDay(){
		return IsAllDay;
	}
	
	public void setBeginTime(String BeginTime){
		this.BeginTime = BeginTime;
	}
	public String getBeginTime(){
		return BeginTime;
	}
	
	public void setEndTime(String EndTime){
		this.EndTie = EndTime;
	}
	public String getEndTime(){
		return EndTie;
	}
	
	public void setWork(String Work){
		this.Work = Work;
	}
	public String getWork(){
		return Work;
	}
	
	public void setType(String Type){
		this.Type = Type;
	}
	public String getType(){
		return Type;
	}
}
