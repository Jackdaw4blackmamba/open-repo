package Optimizers;

import Basics.*;

public class RMSprop extends Optimizer {
    private double decayRate;
    private Dictionary<Tensor> h;

    public RMSprop(double learningRate){
        super(learningRate);
        decayRate = 0.99;
        h = null;
    }

    @Override
    public void update(Dictionary<Tensor> params, Dictionary<Tensor> grads){
        if(h == null) {
            h = new Dictionary<>();
            for (String key : params.keys())
                h.put(key, TensorUtil.getInstanceFilledWith(0, params.get(key).shape()));
        }

        for(String key : params.keys()){
            h.put(key, h.get(key).scale(decayRate));
            h.put(key, h.get(key).add(grads.get(key).pow(2).scale(1.0 - decayRate)));
            params.put(key, params.get(key).sub(grads.get(key).scale(learningRate).div(h.get(key).forEach(c -> Math.sqrt(c) + 1e-7))));
        }
    }
}
