package com.example.entity;

import java.io.Serializable;

/**
 * 文章摘要实体类
 */
public class ArticleSummary implements Serializable {
    private Integer id;
    private String model;
    private String title;
    private String short1;
    private String short2;
    private String short3;
    private String short4;
    private String short5;
    private String short6;
    private String mid1;
    private String mid2;
    private String mid3;
    private String mid4;
    private String mid5;
    private String mid6;
    private String long1;
    private String long2;
    private String long3;
    private String long4;
    private String long5;
    private String long6;
    private String target;
    private String algmid1;
    private String algmid2;
    private String algmid3;
    private String algmid4;
    private String alglong1;
    private String alglong2;
    private String alglong3;
    private String alglong4;
    private String environment;
    private String tools;
    private String datas;
    private String standard;
    private String result;
    private String future;
    private String weekpoint;
    private String summary;
    private String keyword;
    private Integer ifteacher; // 新增字段

    // 现有 getter/setter 保持不变
    public Integer getIfteacher() {
        return ifteacher;
    }

    public void setIfteacher(Integer ifteacher) {
        this.ifteacher = ifteacher;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShort1() {
        return short1;
    }

    public void setShort1(String short1) {
        this.short1 = short1;
    }

    public String getShort2() {
        return short2;
    }

    public void setShort2(String short2) {
        this.short2 = short2;
    }

    public String getShort3() {
        return short3;
    }

    public void setShort3(String short3) {
        this.short3 = short3;
    }

    public String getShort4() {
        return short4;
    }

    public void setShort4(String short4) {
        this.short4 = short4;
    }

    public String getShort5() {
        return short5;
    }

    public void setShort5(String short5) {
        this.short5 = short5;
    }

    public String getShort6() {
        return short6;
    }

    public void setShort6(String short6) {
        this.short6 = short6;
    }

    public String getMid1() {
        return mid1;
    }

    public void setMid1(String mid1) {
        this.mid1 = mid1;
    }

    public String getMid2() {
        return mid2;
    }

    public void setMid2(String mid2) {
        this.mid2 = mid2;
    }

    public String getMid3() {
        return mid3;
    }

    public void setMid3(String mid3) {
        this.mid3 = mid3;
    }

    public String getMid4() {
        return mid4;
    }

    public void setMid4(String mid4) {
        this.mid4 = mid4;
    }

    public String getMid5() {
        return mid5;
    }

    public void setMid5(String mid5) {
        this.mid5 = mid5;
    }

    public String getMid6() {
        return mid6;
    }

    public void setMid6(String mid6) {
        this.mid6 = mid6;
    }

    public String getLong1() {
        return long1;
    }

    public void setLong1(String long1) {
        this.long1 = long1;
    }

    public String getLong2() {
        return long2;
    }

    public void setLong2(String long2) {
        this.long2 = long2;
    }

    public String getLong3() {
        return long3;
    }

    public void setLong3(String long3) {
        this.long3 = long3;
    }

    public String getLong4() {
        return long4;
    }

    public void setLong4(String long4) {
        this.long4 = long4;
    }

    public String getLong5() {
        return long5;
    }

    public void setLong5(String long5) {
        this.long5 = long5;
    }

    public String getLong6() {
        return long6;
    }

    public void setLong6(String long6) {
        this.long6 = long6;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAlgmid1() {
        return algmid1;
    }

    public void setAlgmid1(String algmid1) {
        this.algmid1 = algmid1;
    }

    public String getAlgmid2() {
        return algmid2;
    }

    public void setAlgmid2(String algmid2) {
        this.algmid2 = algmid2;
    }

    public String getAlgmid3() {
        return algmid3;
    }

    public void setAlgmid3(String algmid3) {
        this.algmid3 = algmid3;
    }

    public String getAlgmid4() {
        return algmid4;
    }

    public void setAlgmid4(String algmid4) {
        this.algmid4 = algmid4;
    }

    public String getAlglong1() {
        return alglong1;
    }

    public void setAlglong1(String alglong1) {
        this.alglong1 = alglong1;
    }

    public String getAlglong2() {
        return alglong2;
    }

    public void setAlglong2(String alglong2) {
        this.alglong2 = alglong2;
    }

    public String getAlglong3() {
        return alglong3;
    }

    public void setAlglong3(String alglong3) {
        this.alglong3 = alglong3;
    }

    public String getAlglong4() {
        return alglong4;
    }

    public void setAlglong4(String alglong4) {
        this.alglong4 = alglong4;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFuture() {
        return future;
    }

    public void setFuture(String future) {
        this.future = future;
    }

    public String getWeekpoint() {
        return weekpoint;
    }

    public void setWeekpoint(String weekpoint) {
        this.weekpoint = weekpoint;
    }
}