package com.weiller.sdk.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ServiceInfo @version 1.0
 */
@Data
@AllArgsConstructor
public class ServiceInfo {

	private OpenApiProtocol protocol;
	private String name;
}
