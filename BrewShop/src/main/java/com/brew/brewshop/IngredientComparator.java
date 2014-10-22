package com.brew.brewshop;

import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Yeast;

import java.util.Comparator;

public class IngredientComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        int result = 0;
        if (o1.getClass() != o2.getClass()) {
            return compareDifferent(o1, o2);
        } else if (o1 instanceof MaltAddition) {
            result = compareMalts((MaltAddition) o1, (MaltAddition) o2);
        } else if (o1 instanceof HopAddition) {
            result = compareHops((HopAddition) o1, (HopAddition) o2);
        } else if (o1 instanceof Yeast) {
            result = compareYeast((Yeast) o1, (Yeast) o2);
        }
        return result;
    }

    private int compareMalts(MaltAddition o1, MaltAddition o2) {
        int result;
        if (o1.getWeight().getOunces() != o2.getWeight().getOunces()) {
            if (o1.getWeight().getOunces() > o2.getWeight().getOunces()) {
                result = -1;
            } else {
                result = 1;
            }
        } else {
            result = o1.getMalt().getName().compareToIgnoreCase(o2.getMalt().getName());
        }
        return result;
    }

    private int compareHops(HopAddition o1, HopAddition o2) {
        int result;
        if (o1.getTime() != o2.getTime()) {
            if (o1.getTime() > o2.getTime()) {
                result = 1;
            } else {
                result = -1;
            }
        } else {
            result = o1.getHop().getName().compareToIgnoreCase(o2.getHop().getName());
        }
        return result;
    }

    private int compareYeast(Yeast o1, Yeast o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }

    private int compareDifferent(Object o1, Object o2) {
        int result = 0;
        if (o1 instanceof MaltAddition) {
            result = -1;
        } else if (o1 instanceof HopAddition) {
            if (o2 instanceof MaltAddition) {
                result =  1;
            } else {
                result =  -1;
            }
        } else if (o1 instanceof Yeast) {
            result =  1;
        }
        return result;
    }
}
