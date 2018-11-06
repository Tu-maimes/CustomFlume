/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ysstest.hdfs;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSEventSink extends AbstractSink implements Configurable {
    private static final Logger LOG = LoggerFactory.getLogger(HDFSEventSink.class);

    @Override
    public void configure(Context context) {

    }

    @Override
    public Status process() throws EventDeliveryException {

        Status status = null;
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        Event event = null;
        txn.begin();
        while (true) {
            event = ch.take();
            if (event != null) {
                break;
            }
        }
        try {
            LOG.debug("Get event.");
            byte[] recivevalue = event.getBody();
            System.out.println(new String(recivevalue, 0, recivevalue.length));
            status = Status.READY;
            txn.commit();
        } catch (Throwable th) {
            txn.rollback();
            status = Status.BACKOFF;
            if (th instanceof Error) {
                throw (Error) th;
            } else {
                throw new EventDeliveryException(th);
            }
        } finally {
            txn.close();
        }
        return status;

    }
}
