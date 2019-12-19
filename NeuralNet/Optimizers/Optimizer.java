package Optimizers;

import Basics.*;

public abstract class Optimizer {
    protected double learningRate;

    public Optimizer(double learningRate){
        this.learningRate = learningRate;
    }

    public abstract void update(Dictionary<Tensor> params, Dictionary<Tensor> grads);
}
