package akssu.mcstalker.setting;



public class Setting
{

    public String name;
    public boolean booleanValue;
    public int intValue;

    public Setting(String name, boolean value)
    {
        this.name = name;
        this.booleanValue = value;

    }


    public void set(boolean newVal)
    {
        this.booleanValue = newVal;
    }

    public void set(int newVal)
    {
        this.intValue = newVal;
    }

    public boolean get(){
        return booleanValue;
    }



}
