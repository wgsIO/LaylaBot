package dev.walk.gs.layla.utils;

import java.io.Serializable;

public class ThreeValue<A, B, C> implements Cloneable, Serializable {


    private final A one;
    private final B two;
    private final C three;

    public ThreeValue(A one, B two, C three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public A getOne() {
        return one;
    }

    public B getTwo() {
        return two;
    }

    public C getThree() {
        return three;
    }

    public boolean oneIsNull() {
        return getOne() == null;
    }

    public boolean twoIsNull() {
        return getTwo() == null;
    }

    public boolean threeIsNull() {
        return getTwo() == null;
    }

    public boolean isEmpty() {
        return oneIsNull() && twoIsNull() && threeIsNull();
    }

    public ThreeValue<A, B, C> valueOf(A one, B two, C three) {
        return new ThreeValue(one, two, three);
    }

    public Object[] toArray() {
        return new Object[]{one, two};
    }

    public boolean equals(Object object) {
        if (object instanceof ThreeValue) {
            ThreeValue<?, ?, ?> mv = (ThreeValue) object;
            return compareobject(new Object[]{
                    getOne(), mv.getOne(),
                    getTwo(), mv.getTwo(),
                    getThree(), mv.getThree(),
            });
        } else {
            return false;
        }
    }

    public ThreeValue<A, B, C> clone() {
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
