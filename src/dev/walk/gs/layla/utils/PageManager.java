package dev.walk.gs.layla.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageManager {

    int size;
    List list;

    public PageManager(int size, Object... values) {
        this.size = size;
        this.list = Arrays.asList(values);
    }

    public PageManager(List list, int size) {
        this.size = size;
        this.list = list;
    }

    public int getTotalPages() {
        double v = ((double) list.size() / (double) size);
        return (list.size() / size) == 0 ? 1 : (v >= Double.parseDouble(((int) v) + ".1") ? ((int) v) + 1 : (int) v);
    }

    public List getPage(int page) {
        if (page < 1) {
            page = 0;
        }
        int size = page == 0 ? 0 : (this.size * page) - this.size;
        List values = new ArrayList();
        try {
            int i = 0;
            while (i < this.size) {
                values.add(list.get(size + i));
                i++;
            }
        } catch (Exception e) {
        }
        return values;
    }


}
