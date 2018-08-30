package com.sc.socket.core.exception;

/**
 *
 *
 * 2017年4月1日 上午9:33:24
 */
public class AioDecodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8207465969738755041L;

	/**
	 *
	 *
	 *
	 *
	 */
	public AioDecodeException() {
	}

	/**
	 * @param message
	 *
	 *
	 *
	 */
	public AioDecodeException(String message) {
		super(message);

	}

	/**
	 * @param message
	 * @param cause
	 *
	 *
	 *
	 */
	public AioDecodeException(String message, Throwable cause) {
		super(message, cause);

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
	public AioDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	/**
	 * @param cause
	 *
	 *
	 *
	 */
	public AioDecodeException(Throwable cause) {
		super(cause);

	}

}
