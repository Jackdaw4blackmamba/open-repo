package Optimizers;

import Basics.*;

public class Nesterov extends Optimizer {
    private double momentum;
    private Dictionary<Tensor> v;

    public Nesterov(double learningRate){
        super(learningRate);
        momentum = 0.9;
        v = null;
    }

    @Override
    public void update(Dictionary<Tensor> params, Dictionary<Tensor> grads){
        if(v == null){
            v = new Dictionary<>();
            for(String  key : params.keys())
                v.put(key, TensorUtil.getInstanceFilledWith(0, params.get(key).shape()));
        }
        for(String key : params.keys()){
            v.put(key, v.get(key).scale(momentum));
            v.put(key, v.get(key).sub(grads.get(key).scale(learningRate)));
            params.put(key, params.get(key).add(v.get(key).scale(momentum * momentum)));
            params.put(key, params.get(key).sub(grads.get(key).scale((1.0 + momentum) * learningRate)));
        }
    }
}
