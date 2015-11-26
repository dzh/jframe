/**
 * 
 */
package jframe.pay.dao.service;

import org.apache.ibatis.session.SqlSession;

import jframe.core.plugin.annotation.InjectService;
import jframe.core.plugin.annotation.Injector;
import jframe.memcached.client.MemcachedService;
import jframe.mybatis.MultiMybatisService;
import jframe.pay.domain.dao.OrderAlipay;
import jframe.pay.domain.dao.OrderUpmp;
import jframe.pay.domain.dao.OrderWx;
import jframe.pay.domain.dao.UsrAccount;
import jframe.pay.domain.dao.mapper.Environment;

/**
 * @author dzh
 * @date Sep 2, 2015 2:47:02 AM
 * @since 1.0
 */
@Injector
class MysqlPayDaoService implements PayDaoService {

    @InjectService(id = "jframe.service.memcached.client")
    protected static MemcachedService MemSvc;

    @InjectService(id = "jframe.service.multimybatis")
    protected static MultiMybatisService MultiMybatisSvc;

    @Override
    public void insertUsrAccount(UsrAccount usr) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            session.insert("jframe.pay.domain.dao.mapper.UsrMapper.insertUsrAccount", usr);
            session.commit();
        }
    }

    @Override
    public UsrAccount selectUsrAccount(String account) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.UsrMapper.selectUsrAccount", account);
        }
    }

    @Override
    public int updateUsrAccount(UsrAccount usr) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            int sum = session.update("jframe.pay.domain.dao.mapper.UsrMapper.updateUsrAccount", usr);
            session.commit();
            return sum;
        }
    }

    @Override
    public void insertOrderAlipay(OrderAlipay od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            session.insert("jframe.pay.domain.dao.mapper.OrderMapper.insertOrderAlipay", od);
            session.commit();
        }
    }

    @Override
    public OrderAlipay selectOrderAlipay(String payNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderAlipay", payNo);
        }
    }

    @Override
    public int updateOrderAlipay(OrderAlipay od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            int sum = session.update("jframe.pay.domain.dao.mapper.OrderMapper.updateOrderAlipay", od);
            session.commit();
            return sum;
        }
    }

    @Override
    public OrderAlipay selectOrderAlipayWithOrderNo(String orderNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderAlipayWithOrderNo", orderNo);
        }
    }

    @Override
    public void insertOrderWx(OrderWx od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            session.insert("jframe.pay.domain.dao.mapper.OrderMapper.insertOrderWx", od);
            session.commit();
        }
    }

    @Override
    public OrderWx selectOrderWx(String payNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderWx", payNo);
        }
    }

    @Override
    public int updateOrderWx(OrderWx od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            int sum = session.update("jframe.pay.domain.dao.mapper.OrderMapper.updateOrderWx", od);
            session.commit();
            return sum;
        }
    }

    @Override
    public OrderWx selectOrderWxWithOrderNo(String orderNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderWxWithOrderNo", orderNo);
        }
    }

    @Override
    public OrderUpmp selectOrderUpmp(String payNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderUpmp", payNo);
        }
    }

    @Override
    public OrderUpmp selectOrderUpmpWithOrderNo(String orderNo) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN_RO1).openSession()) {
            return session.selectOne("jframe.pay.domain.dao.mapper.OrderMapper.selectOrderUpmpWithOrderNo", orderNo);
        }
    }

    @Override
    public int updateOrderUpmp(OrderUpmp od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            int sum = session.update("jframe.pay.domain.dao.mapper.OrderMapper.updateOrderUpmp", od);
            session.commit();
            return sum;
        }
    }

    @Override
    public void insertOrderUpmp(OrderUpmp od) {
        try (SqlSession session = MultiMybatisSvc.getSqlSessionFactory(Environment.RUN).openSession()) {
            session.insert("jframe.pay.domain.dao.mapper.OrderMapper.insertOrderUpmp", od);
            session.commit();
        }
    }

}
