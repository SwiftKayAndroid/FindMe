package com.swiftkaydevelopment.findme.managers;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/*
 *    Copyright (C) 2015 Kevin Haines
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 *    Last update Sept 28 2015
 */

public class ConnectionManager {

    private String method;
    private String uri;
    private boolean useJson;
    private boolean basicAuth = false;


    private boolean usePinnedCertifate = false;
    private Map<String,String> params;
    private String username;
    private String password;

    public final String GET = "GET";
    public final String POST = "POST";
    public final String UTF8 = "UTF-8";

    private static final String BOUNCY_CASTLE = "BKS";

    public ConnectionManager(){
        setMethod("GET");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParam(String key,String val){
        params.put(key,val);
    }

    public String getEncodedParams(){
        StringBuilder sb = new StringBuilder();

        for (String key:params.keySet()
             ) {
            String val = null;
            try {
                val = URLEncoder.encode(params.get(key), UTF8);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(sb.length()>0){
                sb.append("&");
            }
            sb.append(key + "=" + val);
            
        }
        return sb.toString();
    }

    public void setUseJson(boolean usejson){
        this.useJson = usejson;
    }
    public void useBasicAuth(boolean useAuth){
        this.basicAuth = useAuth;
    }

    public void setPassword(String pass){
        this.password = pass;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }

    public boolean isUsePinnedCertifate() {
        return usePinnedCertifate;
    }

    public void setUsePinnedCertifate(boolean usePinnedCertifate) {
        this.usePinnedCertifate = usePinnedCertifate;
    }

    public String sendHttpRequest(String uri){
        this.uri = uri;
        boolean redirect = false;

        BufferedReader reader = null;
        HttpURLConnection con = null;

        try{

            //GET METHOD
            if(getMethod().equals(GET)){
                if(params != null) {
                    uri += "?" + getEncodedParams();
                    log("getting params");
                }
                log("no params set");

            }

            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(getMethod());
            log("Request Method is: " + getMethod());

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = con.getHeaderField("Location");

                url = new URL(newUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(getMethod());

            }

            //if USER NEEDS TO USE BASIC AUTHENTICATION
            if(basicAuth) {

                log("Basic Auth being used: username: " + getUsername() + " password: " + getPassword());
                byte[] loginBytes = (username + ":" + password).getBytes();
                StringBuilder loginBuilder = new StringBuilder()
                        .append("basic ")
                        .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

                con.addRequestProperty("Authorization", loginBuilder.toString());

            }

            //POST METHOD
            if(getMethod().equals(POST)){

                log("Using POST METHOD");
                con.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());


                if(useJson){
                    JSONObject jobj = new JSONObject(getParams());
                    String params = "params=" + jobj.toString();
                    writer.write(params);

                }else{
                    writer.write(getEncodedParams());
                }

                writer.flush();

            }


            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            String line;
            while((line = reader.readLine()) !=null){
                sb.append(line);

            }

            return  sb.toString();
        }catch(Exception e){
            e.printStackTrace();


        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }
    public String sendPinnedHttpsRequest(String uri,InputStream resourceStream,char[] key_password){
        this.uri = uri;

        boolean redirect = false;

        BufferedReader reader = null;
        HttpsURLConnection con = null;

        try{

            //GET METHOD
            if(getMethod().equals(GET)){
                if(params != null) {
                    uri += "?" + getEncodedParams();
                    log("getting params");
                }
                log("no params set");

            }


            URL url = new URL(uri);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod(getMethod());
            log("Request Method is: " + getMethod());


            SSLSocketFactory sslFactory = null;

            KeyStore keyStore = KeyStore.getInstance(BOUNCY_CASTLE);
            keyStore.load(resourceStream, key_password);


            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            sslFactory = ctx.getSocketFactory();
            con.setSSLSocketFactory(sslFactory);


            int status = con.getResponseCode();
            if (status != HttpsURLConnection.HTTP_OK) {
                if (status == HttpsURLConnection.HTTP_MOVED_TEMP
                        || status == HttpsURLConnection.HTTP_MOVED_PERM
                        || status == HttpsURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = con.getHeaderField("Location");

                url = new URL(newUrl);
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod(getMethod());

            }


            //if USER NEEDS TO USE BASIC AUTHENTICATION
            if(basicAuth) {

                log("Basic Auth being used: username: " + getUsername() + " password: " + getPassword());
                byte[] loginBytes = (username + ":" + password).getBytes();
                StringBuilder loginBuilder = new StringBuilder()
                        .append("basic ")
                        .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

                con.addRequestProperty("Authorization", loginBuilder.toString());

            }

            //POST METHOD
            if(getMethod().equals(POST)){

                log("Using POST METHOD");
                con.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());


                if(useJson){
                    JSONObject jobj = new JSONObject(getParams());
                    String params = "params=" + jobj.toString();
                    writer.write(params);

                }else{
                    writer.write(getEncodedParams());
                }

                writer.flush();

            }


            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            String line;
            while((line = reader.readLine()) !=null){
                sb.append(line);

            }


            return  sb.toString();
        }catch(Exception e){
            e.printStackTrace();


        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    private void err(String error){

        Log.e("httprequest", error);

    }

    private void log(String msg){
        Log.d("httpRequest", msg);
    }
}
