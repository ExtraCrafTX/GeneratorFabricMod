/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.extracraftx.minecraft.generatorfabricmod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.extracraftx.minecraft.generatorfabricmod.terminal.Interface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeneratorFabricMod {

    public static final String[] SPINNER = {"◢", "◣", "◤", "◥"};
    public static final int INTERVAL = 50;

    public static void main(String[] args) {
        try{
            Interface prompter = new Interface(true);

            prompter.startSpinner("Loading Minecraft versions... ", INTERVAL, SPINNER);
            JsonArray mcVersionsData = jsonFromUrl("https://meta.fabricmc.net/v2/versions/game").getAsJsonArray();
            String[] mcVersions = new String[mcVersionsData.size()];
            int defaultMcVersion = 0;
            for(int i = 0; i < mcVersionsData.size(); i++)
                mcVersions[i] = mcVersionsData.get(i).getAsJsonObject().get("version").getAsString();
            for(int i = 0; i < mcVersionsData.size(); i++){
                if(mcVersionsData.get(i).getAsJsonObject().get("stable").getAsBoolean()){
                    defaultMcVersion = i;
                    break;
                }
            }
            prompter.finishSpinner("done.");
            Thread.sleep(500);

            prompter.startSpinner("Loading Fabric API versions... ", INTERVAL, SPINNER);
            JsonArray apiVersionsData = jsonFromUrl("https://addons-ecs.forgesvc.net/api/v2/addon/306612/files").getAsJsonArray();
            ArrayList<ApiVersion> apiVersionsList = new ArrayList<>();
            Pattern apiRegex = Pattern.compile("\\[([^/\\]]+)(?:/.+)?\\]");
            for(int i = 0; i < apiVersionsData.size(); i++){
                JsonObject version = apiVersionsData.get(i).getAsJsonObject();
                String displayName = version.get("displayName").getAsString();
                Matcher matcher = apiRegex.matcher(displayName);
                if(matcher.find()){
                    String mcVersion = matcher.group(1);
                    boolean found = false;
                    int index = 0;
                    for(int j = 0; j < mcVersions.length; j++){
                        if(mcVersion.equals(mcVersions[j])){
                            index = j;
                            found = true;
                        }
                    }
                    if(!found)
                        index = mcVersions.length;
                    apiVersionsList.add(new ApiVersion(index, displayName));
                }
            }
            ApiVersion[] apiVersions = new ApiVersion[apiVersionsList.size()];
            apiVersionsList.toArray(apiVersions);
            Pattern apiBuildRegex = Pattern.compile("build (\\d+)");
            Arrays.sort(apiVersions, (a,b)->{
                int verA = a.mcVersion;
                int verB = b.mcVersion;
                if(verA == verB){
                    Matcher matcherA = apiBuildRegex.matcher(a.name);
                    matcherA.find();
                    int buildA = Integer.parseInt(matcherA.group(1));
                    Matcher matcherB = apiBuildRegex.matcher(b.name);
                    matcherB.find();
                    int buildB = Integer.parseInt(matcherB.group(1));
                    return buildB - buildA;
                }
                return verA-verB;
            });
            prompter.finishSpinner("done.");
            Thread.sleep(500);

            int mcVersion = prompter.promptList("Select Minecraft version:", true, mcVersions);
            boolean useApi = prompter.yesOrNo("Use Fabric API?", true);
            if(useApi)
                prompter.promptList("Select Fabric API version:", true, apiVersions);
        }
        catch(Exception e){}
    }

    public static JsonElement jsonFromUrl(String url) throws IOException{
        return JsonParser.parseString(getUrl(url));
    }

    public static String getUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        return readAllFromStream(url.openStream());
    }
    
    public static String readAllFromStream(InputStream stream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

    private static class ApiVersion{
        int mcVersion;
        String name;

        public ApiVersion(int mcVersion, String name){
            this.mcVersion = mcVersion;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
