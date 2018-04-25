package com.jettyweb.http.start;

import com.jettyweb.common.ServerStarter;
import com.jettyweb.common.StartContext;
import com.jettyweb.conf.AppInfo;
import com.jettyweb.http.UploadServer;
import com.jettyweb.http.WebServer;
import com.jettyweb.http.filter.HttpLoginWrapper;
import com.jettyweb.http.handler.*;
import com.jettyweb.log.Log;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


import java.util.concurrent.LinkedBlockingDeque;

public class HttpStarter implements ServerStarter {

	public void start(int port) throws Exception {
		initHandlers();
		QueuedThreadPool pool = new QueuedThreadPool(AppInfo.getInt("http.pool.maxThreads", 200),
				AppInfo.getInt("http.pool.minThreads", 8), AppInfo.getInt("http.pool.idleTimeout", 60000),
				new LinkedBlockingDeque<Runnable>(AppInfo.getInt("http.pool.queues", 1000)));
		Server server = new Server(pool);
		ServerConnector connector = new ServerConnector(server, null, null, null,
				AppInfo.getInt("http.connector.acceptors", 0), AppInfo.getInt("http.connector.selectors", 5),
				new HttpConnectionFactory());
		Log.get("HttpServer").info("listen port：" + port);
		String host = AppInfo.get("http.host");
		if (host != null && host.length() > 0) {
			connector.setHost(host);
		}
		connector.setPort(port);
		connector.setReuseAddress(true);

		server.setConnectors(new Connector[] { connector });
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath(AppInfo.get("http.web.root", "/intf"));
		context.addServlet(WebServer.class, "/webserver/*");
		context.addServlet(UploadServer.class, "/upload/*");
		Object path = StartContext.inst.map.get().get(StartContext.HTTP_LOGIN_PATH);
		if (path != null && String.class.isInstance(path)) {
			String loginPath = (String) path;
			if (!loginPath.startsWith("/")) {
				loginPath = "/" + loginPath;
			}
			Log.get("http").info("login path:{}", context.getContextPath() + loginPath);
			context.addServlet(HttpLoginWrapper.class, loginPath);
		}

		server.setHandler(context);
		server.start();
	}

	private void initHandlers() {
		ReqUserHandler userHandler = new ReqUserHandler();

		HttpHandlerChain chain = HttpHandlerChain.inst;
		chain.addHandler(new ReqHeaderHandler());
		chain.addHandler(new ReqBodyHandler());
		chain.addHandler(userHandler);
		chain.addHandler(new Base64DecodeHandler());
		chain.addHandler(new AesDecodeHandler());
		chain.addHandler(new ReqToStringHandler());
		chain.addHandler(new SignValidateHandler());
		chain.addHandler(new InvokeHandler());
		chain.addHandler(new RespToStringHandler());
		chain.addHandler(new ToByteHandler());
		chain.addHandler(new AesEncodeHandler());
		chain.addHandler(new Base64EncodeHandler());
		chain.addHandler(new RespHeaderHandler());
		chain.addHandler(new RespBodyHandler());

		chain = HttpHandlerChain.upload;
		chain.addHandler(new ReqHeaderHandler());
		chain.addHandler(new UploadHandler());
		chain.addHandler(userHandler);
		chain.addHandler(new Base64DecodeHandler());
		chain.addHandler(new AesDecodeHandler());
		chain.addHandler(new ReqToStringHandler());
		chain.addHandler(new SignValidateHandler());
		chain.addHandler(new InvokeHandler());
		chain.addHandler(new RespToStringHandler());
		chain.addHandler(new ToByteHandler());
		chain.addHandler(new AesEncodeHandler());
		chain.addHandler(new Base64EncodeHandler());
		chain.addHandler(new RespHeaderHandler());
		chain.addHandler(new RespBodyHandler());
	}
}
