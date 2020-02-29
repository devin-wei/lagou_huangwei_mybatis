package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{


    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //执行sql
        PreparedStatement prepareStatement = getPrepareStatement(configuration, mappedStatement, params[0]);
        ResultSet resultSet = prepareStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();

        //封装返回结果集
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                //使用内省，利用数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }

    private PreparedStatement getPrepareStatement(Configuration configuration, MappedStatement mappedStatement, Object param) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);
        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(param);
            preparedStatement.setObject(i + 1, o);
        }
        return preparedStatement;
    }

    @Override
    public int execute(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws Exception {
        PreparedStatement prepareStatement = getPrepareStatement(configuration, mappedStatement, params[0]);
        int execute = prepareStatement.executeUpdate();
        return execute;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析后sql
        String parseSql = genericTokenParser.parse(sql);
        //解析后的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
