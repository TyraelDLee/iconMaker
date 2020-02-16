package defaultPackage;

import javafx.application.Platform;

import java.io.*;

public class Execution {
    private final static String packageIcon = "iconutil -c icns ";
    private final static String extractIcon = "iconutil -c iconset ";
    private Observer observer;

    /**
     * main execute method,
     * call the shell in macOS to package/extract
     * icon files.
     *
     * @param address the file location
     * @param packaging define which work it should do, true for package iconset file to icns, vice versa.
     * */
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

    /**
     * Check the address is an iconset folder or not.
     * If it is not and user consist package then it will
     * add the .iconset and try to package to icns file.
     *
     * @param address the file location.
     * */
    private String addressCheck(String address) {
        if (address.length() < 8 || !address.substring(address.length() - 8).equals(".iconset")){
            File file = new File(address);
            address += ".iconset";
            file.renameTo(new File(address));
        }
        System.out.println(address);
        return address;
    }

    /**
     * Check the address is a macOS icon file or not.
     * If it is not and user consist package then it will
     * add the .icns and try to package to iconset folder.
     *
     * @param name the file location.
     * */
    private String nameCheck(String name) {
        if (name.length() < 5 || !name.substring(name.length() - 5).equals(".icns")){
            File file = new File(name);
            name += ".icns";
            file.renameTo(new File(name));
        }
        System.out.println(name);
        return name;
    }

    /**
     * Replace all white space in the location string.
     *
     * @param address the file location.
     * @return the file which all space replaced.
     * */
    private String removeSpace(String address){
        if(address.contains(" ")){
        File file = new File(address);
        address = address.replace(" ","_");
        file.renameTo(new File(address));
        }
        return address;
    }

    /**
     * Get the file type.
     *
     * @param address the file locaiton.
     * @return the file type.
     * */
    private String endWith(String address){
        String type = "";
        for (int i = address.length()-1; i >= 0; i--) {
            if(address.charAt(i)=='.')break;
            type = address.charAt(i)+type;
        }
        type="."+type;
        return type;
    }

    /**
     * Get the file name.
     *
     * @param address the file locaiton.
     * @return the file name.
     * */
    private String fileName(String address){
        String name = "";
        for (int i = address.length()-1; i >= 0; i--) {
            if(address.charAt(i)=='/')break;
            name = address.charAt(i)+name;
        }
        return name;
    }

    /**
     * This method will analysis the file type and try
     * to execute automatically. If it cannot recognize
     * user still may choose execute manually.
     * */
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

    /**
     * When input is an app folder, run this.
     * This method will try to get the icon name in the plist
     * then extract icon file to iconset out.
     *
     * The result will be appear near by the app file.
     * */
    private void executeApp(String address){
        String plist = address+"/Contents/Info.plist";
        String iconName = "";
        String iconSrc = address+"/Contents/Resources/";
        //get the icon name in app//
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
        //execute//
        if(iconSrc.contains(".icns")){
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
    }

    public void reg(Observer observer){
        this.observer = observer;
    }

    public void upd(String message){
        observer.update(message);
    }

    /**
     * TEST METHOD, DO NOT RUN THIS DIRECTLY!
     * */
    public static void main(String[] args) {
        Execution execution = new Execution();
        execution.executeApp("/Users/tyraellee/Desktop/i4Tools.app");

    }
}
//todo: recognize vector image and package to .icns file
