package com.licensetokil.atypistcalendar.parser;

public class GoogleAction extends Action{
	private GoogleActionType type;
	private String userInput;

	public GoogleAction(){
		type = null;
		userInput = new String();
	}

	public GoogleActionType getType(){
		return type;
	}

	public String getUserInput(){
		return userInput;
	}

	public void setType(GoogleActionType newActionType){
		type = newActionType;
	}

	public void setUserInput(String newUserInput){
		userInput = newUserInput;
	}
}
