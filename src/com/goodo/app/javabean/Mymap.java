package com.goodo.app.javabean;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class Mymap implements Parcelable{
	public HashMap<Integer, SendAttachObject> map = new HashMap<Integer, SendAttachObject>();

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeMap(map);
	}
	
	

}
