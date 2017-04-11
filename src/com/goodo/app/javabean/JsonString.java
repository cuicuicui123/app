package com.goodo.app.javabean;
/**
 * Created by ZHUKE on 2015/10/27.
 */
public class JsonString {
    private Goodo Goodo;

    public Goodo getGoodo() {
        return Goodo;
    }

    public void setGoodo(Goodo goodo) {
        this.Goodo = goodo;
    }

    @Override
    public String toString() {
        return "JsonString{" +
                "goodo=" + Goodo +
                '}';
    }
}
