package defaultPackage;

import javafx.application.Platform;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Execution {
    private final static String packageIcon = "iconutil -c icns ";
    private final static String extractIcon = "iconutil -c iconset ";
    private Observer observer;
    private final static int[] sizes = {16,32,128,256,512};

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
        else if(endWith(address).equals(".svg")){
            executeSVG(address);
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
        iconName = removeFrontSpace(iconName);
        iconSrc += iconName;
        //execute//
        if(iconSrc.contains(".icns")){
//            execute(iconSrc,false);
            String repo = address.replace(fileName(address),"");
            Process executor;
            try {
                System.out.println(iconSrc);
                executor = Runtime.getRuntime().exec("cp -r "+iconSrc.replace("\t","")+" "+repo);
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(executor.getInputStream()));
                while ((line = br.readLine()) != null)
                    System.out.println(line);
                executor.destroy();
                br.close();
//                executor = Runtime.getRuntime().exec("rm -r -f "+iconSrc.replace(".icns",".iconset"));
                br = new BufferedReader(new InputStreamReader(executor.getInputStream()));
                while ((line = br.readLine()) != null)
                    System.out.println(line);
                executor.destroy();
            }catch (Exception e){}
        }
    }

    private void executeSVG(String address){
        String fileEncode = encode(address);
        String fileRoot = address.replace(fileName(address),"");
        String file = fileRoot+"icon.iconset";
        File gen;
        for (int i = 0; i < 255; i++) {
            if(i==0) file = fileRoot+"icon.iconset";
            else file = fileRoot+"icon"+i+".iconset";
            if(!new File(file).exists()) {
                gen = new File(file);
                gen.mkdir();
                break;
            }
        }
        //gen folder.

        System.out.println(file);
        for(int size : sizes){
            svgTopng(size, false, encode(address), file+"/icon_");
            svgTopng(size,true, encode(address), file+"/icon_");
        }
        System.out.println("svg convert finish!");
        execute(file,true);
    }

    private void svgTopng(int size, boolean x2, String svg, String outputSRC){
        File img = new File(outputSRC+size+"x"+size+(x2?"@x2":"")+".png");
        try {
            FileOutputStream writer = new FileOutputStream(img);
            byte[] SVGcontents = svg.getBytes(StandardCharsets.UTF_8);
            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float)(x2?size*2:size));
            //pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, size);
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(SVGcontents));
            TranscoderOutput output = new TranscoderOutput(writer);
            pngTranscoder.transcode(input, output);
            writer.flush();
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }

    }

    private String removeFrontSpace(String name){
        if(!(name.charAt(0)+"").equals(" "))
            return name;
        name = (String)name.subSequence(1,name.length());
        return removeFrontSpace(name);
    }

    private String encode(String src){
        String content = "";
        try {
            InputStream in = new FileInputStream(src);
            byte[] data = new byte[in.available()];
            content = new String(data,0,in.read(data));
            in.read(data);
            in.close();
        } catch (IOException e) { e.printStackTrace(); }
        return content;
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
        //execution.executeApp("/Users/tyraellee/Desktop/i4Tools.app");
//        execution.executeSVG("/Users/tyraellee/Desktop/test.svg");
    }
}
//todo: recognize vector image and package to .icns file
