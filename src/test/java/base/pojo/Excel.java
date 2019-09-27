package base.pojo;

public abstract class Excel {
    // Excel行号
    private int rowNo;

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    @Override
    public String toString() {
        return "Excel [rowNo=" + rowNo + "]";
    }
    

}
