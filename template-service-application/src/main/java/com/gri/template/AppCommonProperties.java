package com.gri.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "template-service-application")
public class AppCommonProperties {

    @Value("${spring.flyway.clean-install: false}")
    private boolean cleanInstall;

    public boolean isCleanInstall() {
        return cleanInstall;
    }

}
