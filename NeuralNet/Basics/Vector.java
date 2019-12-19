package Basics;

import java.util.function.Function;

public class Vector implements Tensor {
    private double[] vals;

    public Vector(int numVals){
        vals = new double[numVals];
    }

    public Vector(double[] vals){
        this.vals = vals;
    }

    public Vector(double val){vals = new double[]{val};}

    public Vector(Shape shape){
        vals = new double[shape.shapes[0]];
    }

    public double get(int... idx){
        return vals[idx[0]];
    }

    public void set(double val, int... idx){
        vals[idx[0]] = val;
    }

    public int length(){
        return vals.length;
    }

    public Tensor add(Tensor t){
        Tensor y = new Vector(t.shape());
        for(int i = 0; i < t.shape().shapes[0]; i++)
            y.set(get(i) + t.get(i), i);
        return y;
    }

    public Tensor sub(Tensor t){
        Tensor y = new Vector(t.shape());
        for(int i = 0; i < t.shape().shapes[0]; i++)
            y.set(get(i) - t.get(i), i);
        return y;
    }

    public Tensor mul(Tensor t){
        Tensor nt = new Vector(shape());
        for(int i = 0; i < t.shape().shapes[0]; i++)
            nt.set(get(i) * t.get(i), i);
        return nt;
    }

    public Tensor div(Tensor v){
        Tensor newV = new Vector(shape());
        for(int i = 0; i < v.shape().shapes[0]; i++)
            newV.set(get(i) / v.get(i), i);
        return newV;
    }

    public Tensor scale(double s){
        return forEach(c -> c * s);
    }

    public Tensor pow(double exp){
        Tensor t = new Vector(shape());
        for(int i = 0; i < t.shape().shapes[0]; i++)
            t.set(Math.pow(get(i), exp), i);
        return t;
    }

    public Tensor forEach(Function<Double, Double> f){
        Tensor t = new Vector(shape());
        for(int i = 0; i < t.shape().shapes[0]; i++)
            t.set(f.apply(get(i)), i);
        return t;
    }

    public void mutate(Object nextV){
        Vector v = (Vector)nextV;
        for(int i = 0; i < v.shape().shapes[0]; i++)
            set(v.get(i), i);
    }

    public void print(){
        for(int i = 0; i < vals.length; i++)
            System.out.print(vals[i] + " ");
        System.out.println();
    }

    public Shape shape(){
        return new Shape(length());
    }

    public static Vector getInstanceFilledWith(double value, int numVals){
        Vector v;
        v = new Vector(numVals);
        for(int i = 0; i < v.length(); i++)
            v.set(value, i);
        return v;
    }
}
