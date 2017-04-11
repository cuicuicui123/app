package utils;

public class DateTransform {
	public static String transform(String date){
		String[] DateAndTime = date.split(" ");
		String[] moreDate = DateAndTime[0].split("/");
		int month = Integer.parseInt(moreDate[1]);
		int day = Integer.parseInt(moreDate[2]);
		if(month<10){
			moreDate[1] = "0"+moreDate[1];
		}
		if(day<10){
			moreDate[2] = "0"+moreDate[2];
		}
		
		
		return moreDate[0]+"-"+moreDate[1]+"-"+moreDate[2];
	}
}
