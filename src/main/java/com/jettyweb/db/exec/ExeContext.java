package com.jettyweb.db.exec;

import com.jettyweb.db.DBAction;

public class ExeContext {
	private Object result;
	Object param;

	DBAction action;

	public Object getParam() {
		return param;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public DBAction getAction() {
		return action;
	}

}
