package CommonLayers;

import Basics.*;

public class BatchNormalization implements Layer {
    public Tensor gamma;
    public Tensor beta;
    public double momentum;
    public Shape inputShape; // Conv: 4, Fully: 2
    // For testing
    public Tensor runningMean;
    public Tensor runningVar;
    // For backward
    public int batchSize;
    public Tensor xc;
    public Tensor xn;
    public Tensor std;
    public Tensor dgamma;
    public Tensor dbeta;

    public boolean isTraining;

    public BatchNormalization(Tensor gamma, Tensor beta){
        this.gamma = gamma;
        this.beta = beta;
        this.momentum = 0.9;
        this.runningMean = null;
        this.runningVar = null;
        isTraining = true;
    }

    public Tensor forward(Tensor x){
        inputShape = x.shape();
        return __forward(x, isTraining);
    }

    private Tensor __forward(Tensor x, boolean isTraining){
        if(runningMean == null){
            runningMean = Vector.getInstanceFilledWith(0, x.shape().shapes[1]);
            runningVar = Vector.getInstanceFilledWith(0, x.shape().shapes[1]);
        }

        Tensor xn;
        if(isTraining){
            Tensor mu = new Vector(x.shape().shapes[1]);
            for(int i = 0; i < mu.shape().shapes[0]; i++){
                Tensor col = TensorUtil.col(x, i);
                mu.set(TensorUtil.sum(col) / col.shape().shapes[0], i);
            }
            Tensor xc = x.sub(mu);
            Tensor tmp = xc.pow(2);
            Tensor var = new Vector(tmp.shape().shapes[1]);
            for(int i = 0; i < var.shape().shapes[0]; i++){
                Tensor col = TensorUtil.col(tmp, i);
                var.set(TensorUtil.sum(col) / col.shape().shapes[0], i);
            }
            Tensor std = var.forEach(c -> Math.sqrt(c) + 10e-7);
            xn = xc.sub(std);

            batchSize = x.shape().shapes[0];
            this.xc = xc;
            this.xn = xn;
            this.std = std;
            for(int i = 0; i < runningMean.shape().shapes[0]; i++)
                runningMean.set(momentum * runningMean.get(i) + (1 - momentum) * mu.get(i), i);
            for(int i = 0; i < runningVar.shape().shapes[0]; i++)
                runningVar.set(momentum * runningVar.get(i) + (1 - momentum) * var.get(i), i);
        }
        else{
            Tensor xc = x.sub(runningMean);
            xn = new Matrix(xc.shape());
            for(int i = 0; i < xn.shape().shapes[0]; i++)
                for(int j = 0; j < xn.shape().shapes[1]; j++)
                    xn.set(xc.get(i, j) / (Math.sqrt(runningVar.get(j) + 10e-7)), i, j);
        }

        Tensor out = new Matrix(xn.shape());
        for(int i = 0; i < out.shape().shapes[0]; i++)
            for(int j = 0; j < out.shape().shapes[1]; j++)
                out.set(gamma.get(j) * xn.get(i, j) + beta.get(j), i, j);
        return out;
    }

    public Tensor backward(Tensor dout){
        return __backward(dout);
    }

    private Tensor __backward(Tensor dout){
        Tensor dbeta = new Vector(dout.shape().shapes[1]);
        for(int i = 0; i < dbeta.shape().shapes[0]; i++)
            dbeta.set(TensorUtil.sum(TensorUtil.col(dout, i)), i);
        Tensor dgamma = new Vector(xn.shape().shapes[1]);
        for(int i = 0; i < dgamma.shape().shapes[0]; i++)
            dgamma.set(TensorUtil.sum(TensorUtil.col(xn.mul(dout), i)), i);
        Tensor dxn = new Matrix(dout.shape());
        for(int i = 0; i < dxn.shape().shapes[0]; i++)
            for(int j = 0; j < dxn.shape().shapes[1]; j++)
                dxn.set(gamma.get(j) * dout.get(i, j), i, j);
        Tensor dxc = new Matrix(dxn.shape());
        for(int i = 0; i < dxc.shape().shapes[0]; i++)
            for(int j = 0; j < dxc.shape().shapes[1]; j++)
                dxc.set(dxn.get(i, j) / std.get(j), i, j);
        Tensor tmp = dxn.mul(xc).div(std.pow(2));
        Vector dstd = new Vector(std.shape().shapes[0]);
        for(int i = 0; i < dstd.shape().shapes[0]; i++)
            dstd.set(-TensorUtil.sum(TensorUtil.col(tmp, i)), i);
        Tensor dvar = dstd.scale(0.5).div(std);
        dxc = dxc.add(xc.mul(dvar).scale(2.0 / batchSize));
        Tensor dmu = new Vector(dxc.shape().shapes[1]);
        for(int i = 0; i < dmu.shape().shapes[0]; i++)
            dmu.set(TensorUtil.sum(TensorUtil.col(dxc, i)), i);
        Tensor dx = dxc.sub(dmu).scale(1.0 / batchSize);

        this.dgamma = dgamma;
        this.dbeta = dbeta;

        return dx;
    }

    public void mutate(Object nextB){
        BatchNormalization b = (BatchNormalization)nextB;
        gamma.mutate(b.gamma);
        beta.mutate(b.beta);
        momentum = b.momentum;
        inputShape = b.inputShape.clone();
        runningMean.mutate(b.runningMean);
        runningVar.mutate(b.runningVar);
        batchSize = b.batchSize;
        xc.mutate(b.xc);
        xn.mutate(b.xn);
        std.mutate(b.std);
        dgamma.mutate(b.dgamma);
        dbeta.mutate(b.dbeta);
        isTraining = b.isTraining;
    }
}
