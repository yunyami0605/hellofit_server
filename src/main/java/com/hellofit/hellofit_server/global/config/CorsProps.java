// com.hellofit.hellofit_server.global.config.CorsProps.java
package com.hellofit.hellofit_server.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter @Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProps {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private boolean allowCredentials = true;
    private Long maxAge = 3600L;
}
