package com.example.conf;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomServletConf implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.remove("js");
        mappings.add("js", "application/javascript;charset=utf-8");
        mappings.remove("css");
        mappings.add("css", "text/css;charset=utf-8");
        factory.setMimeMappings(mappings);
    }
}