package com.gba.ws.config;

import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gba.ws.util.AppAuthentication;
//helloworld
/**
 * Extends {@link WebMvcConfigurerAdapter} class, provides application
 * configuration details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:47:57 PM
 */
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {
	//helloworld
	private static final int CONNECTION_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
	private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(60);

	@Autowired
	private AppInterceptor appInterceptor;

	@Bean
	public AsyncRestTemplate asyncRestTemplate() {
		return new AsyncRestTemplate();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder rtb) {
		return rtb.setConnectTimeout(CONNECTION_TIMEOUT).setReadTimeout(READ_TIMEOUT).build();
	}

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean fact = new HibernateJpaSessionFactoryBean();
		fact.setEntityManagerFactory(emf);
		return fact;
	}

	@Bean
	public AppInterceptor appInterceptor() {
		return new AppInterceptor();
	}

	@Bean
	public AppAuthentication appAuthentication() {
		return new AppAuthentication();
	}

	@Bean
	public FilterRegistrationBean greetingFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		AppFilter appFilter = new AppFilter();
		registrationBean.setFilter(appFilter);
		registrationBean.setOrder(1);
		registrationBean.setEnabled(false);
		return registrationBean;
	}

	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(appInterceptor);
		super.addInterceptors(registry);
	}

}
