package model.entity;

import java.sql.Connection;
import java.sql.Statement;

public abstract class DatabaseTables {
    protected Statement stmt;
    protected Connection conn;

    public DatabaseTables(Statement stmt, Connection conn) {
        this.stmt = stmt;
        this.conn = conn;
    }
}
