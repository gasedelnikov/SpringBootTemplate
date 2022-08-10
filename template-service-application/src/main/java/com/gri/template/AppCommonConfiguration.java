package com.gri.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
@EnableConfigurationProperties(value = {AppCommonProperties.class})
@Slf4j
public class AppCommonConfiguration {

    private final AppCommonProperties commonProperties;

    @Autowired
    @Contract(pure = true)
    public AppCommonConfiguration(final AppCommonProperties commonProperties) {
        this.commonProperties = commonProperties;
    }

    @Bean(name = "isCleanInstall")
    public boolean isCleanInstall() {
        return commonProperties.isCleanInstall();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(@Autowired @Qualifier(value = "isCleanInstall") boolean isCleanInstall) {
        return flyway -> {
            if (isCleanInstall) {
                flyway.clean();
            }
            flyway.migrate();
        };
    }

    @Bean
    @Primary
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    @Primary
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
//        return Validation.buildDefaultValidatorFactory().getValidator();

        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure().constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

}
