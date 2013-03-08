/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Decoder;
import javax.websocket.Decoder.Binary;
import javax.websocket.Decoder.BinaryStream;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * ByteBuffer specific concrete whole message implementation.
 */
public class PojoMessageHandlerWholeBinary
        extends PojoMessageHandlerWholeBase<ByteBuffer> {

    private final List<Decoder> decoders = new ArrayList<>();

    public PojoMessageHandlerWholeBinary(Object pojo, Method method,
            Session session, EndpointConfig config, Object[] params,
            int indexPayload, boolean unwrap, int indexSession) {
        super(pojo, method, session, params, indexPayload, unwrap, indexSession);
        try {
            for (Class<? extends Decoder> decoderClazz : config.getDecoders()) {
                if (Binary.class.isAssignableFrom(decoderClazz)) {
                    Binary<?> decoder = (Binary<?>) decoderClazz.newInstance();
                    decoder.init(config);
                    decoders.add(decoder);
                } else if (Decoder.TextStream.class.isAssignableFrom(
                        decoderClazz)) {
                    BinaryStream<?> decoder =
                            (BinaryStream<?>) decoderClazz.newInstance();
                    decoder.init(config);
                    decoders.add(decoder);
                } else {
                    // Text decoder - ignore is
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    protected Object decode(ByteBuffer message) {
        // TODO Not implemented
        return null;
    }


    @Override
    protected void onClose() {
        for (Decoder decoder : decoders) {
            decoder.destroy();
        }
    }
}