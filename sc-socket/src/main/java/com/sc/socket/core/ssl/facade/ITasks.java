package com.sc.socket.core.ssl.facade;

import javax.net.ssl.SSLException;

public interface ITasks
{
    Runnable next();

    void done() throws SSLException;
}
