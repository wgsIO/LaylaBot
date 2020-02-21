package dev.walk.gs.layla.utils;

import java.io.Serializable;

public class MultiValue<A, B> implements Cloneable, Serializable {

    private final A one;
    private final B two;

    public MultiValue(A one, B two) {
        this.one = one;
        this.two = two;
    }

    public A getOne() {
        return one;
    }

    public B getTwo() {
        return two;
    }

    public boolean oneIsNull() {
        return getOne() == null;
    }

    public boolean twoIsNull() {
        return getTwo() == null;
    }

    public MultiValue<A, B> valueOf(A one, B two) {
        return new MultiValue(one, two);
    }

    public Object[] toArray() {
        return new Object[]{one, two};
    }

    public boolean equals(Object object) {
        if (object instanceof MultiValue) {
            MultiValue<?, ?> mv = (MultiValue) object;
            return compareobject(new Object[]{
                    getOne(), mv.getOne(),
                    getTwo(), mv.getTwo(),
            });
        } else {
            return false;
        }
    }

    public MultiValue<A, B> clone() {
        return this;
    }

    boolean compareobject(Object... objects) {
        if (objects != null) {
            throw new NullPointerException("objects");
        }
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException("objects length not even");
        }
        int index = 1;
        while (index < objects.length) {
            Object object1 = objects[(index - 1)];
            Object object2 = objects[index];
            if (!equals(object1, object2)) {
                return false;
            }
            index += 2;
        }
        return true;
    }

    boolean equals(Object ob1, Object ob2) {
        if (ob1 == null) {
            return ob2 == null;
        }
        if (ob2 == null) {
            return false;
        }
        return ob1.equals(ob2);
    }

}
