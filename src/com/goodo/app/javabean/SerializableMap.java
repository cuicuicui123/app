package com.goodo.app.javabean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializableMap implements Serializable{
	private static final long serialVersionUID = -7060210544600464481L;
	private Map<Integer, Object> map;
	
	public Map<Integer, Object> getMap(){
		return map;
	}
	public void setMap(Map<Integer, Object> map){
		this.map = map;
	}
}
