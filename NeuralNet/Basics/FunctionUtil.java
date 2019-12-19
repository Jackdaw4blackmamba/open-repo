package Basics;

public class FunctionUtil {
    public static double sigmoid(double x){
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double tanh(double x){
        return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
    }

    public static Tensor softmax(Tensor x){
        double sum;
        double max;
        Tensor y;
        max = TensorUtil.max(x);
        sum = TensorUtil.sum(x.forEach(c -> Math.exp(c - max)));
        y = x.forEach(c -> Math.exp(c - max) / sum);
        return y;
    }

    public static double crossEntropyError(Tensor y, Tensor t){
        int batchSize = y.shape().shapes[0];
        Tensor tmp = new Vector(batchSize);
        for(int i = 0; i < batchSize; i++){
            Tensor yv = TensorUtil.row(y, i);
            Tensor tv = TensorUtil.row(t, i);
            tmp.set(TensorUtil.sum(tv.mul(yv.forEach(c -> Math.log(c)))), i);
        }
        return -TensorUtil.sum(tmp) / batchSize;
    }
}
