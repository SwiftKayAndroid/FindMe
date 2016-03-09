package com.swiftkaydevelopment.findme.utils;

import android.content.Context;

import java.io.File;

public class FileCache {
     
    private File cacheDir;
    private static FileCache fileCache = null;

    public static FileCache getInstance(Context context) {
        if (fileCache == null) {
            fileCache = new FileCache(context);
        }
        return fileCache;
    }
     
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=context.getCacheDir();//new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
     
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
       String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
       // String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
    }
     
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
 
}