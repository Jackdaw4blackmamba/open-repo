package ActivationLayers;

import Basics.*;

public class ReLULayer implements ActivationLayer {
    private Tensor mask;

    public ReLULayer(){}

    public Tensor forward(Tensor x){
        mask = new Matrix(x.shape());
        for(int i = 0; i < x.shape().shapes[0]; i++)
            for(int j = 0; j < x.shape().shapes[1]; j++)
                mask = mask.forEach(c -> (c <= 0 ? 0 : c));
        return x.mul(mask);
    }

    public Tensor backward(Tensor dout){
        return dout.mul(mask);
    }

    public void mutate(Object nextR){
        ReLULayer r = (ReLULayer)nextR;
        mask.mutate(r.mask);
    }
}
