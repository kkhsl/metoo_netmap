//package com.metoo.sqlite.mapper;
//
//import com.metoo.sqlite.core.config.sqlite.MyBatisUtil;
//import com.metoo.sqlite.entity.OsScan;
//import com.metoo.sqlite.utils.date.DateTools;
//import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
//import org.apache.ibatis.mapping.Environment;
//import org.apache.ibatis.session.Configuration;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.apache.ibatis.transaction.TransactionFactory;
//import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
//
//import javax.activation.DataSource;
//
//public class OsScanMapperImpl implements OsScanMapper{
//
//    @Override
//    public int create(OsScan instance) {
//        // 调用 MyBatis 执行 SQL 插入
//        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
//            OsScanMapper mapper = sqlSession.getMapper(OsScanMapper.class);
//            mapper.create(instance);
//            sqlSession.commit();
//            return 1;
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//            return 0;
//        }
//    }
//    public static void main(String[] args) {
//        // SQLite 数据库文件路径
//        String databaseUrl = "jdbc:sqlite:/path/to/your/mydatabase.db";
//
//        // 配置 DataSource
//        DataSource dataSource = createDataSource(databaseUrl);
//
//        // 配置事务管理器
//        TransactionFactory transactionFactory = new JdbcTransactionFactory();
//
//        // 配置环境
//        Environment environment = new Environment("development", transactionFactory, dataSource);
//
//        // 配置 MyBatis Configuration
//        Configuration configuration = new Configuration(environment);
//        configuration.addMapper(UserMapper.class); // 添加 Mapper 接口
//
//        // 构建 SqlSessionFactory
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
//
//        // 使用 SqlSessionFactory 创建 SqlSession
//        try (SqlSession session = sqlSessionFactory.openSession()) {
//            // 获取 Mapper 接口
//            OsScanMapper osScanMapper = session.getMapper(OsScanMapper.class);
//
//            // 执行数据库操作
//            // 示例：插入用户
//            OsScan osScan = new OsScan();
//            osScan.setCreateTime(DateTools.getCreateTime());
//            osScanMapper.create(osScan);
//
//            // 提交事务
//            session.commit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 创建 DataSource
//    private static DataSource createDataSource(String databaseUrl) {
//        UnpooledDataSourceFactory dataSourceFactory = new UnpooledDataSourceFactory();
//        dataSourceFactory.setDriver("org.sqlite.JDBC");
//        dataSourceFactory.setUrl(databaseUrl);
//        return (DataSource) dataSourceFactory.getDataSource();
//    }
//}
