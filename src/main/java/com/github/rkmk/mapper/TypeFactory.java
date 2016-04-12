package com.github.rkmk.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeFactory<T> {

     Class<? extends T> getType(Class<T> type, ResultSet rs, int index) throws SQLException;
}
