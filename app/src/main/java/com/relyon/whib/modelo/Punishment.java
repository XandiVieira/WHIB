package com.relyon.whib.modelo;

public class Punishment {

    private int reportsAfterLastBlock;
    private boolean blocked;
    private Long endDate;

    public Punishment() {
    }

    public Punishment(int reportsAfterLastBlock, boolean blocked, Long endDate) {
        this.reportsAfterLastBlock = reportsAfterLastBlock;
        this.blocked = blocked;
        this.endDate = endDate;
    }

    public int getReportsAfterLastBlock() {
        return reportsAfterLastBlock;
    }

    public void setReportsAfterLastBlock(int reportsAfterLastBlock) {
        this.reportsAfterLastBlock = reportsAfterLastBlock;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}