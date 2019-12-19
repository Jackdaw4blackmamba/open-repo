package ActivationLayers;

import Basics.*;

public class TanhLayer implements ActivationLayer {
    public Tensor y;

    public TanhLayer(){}

    public Tensor forward(Tensor x){
        y = x.forEach(c -> FunctionUtil.tanh(c));
        return y;
    }

    public Tensor backward(Tensor dout){
        Tensor x = new Matrix(dout.shape());
        for(int i = 0; i < x.shape().shapes[0]; i++)
            for(int j = 0; j < x.shape().shapes[1]; j++)
                x.set(dout.get(i, j) * (1.0 - y.get(i, j) * y.get(i, j)), i, j);
        return x;
    }

    public void mutate(Object nextT){
        TanhLayer t = (TanhLayer)nextT;
        y.mutate(t.y);
    }
}
