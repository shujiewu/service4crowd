package cn.edu.buaa.act.test.util;

import cn.edu.buaa.act.fastwash.data.Box;

public class IoU {
    private static double overlap(double x1, double w1, double x2, double w2)
    {
        double l1 = x1 - w1/2;
        double l2 = x2 - w2/2;
        double left = l1 > l2 ? l1 : l2;
        double r1 = x1 + w1/2;
        double r2 = x2 + w2/2;
        double right = r1 < r2 ? r1 : r2;
        return right - left;
    }

    private static double box_intersection(Box a, Box b)
    {
        double w = overlap(a.getX(), a.getW(), b.getX(), b.getW());
        double h = overlap(a.getY(), a.getH(), b.getY(), b.getH());
        if(w < 0 || h < 0) return 0;
        return w*h;
    }

    private static double box_union(Box a, Box b)
    {
        double i = box_intersection(a, b);
        return a.getW()*a.getH() + b.getW()*b.getH() - i;
    }
    public static double box_iou(Box a, Box b)
    {
        return box_intersection(a, b)/box_union(a, b);
    }
}
