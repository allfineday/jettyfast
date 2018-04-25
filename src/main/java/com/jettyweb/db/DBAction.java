package com.jettyweb.db;

import java.io.IOException;

public interface DBAction {
	void commit() throws IOException;

	void rollback() throws IOException;
}
