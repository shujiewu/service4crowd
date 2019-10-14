package cn.edu.buaa.act.mongodb.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import java.util.Set;

@Configuration
public class MyRepositoryRestMvcConfiguration {
//    @Bean
//    public RepositoryRestConfigurer repositoryRestConfigurer() {
//        return new RepositoryRestConfigurerAdapter() {
//            @Override
//            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//                final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
//                // provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
//                final Set<BeanDefinition> beans = provider.findCandidateComponents("cn.edu.buaa.act.mongodb.entity");
//                for (final BeanDefinition bean : beans) {
//                    try {
//                        config.exposeIdsFor(Class.forName(bean.getBeanClassName()));
//                    } catch (final ClassNotFoundException e) {
//                        // Can't throw ClassNotFoundException due to the method signature. Need to cast it            	        t
//                        // hrow new IllegalStateException("Failed to expose `id` field due to", e);
//                    }
//                }
//            }
//        };
//    }
}
