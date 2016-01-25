package format;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jingcai.apps.common.lang.format.JsonMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lejing on 15/8/11.
 */
public class JsonMapperTest {

    @Test
    public void test_ALWAYS() {
        JsonMapper jsonMapper = new JsonMapper(JsonInclude.Include.ALWAYS);
        Obj object = new Obj();
        object.setObj2(new Obj1());
        object.setList(new ArrayList<Obj1>());
        System.out.println("ALWAYS--------------" + jsonMapper.toJson(object));
        //{"str":"a","str1":"","int0":0,"obj1":"","obj2":{"str":""},"list":[]}
    }

    @Test
    public void test_NON_DEFAULT() {
        JsonMapper jsonMapper = new JsonMapper(JsonInclude.Include.NON_DEFAULT);
        Obj object = new Obj();
        object.setObj2(new Obj1());
        object.setList(new ArrayList<Obj1>());
        System.out.println("NON_DEFAULT--------------" + jsonMapper.toJson(object));
        //{"obj2":{},"list":[]}
    }

    @Test
    public void test_NON_EMPTY() {
        JsonMapper jsonMapper = new JsonMapper(JsonInclude.Include.NON_EMPTY);
        Obj object = new Obj();
        object.setObj2(new Obj1());
        object.setList(new ArrayList<Obj1>());
        System.out.println("NON_EMPTY--------------" + jsonMapper.toJson(object));
        //{"str":"a","int0":0,"obj2":{}}
    }

    @Test
    public void test_NON_NULL() {
        JsonMapper jsonMapper = new JsonMapper(JsonInclude.Include.NON_NULL);
        Obj object = new Obj();
        object.setObj2(new Obj1());
        object.setList(new ArrayList<Obj1>());
        System.out.println("NON_NULL--------------" + jsonMapper.toJson(object));
        //{"str":"a","int0":0,"obj2":{},"list":[]}
    }
}

class Obj {
    private String str = "a";
    private String str1;
    private int int0;
    private Obj1 obj1;
    private Obj1 obj2;
    private List<Obj1> list;

    public Obj1 getObj2() {
        return obj2;
    }

    public void setObj2(Obj1 obj2) {
        this.obj2 = obj2;
    }

    public List<Obj1> getList() {
        return list;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public int getInt0() {
        return int0;
    }

    public void setInt0(int int0) {
        this.int0 = int0;
    }

    public void setList(List<Obj1> list) {
        this.list = list;
    }

    public Obj1 getObj1() {
        return obj1;
    }

    public void setObj1(Obj1 obj1) {
        this.obj1 = obj1;
    }
}

class Obj1 {
    private String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
