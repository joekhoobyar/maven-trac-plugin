package com.googlecode.mojo.trac;

import org.apache.xmlrpc.XmlRpcException;

public class RuntimeXmlRpcException extends RuntimeException {

	private static final long serialVersionUID = -6742641522561846770L;

	public RuntimeXmlRpcException(XmlRpcException e) {
		super(e);
	}
}
