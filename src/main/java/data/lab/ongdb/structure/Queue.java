package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

//  队列抽象数据类型

//ADT Queue<T>                        //队列抽象数据类型，T表示数据元素的数据类型
//声明同java.util.Queue<T>

public interface Queue<T>              //队列接口，描述队列抽象数据类型，T表示数据元素的数据类型
{
    boolean isEmpty();           //判断队列是否空

    boolean add(T x);            //元素x入队，若添加成功，则返回true；否则返回false

    T peek();                    //返回队头元素，没有删除。若队列空，则返回null

    T poll();                    //出队，返回队头元素。若队列空，则返回null
}

