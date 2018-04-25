package com.jettyweb.db.event;

public enum DBOperate {

	INSERT, UPDATE, PART_UPDATE, DELETE,

	OTHER_MODIFY,

	GET,

	LIST, COUNT;
	public boolean isModify(String oprater) {
		return this == INSERT || this == UPDATE || this == PART_UPDATE || this == DELETE || this == OTHER_MODIFY;
	}

}
