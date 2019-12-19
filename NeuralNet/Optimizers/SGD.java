package Optimizers;

import Basics.*;

public class SGD extends Optimizer {
    public SGD(double learningRate){
        super(learningRate);
    }

    @Override
    public void update(Dictionary<Tensor> params, Dictionary<Tensor> grads){
        for(String key : params.keys())
            params.put(key, params.get(key).sub(grads.get(key).scale(learningRate)));
    }
}
