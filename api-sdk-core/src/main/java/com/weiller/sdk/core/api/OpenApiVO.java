package com.weiller.sdk.core.api;

/**
 * OpenApiVO	@version 1.0
 */
public class OpenApiVO {

	private OpenApiProtocol protocol;
	private String path;
	private String name;
	
	public OpenApiProtocol getProtocol() {
		return protocol;
	}
	
	public OpenApiVO setProtocol(OpenApiProtocol protocol) {
		this.protocol = protocol;
		return this;
	}
	public String getPath() {
		return path;
	}
	public OpenApiVO setPath(String path) {
		this.path = path;
		return this;
	}
	public String getName() {
		return name;
	}
	public OpenApiVO setName(String name) {
		this.name = name;
		return this;
	}
	
	
}
