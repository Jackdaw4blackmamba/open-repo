package LastLayers;

import Basics.*;

public class SoftmaxWithLossLayer implements LastLayer {
    private Tensor y;
    private Tensor t;

    public SoftmaxWithLossLayer(){}

    public double forward(Tensor x, Tensor t){
        y = new Matrix(x.shape());
        for(int i = 0; i < y.shape().shapes[0]; i++){
            Tensor row = TensorUtil.row(x, i);
            row = FunctionUtil.softmax(row);
            for(int j = 0; j < row.shape().shapes[0]; j++)
                y.set(row.get(j), i, j);
        }
        this.t = t;

        return FunctionUtil.crossEntropyError(y, t);
    }

    public Tensor backward(double dout){
        return y.sub(t);
    }
}
