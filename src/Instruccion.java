public class Instruccion {
    private String nameOp;
    private String op1;
    private String op2;

    public Instruccion(String nameOp, String op1, String op2) {
        this.nameOp = nameOp.trim().stripIndent().strip().stripLeading().stripTrailing();
        this.op1 = op1;
        this.op2 = op2;
    }

    public String getnameOp() {
        return nameOp;
    }
    public void setCodOp(String codnameOp) {
        this.nameOp = codnameOp;
    }
    public String getOp1() {
        return op1;
    }
    public void setOp1(String op1) {
        this.op1 = op1;
    }
    public String getOp2() {
        return op2;
    }
    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public String toString() {
        return nameOp+ " " + op1 + " " + op2;
    }

}
