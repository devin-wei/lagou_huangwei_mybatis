package com.lagou.sqlSession;

import java.sql.SQLException;
import java.util.List;

public interface SqlSession {
    <E> List<E> selectLsit(String statementd, Object... params) throws Exception;

    <T> T selectOne(String statementId, Object... params) throws Exception;

    int update(String statementId, Object... params) throws Exception;

    int delete(String statementId, Object... params) throws Exception;

    void close() throws SQLException;
}
