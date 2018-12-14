package com.gwu.cs6431.server.service.handler;

import com.gwu.cs6431.server.service.message.Message;

/**
 * The interface of all Handler. Defines what handlers do.
 *
 * @author qijiuzhi
 */
public interface Handler {
    void handle(Message msg);
}
