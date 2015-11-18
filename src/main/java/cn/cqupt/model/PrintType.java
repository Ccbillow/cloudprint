package cn.cqupt.model;

/**
 * 打印类型
 * 0为word，1为pdf
 */
public enum PrintType {

    WORD(0),
    PDF(1);

    private int code;

    PrintType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
