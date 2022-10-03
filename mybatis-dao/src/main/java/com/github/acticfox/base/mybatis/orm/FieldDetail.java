package com.github.acticfox.base.mybatis.orm;

import java.lang.reflect.Field;

import org.apache.ibatis.type.TypeHandler;

/**
 * 类 FieldDetail.java 的实现描述：TODO 类实现描述
 * 
 * @author fanyong.kfy 14-5-19 上午9:45
 */
public class FieldDetail {
    private final Field                        field;
    private final String                       columnName;
    private final boolean                      isColumn;
    private final boolean                      updateAble;
    private final boolean                      insertAble;
    private final boolean                      isPk;
    private final Class<? extends TypeHandler> typeHandler;

    public FieldDetail(Field field, String columnName, boolean updateAble, boolean insertAble, boolean isPk,
                       Class<? extends TypeHandler> typeHandler, boolean isColumn) {
        this.field = field;
        this.columnName = columnName;
        this.updateAble = updateAble;
        this.insertAble = insertAble;
        this.typeHandler = typeHandler;
        this.isPk = isPk;
        this.isColumn = isColumn;
    }

    public boolean isColumn() {
        return isColumn;
    }

    public Class<? extends TypeHandler> getTypeHandler() {
        return typeHandler;
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isUpdateAble() {
        return updateAble;
    }

    public boolean isInsertAble() {
        return insertAble;
    }

    public boolean isPk() {
        return isPk;
    }

    public String getFieldNameWithWrap() {
        return "#{" + getFieldName() + getTypeHandlerString() + "}";
    }

    public String getParamNameWithWrap(String paramName) {
        return "#{" + paramName + getTypeHandlerString() + "}";
    }

    public String getParamNameWithoutWrap(String paramName) {
        return "#{" + paramName + "}";
    }

    /**
     * 指定typeHandler
     * 
     * @return
     */
    private String getTypeHandlerString() {
        return null == typeHandler ? "" : ",typeHandler=" + typeHandler.getName();
    }

    static class Builder {
        private Field                        field;
        private String                       columnName;
        private boolean                      updateAble = true;
        private boolean                      insertAble = true;
        private boolean                      isPk       = false;
        private Class<? extends TypeHandler> typeHandler;
        private boolean                      isColumn   = true;

        public void field(Field field) {
            this.field = field;
        }

        public void columnName(String columnName) {
            this.columnName = columnName;
        }

        public void updateAble(boolean updateAble) {
            this.updateAble = updateAble;
        }

        public void insertAble(boolean insertAble) {
            this.insertAble = insertAble;
        }

        public void isPk(boolean isPk) {
            this.isPk = isPk;
        }

        public void typeHandler(Class<? extends TypeHandler> typeHandler) {
            this.typeHandler = typeHandler;
        }

        public void isColumn(boolean isColumn) {
            this.isColumn = isColumn;
        }

        public FieldDetail build() {
            return new FieldDetail(this.field, this.columnName, this.updateAble, this.insertAble, this.isPk,
                    this.typeHandler, this.isColumn);
        }
    }
}
