package Basics;

import java.util.function.Function;

public interface Tensor extends Mutable {
    double get(int... idx);
    void   set(double val, int... idx);
    Tensor add(Tensor t);
    Tensor sub(Tensor t);
    Tensor mul(Tensor t);
    Tensor div(Tensor t);
    Tensor pow(double exp);
    Tensor scale(double s);
    Tensor forEach(Function<Double, Double> f);
    Shape  shape();

    void   print();
}
