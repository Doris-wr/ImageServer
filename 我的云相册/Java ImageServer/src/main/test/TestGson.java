import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;

/**
 * @Author:wangrui
 * @Date:2020/5/1 0:04
 */
class Hero{
    public String name;
    public String skill1;
    public String skill2;
    public String skill3;
}
public class TestGson {
    public static void main(String[] args) {
        HashMap<String,Object>hashmap=new HashMap<>();
        hashmap.put("name","曹操");
        hashmap.put("skill1","剑气");
        hashmap.put("skill2","三段跳");
        hashmap.put("skill3","加攻击并吸血");
        hashmap.put("skill4","加攻速");
        Hero hero=new Hero();
        hero.name="曹操";
        hero.skill1="剑气";
        hero.skill2="三段跳";
        hero.skill3="加攻速并吸血";
        //通过map转成一个JSON结构的字符串
        //1.创建一个gson对象
        Gson gson=new GsonBuilder().create();
        //2.使用toGson方法把键值对结构转换成JSON字符串
        String str=gson.toJson(hashmap);
       // String str=gson.toJson(hero);
        System.out.println(str);
    }
}
