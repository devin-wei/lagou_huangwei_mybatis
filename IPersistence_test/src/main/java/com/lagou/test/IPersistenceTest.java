package com.lagou.test;

import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import com.lagou.io.Resources;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {

        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        User user = new User();
        user.setId(1);
        user.setUsername("李四");
        User user1 = sqlSession.selectOne("user.selectOne", user);
        System.out.println(user1);



    }

    @Test
    public void test2() throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //编辑
        User user2 = new User();
        user2.setId(1);
        user2.setUsername("李四");
        int update = sqlSession.update("user.update", user2);

        Object o = sqlSession.selectOne("user.selectOne", user2);
        System.out.println((o));
    }

    @Test
    public void test3() throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //删除
        User user = new User();
        user.setId(1);
        int delete = sqlSession.delete("user.delete", user);
        System.out.println(delete);
    }


}
