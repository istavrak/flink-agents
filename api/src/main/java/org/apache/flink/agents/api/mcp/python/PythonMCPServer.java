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


import org.apache.flink.agents.api.chat.messages.ChatMessage;
import org.apache.flink.agents.api.mcp.BaseMCPServer;
import org.apache.flink.agents.api.resource.Resource;
import org.apache.flink.agents.api.resource.ResourceDescriptor;
import org.apache.flink.agents.api.resource.ResourceType;
import org.apache.flink.agents.api.resource.python.PythonResourceAdapter;
import org.apache.flink.agents.api.resource.python.PythonResourceWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class PythonMCPServer extends BaseMCPServer implements PythonResourceWrapper {
    private final Object pythonResource;
    private final PythonResourceAdapter adapter;

    public PythonMCPServer(Object pythonResource, PythonResourceAdapter adapter) {
        super();
        this.pythonResource = pythonResource;
        this.adapter = adapter;
    }

    public PythonMCPServer(
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

    @SuppressWarnings("unchecked")
    public List<PythonMCPTool> listTools() {
        Object result = adapter.callMethod(pythonResource, "list_tools", Collections.emptyMap());
        if (result instanceof List) {
            List<Object> pythonTools = (List<Object>) result;
            List<PythonMCPTool> tools = new ArrayList<>(pythonTools.size());
            for (Object pyTool : pythonTools) {
                tools.add(new PythonMCPTool(adapter, pyTool, null, null));
            }
            return tools;
        }
        return Collections.emptyList();
    }

    public PythonMCPTool getTool(String name) {
        Object pyTool = adapter.callMethod(pythonResource, "get_tool", Map.of("name", name));
        if (pyTool != null) {
            return new PythonMCPTool(adapter, pyTool, null, null);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<PythonMCPPrompt> listPrompts() {
        Object result = adapter.callMethod(pythonResource, "list_prompts", Collections.emptyMap());
        if (result instanceof List) {
            List<Object> pythonPrompts = (List<Object>) result;
            List<PythonMCPPrompt> prompts = new ArrayList<>(pythonPrompts.size());
            for (Object pyPrompt : pythonPrompts) {
                prompts.add(new PythonMCPPrompt(adapter, pyPrompt, null, null));
            }
            return prompts;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<ChatMessage> getPrompt(String name, Map<String, Object> arguments) {
        Object result =
                adapter.callMethod(
                        pythonResource, "get_prompt", Map.of("name", name, "arguments", arguments));
        if (result instanceof List) {
            List<Object> pythonMessages = (List<Object>) result;
            List<ChatMessage> chatMessages = new ArrayList<>(pythonMessages.size());
            for (Object pythonMsg : pythonMessages) {
                chatMessages.add(adapter.fromPythonChatMessage(pythonMsg));
            }
            return chatMessages;
        }
        return Collections.emptyList();
    }

    public List<Object> callTool(String toolName, Map<String, Object> arguments) {
        Object result =
                adapter.callMethod(
                        pythonResource,
                        "call_tool_async",
                        Map.of("tool_name", toolName, "kwargs", arguments));
        if (result instanceof List) {
            return (List<Object>) result;
        }
        return Collections.emptyList();
    }

    @Override
    public void close() throws Exception {
        adapter.callMethod(pythonResource, "close", Collections.emptyMap());
    }
}