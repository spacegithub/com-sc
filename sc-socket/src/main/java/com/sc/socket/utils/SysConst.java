package com.sc.socket.utils;

/**
 * 2018年7月1日 下午10:51:59
 */
public interface SysConst {


    String SOCKET_CORE_VERSION = "3.1.9.v20180828-RELEASE";
    /**
     * \r
     */
    byte CR = 13;

    /**
     * \n
     */
    byte LF = 10;

    /**
     * =
     */
    byte EQ = '=';

    /**
     * &
     */
    byte AMP = '&';

    /**
     * :
     */
    byte COL = ':';

    /**
     * :
     */
    String COL_STR = ":";

    /**
     * ;
     */
    byte SEMI_COL = ';';

    /**
     * 一个空格
     */
    byte SPACE = ' ';

    /**
     * ?
     */
    byte ASTERISK = '?';

    byte[] CR_LF_CR_LF = {CR, LF, CR, LF};

    byte[] CR_LF = {CR, LF};

    byte[] LF_LF = {LF, LF};

    byte[] SPACE_ = {SPACE};

    byte[] CR_ = {CR};

    byte[] LF_ = {LF};

    /**
     * \r\n
     */
    String CRLF = "\r\n";

    String DEFAULT_ENCODING = "utf-8";
}
