package com.codeday.productivity.util;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class CustomIdGeneratorWrapper implements IdentifierGenerator {

    private final IdGenerator generator = new CustomIdGenerator();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generator.generateId();
    }
}
