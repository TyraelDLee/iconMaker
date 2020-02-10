package defaultPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Excution {
    private final static String packageIcon = "iconutil -c icns ";
    private final static String extractIcon = "iconutil -c iconset ";

    public void execute(String address, boolean packaging) {
        if (packaging)
            address = addressCheck(address);
        else
            address = nameCheck(address);
        BufferedReader br = null;
        Process excuter;
        try {
            if (packaging)
                excuter = Runtime.getRuntime().exec(packageIcon + address);
            else
                excuter = Runtime.getRuntime().exec(extractIcon + address);
            br = new BufferedReader(new InputStreamReader(excuter.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
                sb.append(line + "\n");
            System.out.println(sb);
            excuter.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String addressCheck(String address) {
        if (address.length() < 8 || !address.substring(address.length() - 8).equals(".iconset")){
            File file = new File(address);
            address += ".iconset";
            file.renameTo(new File(address));
        }
        System.out.println(address);
        return address;
    }

    private String nameCheck(String name) {
        if (name.length() < 5 || !name.substring(name.length() - 5).equals(".icns")){
            File file = new File(name);
            name += ".icns";
            file.renameTo(new File(name));
        }
        System.out.println(name);
        return name;
    }

    public boolean autoRegconize(String address){
        if(address.length()>5 && address.substring(address.length()-5).equals(".icns")){
            execute(address,false);
            return true;
        }
        else if(address.length() > 8 && address.substring(address.length() - 8).equals(".iconset")){
            execute(address, true);
            return true;
        }
        else return false;
    }

    public static void main(String[] args) {
        Excution excution = new Excution();
        excution.execute("/Users/tyraellee/Desktop/compass.iconset",true);
    }
}
