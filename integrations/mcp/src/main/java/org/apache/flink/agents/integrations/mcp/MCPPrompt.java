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

package org.apache.flink.agents.integrations.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.agents.api.chat.messages.ChatMessage;
import org.apache.flink.agents.api.chat.messages.MessageRole;
import org.apache.flink.agents.api.mcp.BaseMCPPrompt;
import org.apache.flink.agents.api.mcp.BaseMCPServer;
import org.apache.flink.agents.api.prompt.Prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MCP prompt definition that extends the base Prompt class.
 *
 * <p>This represents a prompt managed by an MCP server. Unlike static prompts, MCP prompts are
 * fetched dynamically from the server and can accept arguments.
 */
public class MCPPrompt extends BaseMCPPrompt {
 
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ARGUMENTS = "arguments";
    private static final String FIELD_MCP_SERVER = "mcpServer";
 
    @JsonProperty(FIELD_MCP_SERVER)
    private final MCPServer mcpServer;
 
    /**
     * Create a new MCPPrompt.
     *
     * @param name The prompt name
     * @param description The prompt description
     * @param promptArguments Map of argument names to argument definitions
     * @param mcpServer The MCP server reference
     */
    @JsonCreator
    public MCPPrompt(
            @JsonProperty(FIELD_NAME) String name,
            @JsonProperty(FIELD_DESCRIPTION) String description,
            @JsonProperty(FIELD_ARGUMENTS) Map<String, PromptArgument> promptArguments,
            @JsonProperty(FIELD_MCP_SERVER) MCPServer mcpServer) {
        super(name, description, promptArguments);
        this.mcpServer = Objects.requireNonNull(mcpServer, "mcpServer cannot be null");
    }
 
    @Override
    @JsonIgnore
    public BaseMCPServer getMcpServer() {
        return mcpServer;
    }

    /**
     * Format the prompt as a string with the given arguments. Overrides the base Prompt class to
     * fetch prompts from the MCP server.
     *
     * @param arguments Arguments to pass to the prompt (String keys and values)
     * @return The formatted prompt as a string
     */
    @Override
    public String formatString(Map<String, String> arguments) {
        List<ChatMessage> messages = formatMessages(MessageRole.SYSTEM, arguments);
        return messages.stream()
                .map(msg -> msg.getRole().getValue() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Format the prompt as a list of chat messages with the given arguments. Overrides the base
     * Prompt class to fetch prompts from the MCP server.
     *
     * @param defaultRole The default role for messages (usually SYSTEM)
     * @param kwargs Arguments to pass to the prompt (String keys and values)
     * @return List of formatted chat messages
     */
    @Override
    public List<ChatMessage> formatMessages(MessageRole defaultRole, Map<String, String> kwargs) {
        Map<String, Object> objectArgs = new HashMap<>(kwargs);
        return formatMessages(objectArgs);
    }

    /**
     * Format the prompt as a list of chat messages with the given arguments.
     *
     * @param arguments Arguments to pass to the prompt (Object values)
     * @return List of formatted chat messages
     */
    private List<ChatMessage> formatMessages(Map<String, Object> arguments) {
        return mcpServer.getPrompt(name, validateAndPrepareArguments(arguments));
    }
 
    @Override
    public void close() throws Exception {
        this.mcpServer.close();
    }
}
