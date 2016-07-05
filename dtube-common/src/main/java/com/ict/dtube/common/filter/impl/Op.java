package com.ict.dtube.common.filter.impl;

/**
 * @auther lansheng.zj@ict.com
 */
public abstract class Op {

    private String symbol;


    protected Op(String symbol) {
        this.symbol = symbol;
    }


    public String getSymbol() {
        return symbol;
    }


    public String toString() {
        return symbol;
    }
}
