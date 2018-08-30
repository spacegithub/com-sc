package com.sc.socket.core.exception;

/**
 * 
 *
 * 2017年4月1日 上午9:33:24
 */
public class LengthOverflowException extends Throwable {

	/**
	 * @含义: 
	 * @类型: long
	 */
	private static final long serialVersionUID = 5231789012657669073L;

	/**
	 * 
	 *
	 *
	 * 
	 */
	public LengthOverflowException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 *
	 *
	 * 
	 */
	public LengthOverflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	/**
	 * @param message
	 * @param cause
	 *
	 *
	 * 
	 */
	public LengthOverflowException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 *
	 *
	 * 
	 */
	public LengthOverflowException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 *
	 *
	 * 
	 */
	public LengthOverflowException(Throwable cause) {
		super(cause);

	}

}
