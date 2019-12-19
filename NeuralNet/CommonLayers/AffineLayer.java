package CommonLayers;

import Basics.Tensor;
import Basics.TensorUtil;
import Basics.Vector;

public class AffineLayer implements Layer {
    public Tensor W;
    public Tensor b;
    public Tensor dW;
    public Tensor db;
    public Tensor x;

    public AffineLayer(Tensor W, Tensor b){
        this.W = W;
        this.b = b;
    }

    public Tensor forward(Tensor x){
        this.x = x;

        return TensorUtil.dot(x, W).add(b);
    }

    public Tensor backward(Tensor dout){
        Tensor t = Vector.getInstanceFilledWith(0, dout.shape().shapes[1]);
        dW = TensorUtil.dot(TensorUtil.transpose(x), dout);
        for(int i = 0; i < t.shape().shapes[0]; i++)
            t.set(TensorUtil.sum(TensorUtil.col(dout, i)), i);
        db = t;
        return TensorUtil.dot(dout, TensorUtil.transpose(W));
    }

    public void mutate(Object nextL){
        AffineLayer l = (AffineLayer)nextL;
        W.mutate(l.W);
        b.mutate(l.b);
        dW.mutate(l.dW);
        db.mutate(l.db);
        x.mutate(l.x);
    }
}
