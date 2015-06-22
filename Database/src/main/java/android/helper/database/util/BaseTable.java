package android.helper.database.util;

import java.util.ArrayList;

/**
 * Created by lqnhu on 6/21/15.
 */
public class BaseTable {
    public int RowNumber;
    public static ArrayList<?> Select(Class<?> typeOfResult, int pageNumber, String where) {
        return Singleton.getInstance(DataBase.class).Select(typeOfResult, pageNumber, where);
    }
    public static <T> T Single(Class<T> typeOfResult, long valueOfPrimaryKey) {
        return Singleton.getInstance(DataBase.class).Single(typeOfResult, valueOfPrimaryKey);
    }
    public static <T> T SingleOrDefault(Class<T> typeOfResult, long valueOfPrimaryKey) {
        return Singleton.getInstance(DataBase.class).SingleOrDefault(typeOfResult, valueOfPrimaryKey);
    }
    public static <T> T Single(Class<T> typeOfResult, String where) {
        return Singleton.getInstance(DataBase.class).Single(typeOfResult, where);
    }
    public static <T> T SingleOrDefault(Class<T> typeOfResult, String where) {
        return Singleton.getInstance(DataBase.class).SingleOrDefault(typeOfResult, where);
    }
    public void Insert() {
        Singleton.getInstance(DataBase.class).Insert(this);
    }
    public void Update() {
        Singleton.getInstance(DataBase.class).Update(this);
    }
    public void Delete() {
        Singleton.getInstance(DataBase.class).Delete(this);
    }
    public String GetPrimaryKeyColumn() {
        return "RowNumber";
    }
}
