package com.example.demo.base;

import com.example.demo.SpringbootMybatisShardingApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Created by liunanhua on 2018/3/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootMybatisShardingApplication.class)
@ActiveProfiles(profiles = "dev")
public class BaseTest {


}
