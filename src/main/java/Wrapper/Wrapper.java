package Wrapper;


import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.util.ArrayList;


public class Wrapper implements Serializable {
    private static final long serialVersionUID = 2901775676734480119L;
    private final int stage;
    private final ArrayList<Object> data;
    private final Object object;

    public int getStage() {
        return stage;
    }

    public ArrayList<Object> getData() {
        return data;
    }

    public Object getObject() {
        return object;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public Wrapper(int stage, @Nullable ArrayList<Object> data, @Nullable Object object) {
        this.stage = stage;
        this.data = data;
        this.object = object;
    }


}
