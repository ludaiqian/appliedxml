package org.jczh.appliedxml;

public class NodeException extends RuntimeException {

	private static final long serialVersionUID = -4439737623884755752L;

	public NodeException(String text) {
		super(text);
	}

	public NodeException() {
		super();
	}

	public NodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NodeException(Throwable cause) {
		super(cause);
	}

}
