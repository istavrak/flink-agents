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
 
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.agents.api.prompt.Prompt;
import org.apache.flink.agents.api.resource.ResourceType;
 
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
 
/** Abstract base class for MCP prompts. */
public abstract class BaseMCPPrompt extends Prompt {
 
    protected final String name;
    protected final String description;
    protected final Map<String, PromptArgument> promptArguments;
 
    protected BaseMCPPrompt(
            String name, String description, Map<String, PromptArgument> promptArguments) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.description = description;
        this.promptArguments =
                promptArguments != null ? new HashMap<>(promptArguments) : new HashMap<>();
    }
 
    protected BaseMCPPrompt() {
        this.name = null;
        this.description = null;
        this.promptArguments = new HashMap<>();
    }
 
    @Override
    public ResourceType getResourceType() {
        return ResourceType.MCP_PROMPT;
    }
 
    public String getName() {
        return name;
    }
 
    public String getDescription() {
        return description;
    }
 
    public Map<String, PromptArgument> getPromptArguments() {
        return new HashMap<>(promptArguments);
    }
 
    /**
     * Get the MCP server associated with this prompt.
     *
     * @return The MCP server
     */
    @JsonIgnore
    public abstract BaseMCPServer getMcpServer();
 
    /** Represents an argument that can be passed to an MCP prompt. */
    public static class PromptArgument {
        @JsonProperty("name")
        private final String name;
 
        @JsonProperty("description")
        private final String description;
 
        @JsonProperty("required")
        private final boolean required;
 
        @JsonCreator
        public PromptArgument(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("required") boolean required) {
            this.name = Objects.requireNonNull(name, "name cannot be null");
            this.description = description;
            this.required = required;
        }
 
        public String getName() {
            return name;
        }
 
        public String getDescription() {
            return description;
        }
 
        public boolean isRequired() {
            return required;
        }
 
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PromptArgument that = (PromptArgument) o;
            return required == that.required
                    && Objects.equals(name, that.name)
                    && Objects.equals(description, that.description);
        }
 
        @Override
        public int hashCode() {
            return Objects.hash(name, description, required);
        }
    }
 
    /**
     * Validate that all required arguments are present and prepare the arguments map.
     *
     * @param arguments The provided arguments
     * @return A validated map of arguments
     * @throws IllegalArgumentException if required arguments are missing
     */
    protected Map<String, Object> validateAndPrepareArguments(Map<String, Object> arguments) {
        Map<String, Object> result = new HashMap<>();
 
        for (PromptArgument arg : promptArguments.values()) {
            if (arg.isRequired()) {
                if (arguments == null || !arguments.containsKey(arg.getName())) {
                    throw new IllegalArgumentException(
                            "Missing required argument: " + arg.getName());
                }
                result.put(arg.getName(), arguments.get(arg.getName()));
            } else if (arguments != null && arguments.containsKey(arg.getName())) {
                result.put(arg.getName(), arguments.get(arg.getName()));
            }
        }
 
        return result;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseMCPPrompt that = (BaseMCPPrompt) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(promptArguments, that.promptArguments)
                && Objects.equals(getMcpServer(), that.getMcpServer());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(name, description, promptArguments, getMcpServer());
    }
 
    @Override
    public String toString() {
        return String.format(
                "%s{name='%s', server='%s'}",
                getClass().getSimpleName(),
                name,
                getMcpServer() != null ? getMcpServer().toString() : "null");
    }
}
