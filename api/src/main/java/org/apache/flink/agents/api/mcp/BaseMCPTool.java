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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.flink.agents.api.tools.Tool;
import org.apache.flink.agents.api.tools.ToolMetadata;
import org.apache.flink.agents.api.tools.ToolParameters;
import org.apache.flink.agents.api.tools.ToolType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Abstract base class for MCP tools. */
public abstract class BaseMCPTool extends Tool {

    protected BaseMCPTool(ToolMetadata metadata) {
        super(metadata);
    }

    protected BaseMCPTool() {
        super(new ToolMetadata("", "", ""));
    }

    @Override
    @JsonIgnore
    public ToolType getToolType() {
        return ToolType.MCP;
    }

    /**
     * Get the MCP server associated with this tool.
     *
     * @return The MCP server
     */
    @JsonIgnore
    public abstract BaseMCPServer getMcpServer();

    /**
     * Helper method to convert ToolParameters to a Map of arguments.
     *
     * @param parameters The tool parameters
     * @return A map of argument names to values
     */
    protected Map<String, Object> prepareArguments(ToolParameters parameters) {
        Map<String, Object> arguments = new HashMap<>();
        for (String paramName : parameters.getParameterNames()) {
            arguments.put(paramName, parameters.getParameter(paramName));
        }
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseMCPTool that = (BaseMCPTool) o;
        return Objects.equals(metadata, that.metadata)
                && Objects.equals(getMcpServer(), that.getMcpServer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, getMcpServer());
    }

    @Override
    public String toString() {
        return String.format(
                "%s{name='%s', server='%s'}",
                getClass().getSimpleName(),
                metadata.getName(),
                getMcpServer() != null ? getMcpServer().toString() : "null");
    }
}
