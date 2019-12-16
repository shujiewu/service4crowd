package cn.edu.buaa.act.fastwash.estimate;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiLabel {
    int i, j, lij;

    public MultiLabel(int i, int j, int lij) {
        this.i = i;
        this.j = j;
        this.lij = lij;
    }

    public int delta(int k) {
        return k == lij ? 1 : 0;
    }
}

