package com.goodo.app.javabean;
import java.util.List;

/**
 * Created by ZHUKE on 2015/10/27.
 */
public class Goodo {

    private String KeywordType;
    private String Keyword;
    private String Unit_ID;
    private String Page;
    private String PageSize;
    private String RecordCount;
    private String PageCount;
    private List<Message> R;

    public String getKeywordType() {
        return KeywordType;
    }

    public void setKeywordType(String keywordType) {
        KeywordType = keywordType;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public String getUnit_ID() {
        return Unit_ID;
    }

    public void setUnit_ID(String unit_ID) {
        Unit_ID = unit_ID;
    }

    public String getPage() {
        return Page;
    }

    public void setPage(String page) {
        Page = page;
    }

    public String getPageSize() {
        return PageSize;
    }

    public void setPageSize(String pageSize) {
        PageSize = pageSize;
    }

    public String getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(String recordCount) {
        RecordCount = recordCount;
    }

    public String getPageCount() {
        return PageCount;
    }

    public void setPageCount(String pageCount) {
        PageCount = pageCount;
    }

    public List<Message> getR() {
        return R;
    }

    public void setR(List<Message> r) {
        R = r;
    }

    @Override
    public String toString() {
        return "Goodo{" +
                "KeywordType='" + KeywordType + '\'' +
                ", Keyword='" + Keyword + '\'' +
                ", Unit_ID='" + Unit_ID + '\'' +
                ", Page='" + Page + '\'' +
                ", PageSize='" + PageSize + '\'' +
                ", RecordCount='" + RecordCount + '\'' +
                ", PageCount='" + PageCount + '\'' +
                ", R=" + R +
                '}';
    }
}
