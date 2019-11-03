package de.unipassau.sep19.hafenkran.userservice.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.unipassau.sep19.hafenkran.userservice.controller",
        "de.unipassau.sep19.hafenkran.userservice.util",
})
@EnableAutoConfiguration
public class ConfigEntrypoint {

}
