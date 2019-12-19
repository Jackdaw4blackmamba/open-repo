package Basics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dictionary<V extends Mutable> {
    private HashMap<String, V> map;
    private List<String> keyList;

    public Dictionary(){
        map = new HashMap<>();
        keyList = new ArrayList<>();
    }

    public void put(String key, V value){
        if(map.get(key) == null)
            map.put(key, value);
        else
            map.get(key).mutate(value);
        if(!keyList.contains(key))
            keyList.add(key);
    }

    public V get(String key){
        if(map.get(key) == null)
            System.out.println("NULL: " + key);
        return map.get(key);
    }

    public List<String> keys(){
        return new ArrayList<>(map.keySet());
    }

    public List<V> values(){
        return new ArrayList<>(map.values());
    }

    public List<String> orderedKeys(){
        return keyList;
    }

    public List<V> orderedValues(){
        List<V> list = new ArrayList<>();
        for(int i = 0; i < keyList.size(); i++)
            list.add(get(keyList.get(i)));
        return list;
    }

    public void print(){
        for(String key : keys()) {
            System.out.println(key);
            V v = get(key);
            if(v instanceof Tensor) {
                    System.out.print("Shape: ");
                    ((Tensor) v).shape().print();
                    ((Tensor) v).print();
                }
        }
        System.out.println();
    }
}
