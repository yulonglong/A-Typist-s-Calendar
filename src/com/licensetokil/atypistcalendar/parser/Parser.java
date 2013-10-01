package com.licensetokil.atypistcalendar.parser;

import java.util.Calendar;
import java.util.StringTokenizer;

public class Parser {;

	private static String ADD = "add";
	private static String DELETE = "delete";
	private static String DISPLAY = "display";
	private static String UPDATE = "update";
	private static String SEARCH = "search";
	private static String MARK = "mark";
	private static String EXIT = "exit";
	private static String GCAL = "gcal";
	private static String GCAL_SYNC = "gcal sync";
	private static String GCAL_QUICK_ADD = "gcal quick add";
	
	private static ACTION_TYPE determineActionType(String commandTypeString) {
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");
		if (commandTypeString.equalsIgnoreCase(ADD)) {
			return ACTION_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase(DELETE)) {
			return ACTION_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase(DISPLAY)) {
			return ACTION_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase(UPDATE)) {
			return ACTION_TYPE.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase(SEARCH)) {
			return ACTION_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase(MARK)) {
			return ACTION_TYPE.MARK;
		} else if (commandTypeString.equalsIgnoreCase(EXIT)) {
			return ACTION_TYPE.EXIT;
		} else if (commandTypeString.equalsIgnoreCase(GCAL)) {
			return ACTION_TYPE.GCAL;
		} else if (commandTypeString.equalsIgnoreCase(GCAL_SYNC)) {
			return ACTION_TYPE.GCAL_SYNC;
		} else if (commandTypeString.equalsIgnoreCase(GCAL_QUICK_ADD)) {
			return ACTION_TYPE.GCAL_QUICK_ADD;
		} else {
			return ACTION_TYPE.INVALID;
		}
	}
	
	
	public static abstract class Action{
		ACTION_TYPE type;
	
		abstract ACTION_TYPE getType();
		abstract void setType (ACTION_TYPE newActionType);
	}
	
	public static class LocalAction extends Action{
		private ACTION_TYPE type;
		private Calendar startTime;
		private Calendar endTime;
		private String description;
		private String place;
	
		public LocalAction(){
			type = null;
			startTime = Calendar.getInstance();
			endTime = Calendar.getInstance();
			description = new String();
			place = new String();
		}
	
		public ACTION_TYPE getType(){
			return type;
		}
	
		public Calendar getStartTime(){
			return startTime;
		}
	
		public Calendar getEndTime(){
			return endTime;
		}
	
		public String getDescription(){
			return description;
		}
	
		public String getPlace(){
			return place;
		}
	
		public void setType(ACTION_TYPE newActionType){
			type = newActionType;
		}
	
		public void setStartTime(Calendar newStartTime){
			startTime = newStartTime;
		}
	
		public void setEndTime(Calendar newEndTime){
			endTime = newEndTime;
		}
	
		public void setDescription(String newDescription){
			description = newDescription;
		}
	
		public void setPlace(String newPlace){
			place = newPlace;
		}
		
		public String toString(){
			return (type + "\n" +
				   startTime + "\n" +
			       endTime + "\n" +
			       description + "\n" +
			       place + "\n");
		}
	
	}
	
	public static class GoogleAction extends Action{
		private ACTION_TYPE type;
		private String userInput;
	
		public GoogleAction(){
			type = null;
			userInput = new String();
		}
	
		public ACTION_TYPE getType(){
			return type;
		}
	
		public String getUserInput(){
			return userInput;
		}
	
		public void setType(ACTION_TYPE newActionType){
			type = newActionType;
		}
	
		public void setUserInput(String newUserInput){
			userInput = newUserInput;
		}
	}
	
	private static String getRemainingTokens(StringTokenizer strRemaining){
		String strResult = new String();
		while (strRemaining.hasMoreTokens()) {
			strResult = strResult + " " +strRemaining.nextToken();
		}
		return strResult;
	}
	
	private static boolean addParser(StringTokenizer st, LocalAction userAction){

		userAction.setType(ACTION_TYPE.ADD);
		
		Calendar startTimeCal = Calendar.getInstance();
		Calendar endTimeCal = Calendar.getInstance();
		
		String description = new String(st.nextToken());
		
		String prep = new String (st.nextToken());
		String place = new String(st.nextToken());
		
		
		prep = new String (st.nextToken());
		String date = new String(st.nextToken());
		String strday = new String();
		strday = date.substring(0,2);
		int day = Integer.parseInt(strday);
		String strmonth = new String();
		strmonth = date.substring(3,5);
		int month = Integer.parseInt(strmonth);
		int year = 2013;
		
		prep = new String (st.nextToken());
		String startTime = new String(st.nextToken());
		startTime = startTime.substring(0,2);
		int intStartTime = Integer.parseInt(startTime);
		
		prep = new String (st.nextToken());
		String endTime = new String(st.nextToken());
		endTime = endTime.substring(0,2);
		int intEndTime = Integer.parseInt(endTime);
		
		startTimeCal.set(year, month, day, intStartTime, 0);
		endTimeCal.set(year, month, day, intEndTime, 0);
		
		
		userAction.setStartTime(startTimeCal);
		userAction.setEndTime(endTimeCal);
		userAction.setDescription(description);
		userAction.setPlace(place);
		
		return true;
	}
	LocalAction deleteParser(StringTokenizer st){
		LocalAction userAction = new LocalAction();
		return userAction;
	}
	LocalAction displayParser(StringTokenizer st){
		LocalAction userAction = new LocalAction();
		return userAction;
	}
	LocalAction updateParser(StringTokenizer st){
		LocalAction userAction = new LocalAction();
		return userAction;
	}
	LocalAction searchParser(StringTokenizer st){
		LocalAction userAction = new LocalAction();
		return userAction;
	}
	LocalAction markParser(StringTokenizer st){
		LocalAction userAction = new LocalAction();
		return userAction;
	}
	
	public static Action Parse(String userInput){
		StringTokenizer st = new StringTokenizer(userInput);
		String stringUserAction;
		stringUserAction = new String(st.nextToken());
	
		ACTION_TYPE actionType = determineActionType(stringUserAction);
	
		//if GCAL type
		if(actionType == ACTION_TYPE.GCAL){
			GoogleAction userAction = new GoogleAction();
			stringUserAction = stringUserAction + " " + st.nextToken();
			actionType = determineActionType(stringUserAction);
	
			if(actionType == ACTION_TYPE.GCAL_SYNC){
				userInput = new String(getRemainingTokens(st));
				userAction.setUserInput(userInput);
				return userAction;
			}
			else{
				stringUserAction = stringUserAction + " " + st.nextToken();
				actionType = determineActionType(stringUserAction);
	
				if(actionType == ACTION_TYPE.GCAL_QUICK_ADD){
					userAction.setType(ACTION_TYPE.GCAL_QUICK_ADD);
					userInput = new String(getRemainingTokens(st));
					userAction.setUserInput(userInput);
					return userAction;
				}
				else{
					System.out.println("Error! Invalid GCAL Command!");
					userAction.setType(ACTION_TYPE.INVALID);
					return userAction;
				}
	
			}
	
		}
		//if not GCAL type (means Local type)
		else{
			LocalAction userAction = new LocalAction();
			switch(actionType){
			case ADD:
				addParser(st,userAction);
				break;
			case DELETE:
				//userAction = deleteParser(st);
				break;
			case DISPLAY:
				//userAction = displayParser(st);
				break;
			case UPDATE:
				//userAction = updateParser(st);
				break;
			case SEARCH:
				//userAction = searchParser(st);
				break;
			case MARK:
				//userAction = markParser(st);
				break;
			case EXIT:
				userAction.setType(ACTION_TYPE.EXIT);
				break;
			default:
				userAction.setType(ACTION_TYPE.INVALID);
				break;
			} 
			return userAction;
		}
	}


}
