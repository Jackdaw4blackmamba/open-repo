package Basics;

public class TensorUtil {
    public static double max(Tensor t){
        double max = 0;
        if(t.shape().metaShape == 1){
            max = t.get(0);
            for(int i = 0; i < t.shape().shapes[0]; i++)
                if(max < t.get(i))
                    max = t.get(i);
        }
        else if(t.shape().metaShape == 2){
            max = t.get(0, 0);
            for(int i = 0; i < t.shape().shapes[0]; i++)
                for(int j = 0; j < t.shape().shapes[1]; j++)
                    if(max < t.get(i, j))
                        max = t.get(i, j);
        }
        return max;
    }

    public static double sum(Tensor t){
        double sum = 0;
        if(t.shape().metaShape == 1){
            for(int i = 0; i < t.shape().shapes[0]; i++)
                sum += t.get(i);
        }
        else if(t.shape().metaShape == 2){
            for(int i = 0; i < t.shape().shapes[0]; i++)
                for(int j = 0; j < t.shape().shapes[1]; j++)
                    sum += t.get(i, j);
        }
        return sum;
    }

    public static Tensor dot(Tensor t1, Tensor t2){
        Tensor t;
        if(t1.shape().metaShape == 1 && t2.shape().metaShape == 1){
            // Vector dot Vector
            t = t1.mul(t2);
        }
        else if(t1.shape().metaShape == 1 && t2.shape().metaShape == 2){
            // Vector dot Matrix
            t = new Vector(t2.shape().shapes[1]);
            for(int i = 0; i < t.shape().shapes[0]; i++){
                Tensor col = TensorUtil.col(t2, i);
                t.set(TensorUtil.sum(t1.mul(col)), i);
            }
        }
        else if(t1.shape().metaShape == 2 && t2.shape().metaShape == 1){
            // Matrix dot Vector
            t = new Vector(t1.shape().shapes[0]);
            for(int i = 0; i < t.shape().shapes[0]; i++){
                Tensor row = TensorUtil.row(t1, i);
                t.set(TensorUtil.sum(row.mul(t2)), i);
            }
        }
        else{
            // Matrix dot Matrix
            t = new Matrix(t1.shape().shapes[0], t2.shape().shapes[1]);
            for(int i = 0; i < t.shape().shapes[0]; i++){
                Tensor row = TensorUtil.row(t1, i);
                for(int j = 0; j < t.shape().shapes[1]; j++){
                    Tensor col = TensorUtil.col(t2, j);
                    t.set(TensorUtil.sum(row.mul(col)), i, j);
                }
            }
        }
        return t;
    }

    public static Tensor transpose(Tensor t){
        Tensor nt = new Matrix(t.shape().shapes[1], t.shape().shapes[0]);
        for(int i = 0; i < t.shape().shapes[0]; i++){
            Tensor row = row(t, i);
            for(int j = 0; j < nt.shape().shapes[0]; j++)
                nt.set(row.get(j), j, i);
        }
        return nt;
    }

    public static Tensor row(Tensor t, int idx){
        if(t.shape().metaShape == 1)
            return t;
        Tensor nt = new Vector(t.shape().shapes[1]);
        for(int i = 0; i < nt.shape().shapes[0]; i++)
            nt.set(t.get(idx, i), i);
        return nt;
    }

    public static Tensor col(Tensor t, int idx){
        if(t.shape().metaShape == 1)
            return t;
        Tensor nt = new Vector(t.shape().shapes[0]);
        for(int i = 0; i < nt.shape().shapes[0]; i++)
            nt.set(t.get(i, idx), i);
        return nt;
    }

    public static Tensor getInstanceFilledWith(double val, Shape shape){
        Tensor nt = null;
        if(shape.metaShape == 1)
            nt = new Vector(shape);
        else if(shape.metaShape == 2)
            nt = new Matrix(shape);
        nt = nt.forEach(c -> val);
        return nt;
    }
}
