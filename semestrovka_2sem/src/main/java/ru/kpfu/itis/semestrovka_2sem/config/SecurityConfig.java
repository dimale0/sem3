package ru.kpfu.itis.semestrovka_2sem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.service.UserService;
import ru.kpfu.itis.semestrovka_2sem.config.RoleBasedSuccessHandler;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleBasedSuccessHandler successHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

            String role = user.getRole().name(); // e.g. "ADMIN", "TUTOR", "STUDENT"
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities("ROLE_" + role)
                    .build();
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Подключаем свой провайдер аутентификации:
                .authenticationProvider(authenticationProvider())

                // Включаем CSRF (по умолчанию CSRF уже включён, достаточно не вызывать .disable()):
                //.csrf(AbstractHttpConfigurer::disable) // не отключаем

                // Разрешаем всем GET- и POST-запросы к /login
                .authorizeHttpRequests(authz -> authz
                        // GET-страница логина должна быть общедоступна:
                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                        // POST-запрос авторизации (форма) тоже разрешаем:
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // Разрешаем регистрацию и статику:
                        .requestMatchers("/", "/users/register", "/css/**", "/js/**", "/favicon.ico").permitAll()

                        // Доступные всем страницы просмотра
                        .requestMatchers(HttpMethod.GET, "/tutors", "/tutors/*", "/subjects", "/requests", "/requests/view/**").permitAll()

                        // Примеры ограничения по ролям:
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/tutors/create/**", "/tutors/edit/**", "/tutors/delete/**").hasRole("TUTOR")
                        .requestMatchers("/subjects/create/**", "/subjects/edit/**", "/subjects/delete/**").hasRole("TUTOR")
                        .requestMatchers("/requests/create/**", "/requests/edit/**", "/requests/delete/**").hasRole("TUTOR")
                        .requestMatchers("/requests/*/respond").hasRole("STUDENT")
                        .requestMatchers("/responses/student/**").hasRole("STUDENT")
                        .requestMatchers("/responses/request/**", "/responses/tutor/**").hasRole("TUTOR")

                        // Любые остальные запросы — только для аутентифицированных:
                        .anyRequest().authenticated()
                )

                // Настройка формы логина:
                .formLogin(form -> form
                        .loginPage("/login")          // наш GET-эндпоинт, который возвращает auth/login.html
                        .loginProcessingUrl("/login") // URL, на который будет отправляться POST с username/password
                        .successHandler(successHandler)
                        .failureUrl("/login?error")   // при ошибке добавляется ?error
                        .permitAll()
                )

                // Настройка выхода:
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
