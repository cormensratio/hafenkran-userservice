package de.unipassau.sep19.hafenkran.userservice.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "de.unipassau.sep19.hafenkran.userservice.controller",
        "de.unipassau.sep19.hafenkran.userservice.util",
        "de.unipassau.sep19.hafenkran.userservice.service.impl",
        "de.unipassau.sep19.hafenkran.userservice.serviceclient.impl"
})
@EntityScan(basePackages = {
        "de.unipassau.sep19.hafenkran.userservice.model"
})
@EnableJpaRepositories(basePackages = {
        "de.unipassau.sep19.hafenkran.userservice.repository"
})
@EnableAutoConfiguration
public class ConfigEntrypoint {

}
