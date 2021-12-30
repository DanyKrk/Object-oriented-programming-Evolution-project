package agh.ics.oop;

import java.util.Arrays;

public class GenotypeMapKey {
    private int[] genotype;

    public GenotypeMapKey(int[] genotype){
        this.genotype = genotype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenotypeMapKey that = (GenotypeMapKey) o;
        return Arrays.equals(genotype, that.genotype);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genotype);
    }

    public int[] getGenotype() {
        return this.genotype;
    }
}
