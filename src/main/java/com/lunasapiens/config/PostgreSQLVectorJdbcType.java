package com.lunasapiens.config;

import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.WrapperOptions;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
    vedi: package com.lunasapiens.entity.ArticleContent

    // Usiamo un tipo JDBC custom (PostgreSQLVectorJdbcType) perché Hibernate non supporta nativamente
    // il tipo 'vector' di PostgreSQL (pgvector). Questo JdbcType gestisce la conversione tra
    // Float[] in Java e il formato accettato da pgvector, permettendo di salvare e leggere correttamente
    // gli embeddings direttamente dal database.
 */
public class PostgreSQLVectorJdbcType implements JdbcType {

    @Override
    public int getJdbcTypeCode() {
        return java.sql.Types.OTHER;
    }

    @Override
    public <X> BasicBinder<X> getBinder(final JavaType<X> javaType) {
        return new BasicBinder<X>(javaType, this) {

            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                Float[] vector = (Float[]) value;
                if (vector == null) {
                    st.setObject(index, null, java.sql.Types.OTHER);
                    return;
                }
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < vector.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(vector[i]);
                }
                sb.append(']');
                st.setObject(index, sb.toString(), java.sql.Types.OTHER);
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                Float[] vector = (Float[]) value;
                if (vector == null) {
                    st.setObject(name, null, java.sql.Types.OTHER);
                    return;
                }
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < vector.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(vector[i]);
                }
                sb.append(']');
                st.setObject(name, sb.toString(), java.sql.Types.OTHER);
            }
        };
    }

    @Override
    public <X> BasicExtractor<X> getExtractor(final JavaType<X> javaType) {
        return new BasicExtractor<X>(javaType, this) {

            @Override
            protected X doExtract(ResultSet rs, int index, WrapperOptions options) throws SQLException {
                return parseVector(rs.getString(index), javaType);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return parseVector(statement.getString(index), javaType);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return parseVector(statement.getString(name), javaType);
            }

            private X parseVector(String vectorText, JavaType<X> javaType) {
                if (vectorText == null) {
                    return javaType.wrap(null, null);
                }
                vectorText = vectorText.replaceAll("[\\[\\]]", "").trim();
                if (vectorText.isEmpty()) {
                    return javaType.wrap(new Float[0], null);
                }
                String[] parts = vectorText.split(",");
                Float[] arr = new Float[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    arr[i] = Float.parseFloat(parts[i].trim());
                }
                return javaType.wrap(arr, null);
            }
        };
    }

}
