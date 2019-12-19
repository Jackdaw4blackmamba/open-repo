package CommonLayers;

import Basics.Mutable;
import Basics.Tensor;

public interface Layer extends Mutable {
    Tensor forward(Tensor x);
    Tensor backward(Tensor dout);
}
