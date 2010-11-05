/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grt192.core;

/**
 *
 * @author student
 */
public class Pair<A,B> {
    private A a;
    private B b;
    public Pair(A a,B b) {
        this.a = a;
        this.b = b;
    }
    public A getFirst() {
        return a;
    }
    public B getSecond() {
        return b;
    }
}
