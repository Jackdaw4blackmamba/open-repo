package Basics;

import java.util.function.Function;

public class Matrix implements Tensor {
    private double[] vals;
    private int numRows;
    private int numCols;

    public Matrix(int numRows, int numCols){
        this.numRows = numRows;
        this.numCols = numCols;
        vals = new double[numRows * numCols];
    }

    public Matrix(double[][] vals){
        numRows = vals.length;
        numCols = vals[0].length;
        double[] tmp = new double[numRows * numCols];
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++)
                tmp[i * numCols + j] = vals[i][j];
        this.vals = tmp;
    }

    public Matrix(Shape shape){
        if(shape.metaShape == 1){
            numRows = 1;
            numCols = shape.shapes[0];
        }
        else if(shape.metaShape == 2){
            numRows = shape.shapes[0];
            numCols = shape.shapes[1];
        }
        vals = new double[numRows * numCols];
    }

    public double get(int... idx){
        return vals[idx[0] * numCols + idx[1]];
    }

    public void set(double val, int... idx){
        vals[idx[0] * numCols + idx[1]] = val;
    }

    public Tensor add(Tensor t){
        Tensor nt = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++) {
                if(t.shape().metaShape == 1)
                    nt.set(get(i, j) + t.get(j), i, j);
                else if(t.shape().metaShape == 2)
                    nt.set(get(i, j) + t.get(i, j), i, j);
            }
        return nt;
    }

    public Tensor sub(Tensor t){
        Tensor nt = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++) {
                if(t.shape().metaShape == 1)
                    nt.set(get(i, j) - t.get(j), i, j);
                else if(t.shape().metaShape == 2)
                    nt.set(get(i, j) - t.get(i, j), i, j);
            }
        return nt;
    }

    public Tensor pow(double exp){
        Tensor m = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++)
                m.set(Math.pow(get(i, j), exp), i, j);
        return m;
    }

    public Tensor mul(Tensor t){
        Tensor nt = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++) {
                if(t.shape().metaShape == 1)
                    nt.set(get(i, j) * t.get(j), i, j);
                else if(t.shape().metaShape == 2)
                    nt.set(get(i, j) * t.get(i, j), i, j);
            }
        return nt;
    }

    public Tensor div(Tensor t){
        Tensor nt = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++) {
                if(t.shape().metaShape == 1)
                    nt.set(get(i, j) / t.get(j), i, j);
                else if(t.shape().metaShape == 2)
                    nt.set(get(i, j) / t.get(i, j), i, j);
            }
        return nt;
    }

    public Tensor scale(double s){
        return forEach(c -> c * s);
    }

    public Tensor forEach(Function<Double, Double> f){
        Tensor t = new Matrix(shape());
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++)
                t.set(f.apply(get(i, j)), i, j);
        return t;
    }

    public void mutate(Object nextM){
        Matrix m = (Matrix)nextM;
        for(int i = 0; i < numRows; i++)
            for(int j = 0; j < numCols; j++)
                set(m.get(i, j), i, j);
    }

    public static Matrix getInstanceFilledWithZeros(int rows, int cols){
        Matrix m = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                m.set(0, i, j);
        return m;
    }

    public static Matrix getInstanceFilledWithZeros(Shape shape){
        return getInstanceFilledWithZeros(shape.shapes[0], shape.shapes[1]);
    }

    public Shape shape(){
        return new Shape(numRows, numCols);
    }

    public void print(){
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++)
                System.out.print(get(i, j) + " ");
            System.out.println();
        }
    }
}
