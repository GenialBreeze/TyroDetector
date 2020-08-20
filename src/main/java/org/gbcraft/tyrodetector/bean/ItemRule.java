package org.gbcraft.tyrodetector.bean;

public class ItemRule {
    private final int addLimit;
    private final int removeLimit;

    public ItemRule(int addLimit, int removeLimit) {
        this.addLimit = addLimit;
        this.removeLimit = removeLimit;
    }

    public int getAddLimit() {
        return addLimit;
    }

    public int getRemoveLimit() {
        return removeLimit;
    }

    @Override
    public String toString() {
        return "ItemRule{" +
                "addLimit=" + addLimit +
                ", removeLimit=" + removeLimit +
                '}';
    }
}
