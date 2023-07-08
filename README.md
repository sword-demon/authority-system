# springboot 实现简单权限管理

## spring security

登录认证主要涉及 2 个重要的接口：`UserDetailsService`和`UserDetails`接口。
`UserDetailsService`接口主要定义了一个方法`loadUserByUsername`用于完成用户信息的查询，
其中`username`就是登录时的登录用户名，登录认证时没需要自定义一个实现类实现`UserDetailsService`接口，
完成数据库查询，该接口返回`UserDetail`。

`UserDetail`主要用于封装认证成功时的用户信息，即`UserDetailsService`返回的用户信息，
可以使用自己定义的一个用户实体类，但是最好实现`UserDetail`接口。

## 认证步骤

1. 自定义`UserDetails`类；当实体类对象字段不满足时需要自定义`UserDetails`，一般都要自定义
2. 自定义`UserDetailsService`类，主要用于从数据库查询用户信息
3. 创建登录认证成功处理器，认证成功后需要返回`JSON`数据，菜单权限等
4. 创建登录认证失败处理器，认证失败需要返回`JSON`数据，给前端判断
5. 创建匿名用户访问无权限资源处理器，匿名用户访问时，需要提示`JSON`
6. 创建认证过得用户访问无权限资源时的处理器，无权限访问时，需要提示`JSON`
7. 配置`Spring Security`配置类时，把上面自定义的处理器交给`Spring Security`

## 添加 Spring Security 依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## 自定义`UserDetails`类

1. 将`User`类实现`UserDetails`接口
2. 将原有的`isAccountNnExpired`、`isAccountNonLocked`、`isCredentialsNonExpired`和`isEnabled`属性修改成`boolean`
   类型，同时添加`authorities`属性。
3. **注意：上面四个属性只能是非包装类的`boolean`类型属性，而且设置默认值为`true`**

## 编写 UserService 接口

在对应的你的用户服务类里编写根据用户名查询用户信息的方法

```java
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName
     * @return
     */
    User findUserByUserName(String userName);
}
```

然后去实现它即可。

## 自定义 UserDetailsService 类

在`xxx.xxx.config.security.service`包下创建`CustomerUserDetailsService`类实现`UserDetailsService`接口

```java

@Component
public class CustomerUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        User user = userService.findUserByUserName(username);
        // 如果对象为空，认证失败
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 查询成功返回用户对象
        return user;
    }
}

```

## 编写自定义认证成功处理器

在`xxx.xxx.config.security.handler`包下创建`LoginSuccessHandler`登录认证成功处理器类

```java
/**
 * 登录认证成功处理器类
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 设置响应的编码格式
        response.setContentType("application/json;charset=utf-8");
        // 获取当前登录用户信息
        User user = (User) authentication.getPrincipal();
        // 将对象转换成 JSON 并消除循环引用
        String result = JSON.toJSONString(user, SerializerFeature.DisableCircularReferenceDetect);
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

```

## 编写自定义认证失败处理器

```java

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        // 设置响应的编码格式
        response.setContentType("application/json;charset=utf-8");

        // 定义变量保存异常的信息
        String message;
        // 判断异常类型
        // 账户过期
        if (exception instanceof AccountExpiredException) {
            message = "账户过期，登录失败";
        } else if (exception instanceof BadCredentialsException) {
            message = "用户名密码错误，登录失败";
        } else if (exception instanceof CredentialsExpiredException) {
            message = "密码过期,登录失败";
        } else if (exception instanceof DisabledException) {
            message = "账户被禁用，登录失败";
        } else if (exception instanceof LockedException) {
            message = "账户被锁，登录失败";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            message = "账户不存在，登录失败";
        } else {
            message = "登录失败";
        }

        // 将结果转换成 JSON 格式
        String result = JSON.toJSONString(Result.error().code(ResultCode.ERROR).message(message));

        // 将结果保存到输出流中
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
```

## 编写认证过的用户无权限访问处理器

```java
/**
 * 用户认证无权限资源时处理器
 */
@Component
public class CustomerAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 设置客户端的响应内容类型
        response.setContentType("application/json;charset=utf-8");
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();


        // 将结果转换成 JSON 格式
        String result = JSON.toJSONString(Result.error().code(ResultCode.NO_AUTH).message("无权限访问，请联系管理员"), SerializerFeature.DisableCircularReferenceDetect);

        // 将结果保存到输出流中
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
```

## 编写匿名用户访问无权限资源处理器

```java
/**
 * 匿名用户访问无权限资源时处理器
 */
@Component
public class AnonymousAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 设置客户端的响应内容类型
        response.setContentType("application/json;charset=utf-8");
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();


        // 将结果转换成 JSON 格式
        String result = JSON.toJSONString(Result.error().code(ResultCode.NO_LOGIN).message("匿名用户无权限访问"), SerializerFeature.DisableCircularReferenceDetect);

        // 将结果保存到输出流中
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
```

## 编写 spring security 配置类

> 将前面 6 个步骤整合一起使用

```java

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Resource
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    @Resource
    private CustomerAccessDeniedHandler customerAccessDeniedHandler;

    @Resource
    private CustomerUserDetailsService customerUserDetailsService;

    /**
     * 注入加密类
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 处理登录认证
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 如果表单请求的用户名不是 username 和 password 还需要单独设置
        // 登录过程的处理
        http.formLogin()    // 表单登录
                .loginProcessingUrl("/api/user/login") // 设置登录请求的 url 地址，自定义
                .successHandler(loginSuccessHandler) // 登录认证成功的处理器
                .failureHandler(loginFailureHandler) // 认证失败得处理器
                .and()
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不创建 session
                .and()
                .authorizeRequests() // 设置需要拦截的请求
                .antMatchers("/api/user/login").permitAll() // 登录请求放行
                .anyRequest().authenticated() // 其他一律请求都需要身份认证拦截
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(anonymousAuthenticationHandler) // 匿名无权限访问
                .accessDeniedHandler(customerAccessDeniedHandler) // 认证用户无权限访问
                .and()
                .cors(); // 支持跨域请求
    }

    /**
     * 配置认证处理器
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
```