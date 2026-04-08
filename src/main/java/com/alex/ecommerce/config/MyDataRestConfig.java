package com.alex.ecommerce.config;

import com.alex.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    private final EntityManager entityManager;

    @Value("${allowed.origins}")
    private String [] theAllowedOrigins;

    @Autowired
    public MyDataRestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    // Custom configurations for Spring Data REST can be added here

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // disable HTTP methods: PUT, POST and DELETE for Product and ProductCategory
        HttpMethod[] unsupportedActions = {HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.POST, HttpMethod.DELETE};

        // Disable for Product: item and collection
        config.getExposureConfiguration()
                .forDomainType(Product.class)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions));

        // Disable for ProductCategory: item and collection
        disableHttpMethods(config, unsupportedActions, ProductCategory.class);
        disableHttpMethods(config, unsupportedActions, Product.class);
        disableHttpMethods(config, unsupportedActions, Country.class);
        disableHttpMethods(config, unsupportedActions, State.class);
        disableHttpMethods(config, unsupportedActions, Order.class);

        // expose entity ids
        exposeIds(config);

        // configure CORS mapping
        cors.addMapping(config.getBasePath() + "/**")
                .allowedOrigins(theAllowedOrigins);

        // call super as a best practice (optional)
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);



    }

    private static void disableHttpMethods(RepositoryRestConfiguration config, HttpMethod[] unsupportedActions, Class<?> domainType) {
        config.getExposureConfiguration()
                .forDomainType(domainType)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        // get a list of all entity classes from the entity manager
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        // create an array of the entity types
        List<Class> entityClasses = new ArrayList<>();

        // get the entity types for the entities
        for (EntityType tempEntityType : entities) {
            entityClasses.add(tempEntityType.getJavaType());
        }

        // expose the entity ids for the array of entity/domain types
        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}