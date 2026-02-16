package com.example.examen.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import javax.sql.DataSource;

@Configuration
public class ConfiguracionSeguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select email, pw, activo from usuario where email=?");
        users.setAuthoritiesByUsernameQuery("SELECT email, perfil FROM usuario WHERE email=?");
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/imagenes/**").permitAll()
                .requestMatchers("/", "/login", "/logout", "/registro", "/registro/guardar").permitAll()
                .requestMatchers("/cliente/**").hasAuthority("CLIENTE")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated());
        http.formLogin(formLogin -> formLogin.loginPage("/login").permitAll());
        http.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll());
        http.exceptionHandling((exception) -> exception.accessDeniedPage("/denegado"));

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
