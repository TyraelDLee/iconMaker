package defaultPackage;

import javafx.application.Platform;

import java.io.*;

public class Execution {
    private final static String packageIcon = "iconutil -c icns ";
    private final static String extractIcon = "iconutil -c iconset ";
    private Observer observer;

    public void execute(String address, boolean packaging) {
        if (packaging)
            address = addressCheck(address);
        else
            address = nameCheck(address);
        BufferedReader br = null;
        Process executor;
        try {
            System.out.println("running");
            if (packaging)
                executor = Runtime.getRuntime().exec(packageIcon + address);
            else
                executor = Runtime.getRuntime().exec(extractIcon + address);
            br = new BufferedReader(new InputStreamReader(executor.getErrorStream()));
            String line;
            StringBuilder errorInfo = new StringBuilder();
            while ((line = br.readLine()) != null)
                errorInfo.append(line + "\n");
            String errorMessage = "";
            if (errorInfo.length()<1) {
                System.out.println("successful");
                errorMessage = "successful";
            }else {
                errorMessage = (errorInfo+"").replace(address,"");
                System.out.println(errorMessage);

            }
            String finalErrorMessage = errorMessage;
            Platform.runLater(()->{
                upd(finalErrorMessage);
            });
            executor.destroy();
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

    private String removeSpace(String address){
        if(address.contains(" ")){
        File file = new File(address);
        address = address.replace(" ","_");
        file.renameTo(new File(address));
        }
        return address;
    }

    private String endWith(String address){
        String type = "";
        for (int i = address.length()-1; i >= 0; i--) {
            if(address.charAt(i)=='.')break;
            type = address.charAt(i)+type;
        }
        type="."+type;
        return type;
    }

    private String fileName(String address){
        String name = "";
        for (int i = address.length()-1; i >= 0; i--) {
            if(address.charAt(i)=='/')break;
            name = address.charAt(i)+name;
        }
        return name;
    }

    public boolean autoRegconize(String address){
        address = removeSpace(address);
        if(endWith(address).equals(".icns")){
            execute(address,false);
            return true;
        }
        else if(endWith(address).equals(".iconset")){
            execute(address, true);
            return true;
        }
        else if(endWith(address).equals(".app")){
            executeApp(address);
            return true;
        }
        else return false;
    }

    private void executeApp(String address){
        String plist = address+"/Contents/Info.plist";
        String iconName = "";
        String iconSrc = address+"/Contents/Resources/";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(plist))));
            while ((iconName = reader.readLine()) != null){
                if(iconName.contains(".icns")){
                    iconName = iconName.replace("\t","").replace("<string>","").replace("</string>","");
                    break;
                }
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(iconName);
        iconSrc += iconName;
        if(iconSrc.contains(".icns"))
            execute(iconSrc,false);
        String repo = address.replace(fileName(address),"");
        Process executor;
        try {
            executor = Runtime.getRuntime().exec("cp -r "+iconSrc.replace(".icns",".iconset")+" "+repo);
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(executor.getInputStream()));
            while ((line = br.readLine()) != null)
                System.out.println(line);
            executor.destroy();
            br.close();
            executor = Runtime.getRuntime().exec("rm -r -f "+iconSrc.replace(".icns",".iconset"));
            br = new BufferedReader(new InputStreamReader(executor.getInputStream()));
            while ((line = br.readLine()) != null)
                System.out.println(line);
            executor.destroy();
        }catch (Exception e){}
    }

    public void reg(Observer observer){
        this.observer = observer;
    }

    public void upd(String message){
        observer.update(message);
    }

    public static void main(String[] args) {
        Execution execution = new Execution();
        execution.executeApp("/Users/tyraellee/Desktop/i4Tools.app");
    }
}
//todo: recognize .app
