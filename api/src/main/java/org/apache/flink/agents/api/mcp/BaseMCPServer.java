/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.agents.api.mcp;

import org.apache.flink.agents.api.resource.Resource;
import org.apache.flink.agents.api.resource.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Abstract base class for MCP servers. */
public abstract class BaseMCPServer extends Resource {

    protected final String endpoint;
    protected final Map<String, String> headers;
    protected final long timeoutSeconds;

    protected BaseMCPServer(String endpoint, Map<String, String> headers, long timeoutSeconds) {
        this.endpoint = endpoint;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.timeoutSeconds = timeoutSeconds;
    }

    protected BaseMCPServer() {
        this.endpoint = null;
        this.headers = new HashMap<>();
        this.timeoutSeconds = 30;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MCP_SERVER;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseMCPServer that = (BaseMCPServer) o;
        return timeoutSeconds == that.timeoutSeconds
                && Objects.equals(endpoint, that.endpoint)
                && Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, headers, timeoutSeconds);
    }

    @Override
    public String toString() {
        return String.format("%s{endpoint='%s'}", getClass().getSimpleName(), endpoint);
    }
}