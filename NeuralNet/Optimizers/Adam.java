package Optimizers;

import Basics.*;

public class Adam extends Optimizer {
    private double beta1;
    private double beta2;
    private int iter;
    private Dictionary<Tensor> m;
    private Dictionary<Tensor> v;

    public Adam(double learningRate){
        super(learningRate);
        beta1 = 0.9;
        beta2 = 0.999;
        iter = 0;
        m = null;
        v = null;
    }

    @Override
    public void update(Dictionary<Tensor> params, Dictionary<Tensor> grads){
        double lr_t;

        if(m == null){
            m = new Dictionary<>();
            v = new Dictionary<>();
            for(String key : params.keys()){
                m.put(key, TensorUtil.getInstanceFilledWith(0, params.get(key).shape()));
                v.put(key, TensorUtil.getInstanceFilledWith(0, params.get(key).shape()));
            }
        }

        iter += 1;
        lr_t = learningRate * Math.sqrt(1.0 - Math.pow(beta2, iter)) / (1.0 - Math.pow(beta1, iter));

        for(String key : params.keys()){
            m.put(key, m.get(key).add(grads.get(key).sub(m.get(key)).scale(1.0 - beta1)));
            v.put(key, v.get(key).add(grads.get(key).pow(2).sub(v.get(key)).scale(1.0 - beta2)));
            params.put(key, params.get(key).sub(m.get(key).scale(lr_t).div(v.get(key).forEach(c -> Math.sqrt(c) + 1e-7))));
        }
    }
}
