package Basics;

public class Shape {
    public int[] shapes;
    public int metaShape;

    public Shape(int... shapes){
        this.shapes = shapes;
        metaShape = shapes.length;
    }

    public boolean equals(Shape s){
        if(metaShape != s.metaShape)
            return false;
        for(int i = 0; i < metaShape; i++)
            if(shapes[i] != s.shapes[i])
                return false;
        return true;
    }

    public Shape clone(){
        return new Shape(shapes);
    }

    public void print(){
        System.out.println("Meta: " + metaShape);
        System.out.print(shapes[0]);
        for(int i = 1; i < metaShape; i++)
            System.out.print(", " + shapes[i]);
        System.out.println();
    }
}
