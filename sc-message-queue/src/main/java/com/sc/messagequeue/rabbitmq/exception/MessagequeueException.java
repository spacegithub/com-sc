package com.sc.messagequeue.rabbitmq.exception;

public class MessagequeueException extends RuntimeException {
    public MessagequeueException(String message) {
        super(message);
    }

    public MessagequeueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessagequeueException(Throwable cause) {
        super(cause);
    }
}
