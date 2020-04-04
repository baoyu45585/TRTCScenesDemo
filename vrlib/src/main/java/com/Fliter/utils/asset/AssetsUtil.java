package com.Fliter.utils.asset;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *  Assets文件工具类
 */
public class AssetsUtil{

    private static  AssetsUtil instance;
    public final static String IS_HAS_WRITE_SD="is_has_write_SD";
    public final static String FILE_NAME="files";

    private AssetsUtil() {
        super();
    }
    public synchronized static AssetsUtil getInstance(){
        if (instance == null) {
            instance = new AssetsUtil();
        }
        return instance;
    }

    /**
     * 把Assets文件夹复制到手机
     * @param context
     */
    public void initAssetToSD(Context context){
        if ((boolean) SharePreferenceUtil.getPreference(context,IS_HAS_WRITE_SD, true)) {
          assetToSD(FILE_NAME, SDUtil.getDiskCacheDir(context)+ "/"+FILE_NAME,context);
         //   assetToSD(FILE_NAME, context.getExternalFilesDir(null).getAbsolutePath()+ "/"+FILE_NAME,context);
            SharePreferenceUtil.savePreference (context,IS_HAS_WRITE_SD, false);
        }
    }




    /**
     * @param assetPath  asset下的路径
     * @param SDPath     SDpath下保存路径
     */
    public void assetToSD(String assetPath, String SDPath , Context context){

        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames=null;
        FileOutputStream out = null;
        InputStream in=null;
        try {
            filenames = context.getAssets().list(assetPath);
            if(filenames.length>0){//说明是目录
                //创建目录
                getDirectory(assetPath,context);
                for(String fileName:filenames){
                    assetToSD(assetPath+"/"+fileName, SDPath+"/"+fileName,context);
                }
            }else{//说明是文件，直接复制到SD卡
                File SDFlie=new File(SDPath);
                String path=assetPath.substring(0, assetPath.lastIndexOf("/"));
                getDirectory(path,context);
                //将内容写入到文件中
                if(!SDFlie.exists()){
                    SDFlie.createNewFile();
                }
                //将内容写入到文件中
                in= context.getAssets().open(assetPath);
                out= new FileOutputStream(SDFlie);
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=in.read(buffer))!=-1){
                    out.write(buffer, 0, byteCount);
                }
                out.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                if(out!=null){
                    out.close();
                    out=null;
                }
                if(in!=null){
                    in.close();
                    in=null;
                }
                /**
                 * 关闭报错，java.lang.RuntimeException:
                 * Unable to start activity ComponentInfo
                 * {com.example.wealth/com.example.wealth.UI.main}:
                 * java.lang.RuntimeException: Assetmanager has been closed
                 */
//              if(asset!=null){
//                  asset.close();
//                  asset=null;
//                  }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }




    /**
     * @param assetPath  必须asset文件夹下的文件路径
     */
    public String getAssetFileContent(String assetPath, Context context){
        StringBuffer buffer=new StringBuffer();
        InputStream in=null;
        String line; // 用来保存每行读取的内容
        try {
            //将内容写入到文件中
            in= context.getAssets().open(assetPath);
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
           line = reader.readLine(); // 读取第一行
           while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
           }
            inputStreamReader.close();
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }



    //分级建立文件夹
    public void getDirectory(String path, Context context){
        //对SDpath进行处理，分层级建立文件夹
        String[]s=path.split("/");
        String str=context.getExternalCacheDir().getPath();
        for (int i = 0; i < s.length; i++) {
            str=str+"/"+s[i];
            File file=new File(str);
            if(!file.exists()){
                file.mkdir();
            }
        }
    }
}