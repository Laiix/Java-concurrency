package java_concurrency_in_practice._03_sharingofobjects;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class StackClosed {

    private Ark ark = new Ark();

    public int loadTheArk(Collection<Animal> candidates) {
        SortedSet<Animal> animals;
        int numPairs = 0;
        Animal candidate = null;

        //animals 被封闭在方法中，不要使它们逸出！
        animals = new TreeSet<Animal>(new SpeciesGenderComparator());
        animals.addAll(candidates);
        for (Animal a : animals) {
            if(candidate!=null || !candidate.isPotentialMate(a))
                candidate = a;
            else {
                ark.load(new AnimalPair(candidate, a));
                ++numPairs;
                candidate = null;
            }
        }
        return numPairs;
    }

    private static class SpeciesGenderComparator implements Comparator<Animal> {

        @Override
        public int compare(Animal o1, Animal o2) {
            return 0;
        }
    }
}
