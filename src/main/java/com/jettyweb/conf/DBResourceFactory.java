package com.jettyweb.conf;

public interface DBResourceFactory {

	DBResource create(String dbName) throws Exception;
}
