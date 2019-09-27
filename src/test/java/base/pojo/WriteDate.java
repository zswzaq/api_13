package base.pojo;

/**
 * 写回数据
 * 
 * @author Administrator
 *
 */
public class WriteDate {
    private int rowNo;// 写回的行号
    private int columnNo;// 写回的列号
    private String data;// 写回的内容
    public int getRowNo() {
        return rowNo;
    }
    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }
    public int getColumnNo() {
        return columnNo;
    }
    public void setColumnNo(int columnNo) {
        this.columnNo = columnNo;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "writeDate [rowNo=" + rowNo + ", columnNo=" + columnNo + ", data=" + data + "]";
    }
    public WriteDate(int rowNo, int columnNo, String data) {
        this.rowNo = rowNo;
        this.columnNo = columnNo;
        this.data = data;
    }
    
}
