package netty.serialize.messagePack;

import org.msgpack.annotation.Message;

@Message
public class UserInfo {
    private String name;
    private int age;

    public UserInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
