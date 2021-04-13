package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

// 稀疏矩阵的压缩存储
//1.  表示稀疏矩阵非零元素的三元组

//   图的邻接矩阵表示和实现

//稀疏矩阵非零元素三元组类，图带权值的边类
public class Triple implements Comparable<Triple>, Addible<Triple> {
    int row, column, value;                                //行号、列号、元素值，默认访问权限
    //行号（边的起点序号），列号（终点序号）、元素值（权值）

    //构造方法，参数指定行号、列号、元素值。若行号、列号为负，则抛出无效参数异常
    public Triple(int row, int column, int value) {
        if (row >= 0 && column >= 0) {
            this.row = row;
            this.column = column;
            this.value = value;
        } else {
            throw new IllegalArgumentException("行、列号不能为负数：row=" + row + "，column=" + column);
        }
    }

    public Triple(Triple tri)                             //拷贝构造方法，复制一个三元组
    {
        this(tri.row, tri.column, tri.value);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString()                               //返回三元组描述字符串
    {
        return "(" + row + "," + column + "," + value + ")";
    }

    // 转置矩阵用
    public Triple toSymmetry()                            //返回矩阵对称位置元素的三元组。
    {
        return new Triple(this.column, this.row, this.value);
    }

    @Override
    public boolean equals(Object obj)                      //比较两个三元组是否相等，比较位置和元素值
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Triple)) {
            return false;
        }
        Triple tri = (Triple) obj;
        return this.row == tri.row && this.column == tri.column && this.value == tri.value;
    }

    //根据行、列位置比较三元组对象大小，与元素值无关，约定三元组排序次序
    @Override
    public int compareTo(Triple tri) {
        if (this.row == tri.row && this.column == tri.column) {
            return 0;                                      //相等，与equals()方法含义不同
        }
        return (this.row < tri.row || this.row == tri.row && this.column < tri.column) ? -1 : 1;
    }

    @Override
    public void add(Triple term)                           //加法（＋=运算），实现Addible<T>接口
    {
        if (this.compareTo(term) == 0) {
            this.value += term.value;
        } else {
            throw new IllegalArgumentException("两项的指数不同，不能相加。");
        }
    }

    @Override
    public boolean removable()                             //约定删除元素条件，实现Addible<T>接口
    {
        return this.value == 0;                              //不存储值为0的元素
    }
}

/*没有用到
    public Triple plus(Triple term)                 //加法，重载运算符＋
    {
    	Triple tmp = new Triple(this);             //拷贝构造方法
        tmp.add(term);
    	return tmp;
    } *
 * 没有
    public Triple()
    {
        this(10,10,0);
    }
 */
