/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.flink.agents.api.mcp.python;

import org.apache.flink.agents.api.mcp.BaseMCPServer;
import org.apache.flink.agents.api.mcp.BaseMCPTool;
import org.apache.flink.agents.api.resource.Resource;
import org.apache.flink.agents.api.resource.ResourceDescriptor;
import org.apache.flink.agents.api.resource.ResourceType;
import org.apache.flink.agents.api.resource.python.PythonResourceAdapter;
import org.apache.flink.agents.api.resource.python.PythonResourceWrapper;
import org.apache.flink.agents.api.tools.ToolParameters;
import org.apache.flink.agents.api.tools.ToolResponse;
import org.apache.flink.agents.api.tools.ToolType;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

public class PythonMCPTool extends BaseMCPTool implements PythonResourceWrapper {
    private final Object pythonResource;
    private final PythonResourceAdapter adapter;

    public PythonMCPTool(PythonResourceAdapter adapter, Object pythonResource) {
        super();
        this.pythonResource = pythonResource;
        this.adapter = adapter;
    }

    public PythonMCPTool(
            PythonResourceAdapter adapter,
            Object pythonResource,
            ResourceDescriptor descriptor,
            BiFunction<String, ResourceType, Resource> getResource) {
        super();
        this.pythonResource = pythonResource;
        this.adapter = adapter;
    }

    @Override
    public Object getPythonResource() {
        return pythonResource;
    }

    @Override
    public BaseMCPServer getMcpServer() {
        Object pythonServer = adapter.callMethod(pythonResource, "mcp_server", Collections.emptyMap());
        if (pythonServer != null) {
            return new PythonMCPServer(pythonServer, adapter);
        }
        return null;
    }

    @Override
    public ToolResponse call(ToolParameters parameters) {
        return (ToolResponse)
                adapter.callMethod(
                        pythonResource, "call", Map.of("kwargs", prepareArguments(parameters)));
    }

    @Override
    public void close() throws Exception {
        adapter.callMethod(pythonResource, "close", Collections.emptyMap());
    }
}
