package LastLayers;

import Basics.Tensor;

public interface LastLayer {
    double forward(Tensor x, Tensor t);
    Tensor backward(double dout);
}
