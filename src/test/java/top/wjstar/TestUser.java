package top.wjstar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
public class TestUser {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 测试生成密码
     */
    @Test
    public void test1() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
