package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

//   线性表的应用：多项式的表示及运算
//   一元多项式的表示及运算
//  一元多项式的排序单链表实现

public interface Addible<T>                      //可相加接口，T表示数据元素的数据类型
{
    void add(T t);                        //+=加法，约定两元素相加规则
    boolean removable();                  //约定删除元素条件
}
