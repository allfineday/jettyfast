package com.jettyweb.listener;


import com.jettyweb.db.event.DBOperate;
import com.jettyweb.util.GsonUtil;

import java.util.EventObject;

public class DBEvent extends EventObject {
	private static final long serialVersionUID = 5467587679L;

	/**
	 * 表名，非空
	 */
	private String type;

	/**
	 * 操作类型，非空
	 */
	private DBOperate operate;

	/**
	 * @param source
	 * @param type
	 *            表名，非空
	 * @param operate
	 *            操作，非空
	 */
	public DBEvent(Object source, String type, DBOperate operate) {
		super(GsonUtil.copyObject(source));
		this.type = type;
		this.operate = operate;
	}

	public String getType() {
		return type;
	}

	public DBOperate getOperate() {
		return operate;
	}

	@Override
	public String toString() {
		return "DBEvent [type=" + type + ", operate=" + operate + "]";
	}
}
