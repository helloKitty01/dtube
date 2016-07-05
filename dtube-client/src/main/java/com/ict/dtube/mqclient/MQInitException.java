package com.ict.dtube.mqclient;

import java.io.IOException;

/**
 *@author sky
 */
public class MQInitException extends RuntimeException {

	public MQInitException(Exception e) {
		super(e);
	}
	
	public MQInitException(String message){
		super(message);
	}

}
