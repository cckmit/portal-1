package ru.protei.portal.core.controller.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {


    // Create 2 users for demo
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user").password("pswrd").roles("USER")
                .and()
                .withUser("admin").password("pswrd").roles("USER", "ADMIN");

    }

    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                //HttpChannelOverHttp@242ecdc3{r=1,c=false,a=DISPATCHED,uri=/Portal/springApi/api/addyoutrackidintoissue/100447/PG-209}
                .antMatchers( "/Portal/springApi/api/addyoutrackidintoissue/**").hasRole("USER")
                .antMatchers( "/springApi/api/addyoutrackidintoissue/**").hasRole("ADMIN")
                .antMatchers( "/api/addyoutrackidintoissue/**").hasRole("ROOR")
                .antMatchers( "Portal/springApi/api/addyoutrackidintoissue/**").hasRole("USER2")
                .antMatchers( "springApi/api/addyoutrackidintoissue/**").hasRole("ADMIN2")
                .antMatchers( "api/addyoutrackidintoissue/**").hasRole("ROOR2")
//                .antMatchers(HttpMethod.POST, "/addyoutrackidintoissue").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT, "/addyoutrackidintoissue/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PATCH, "/addyoutrackidintoissue/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/addyoutrackidintoissue/**").hasRole("ADMIN")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }


}
