package CommonLayers;

import Basics.Matrix;
import Basics.Tensor;

import java.util.Random;

public class DropoutLayer implements Layer {
    public double dropoutRatio;
    public boolean isTraining;
    public Tensor mask;

    public DropoutLayer(double dropoutRatio){
        this.dropoutRatio = dropoutRatio;
        isTraining = true;
    }

    public Tensor forward(Tensor x){
        if(isTraining){
            Random rand = new Random();
            mask = new Matrix(x.shape());
            mask = mask.forEach(c -> rand.nextDouble());
            mask = mask.forEach(c -> (c > dropoutRatio ? 1.0 : 0));
            return x.mul(mask);
        }
        else
            return x.scale(1.0 - dropoutRatio);
    }

    public Tensor backward(Tensor dout){
        return dout.mul(mask);
    }

    public void mutate(Object nextD){
        DropoutLayer d = (DropoutLayer)nextD;
        dropoutRatio = d.dropoutRatio;
        isTraining = d.isTraining;
        mask.mutate(d.mask);
    }
}
