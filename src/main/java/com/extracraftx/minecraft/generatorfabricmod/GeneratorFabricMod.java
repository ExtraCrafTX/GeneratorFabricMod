package com.extracraftx.minecraft.generatorfabricmod;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.extracraftx.minecraft.generatorfabricmod.terminal.Interface;
import com.extracraftx.minecraft.templatemakerfabric.TemplateMakerFabric;
import com.extracraftx.minecraft.templatemakerfabric.data.DataProvider;
import com.extracraftx.minecraft.templatemakerfabric.data.holders.*;
import com.extracraftx.minecraft.templatemakerfabric.fabric.*;

public class GeneratorFabricMod {

    public static final String[] SPINNER = {
        "[    ]",
        "[=   ]",
        "[==  ]",
        "[=== ]",
        "[ ===]",
        "[  ==]",
        "[   =]",
        "[    ]",
        "[   =]",
        "[  ==]",
        "[ ===]",
        "[=== ]",
        "[==  ]",
        "[=   ]"
    };
    public static final int INTERVAL = 50;

    public static final String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void",
            "volatile", "while", "continue" };
    public static final Pattern PACKAGE_REGEX = Pattern
            .compile("^([A-Za-z$_][A-Za-z0-9$_]*\\.)*[A-Za-z$_][A-Za-z0-9$_]*$");
    public static final Pattern IDENT_REGEX = Pattern.compile("^[A-Za-z$_][A-Za-z0-9$_]*$");

    public static void main(String[] args) {
        System.out.println("GeneratorFabricMod version 0.1.5");
        try {
            Interface prompter = new Interface();
            DataProvider dataProvider = new DataProvider();
            TemplateMakerFabric templateMaker = new TemplateMakerFabric();

            prompter.startSpinner("Getting Minecraft versions... ", INTERVAL, SPINNER);
            ArrayList<MinecraftVersion> mcVersions = dataProvider.getMinecraftVersions();
            int defaultMcVersion = 0;
            for (int i = 0; i < mcVersions.size(); i++) {
                if (mcVersions.get(i).stable) {
                    defaultMcVersion = i;
                    break;
                }
            }
            prompter.finishSpinner("[done]");

            prompter.startSpinner("Getting Fabric API versions... ", INTERVAL, SPINNER);
            ArrayList<IndexedFabricApiVersion> apiVersions = dataProvider.getSortedFabricApiVersions();
            prompter.finishSpinner("[done]");

            prompter.startSpinner("Getting Yarn mapping versions... ", INTERVAL, SPINNER);
            dataProvider.getYarnVersions();
            prompter.finishSpinner("[done]");

            prompter.startSpinner("Getting Loom versions... ", INTERVAL, SPINNER);
            ArrayList<LoomVersion> loomVersions = dataProvider.getLoomVersions();
            prompter.finishSpinner("[done]");

            prompter.startSpinner("Getting Fabric Loader versions... ", INTERVAL, SPINNER);
            dataProvider.getLoaderVersions();
            prompter.finishSpinner("[done]");

            License[] licenses = dataProvider.getSupportedLicenses();

            MinecraftVersion mcVersion = mcVersions.get(
                prompter.promptList("Minecraft version:", true, defaultMcVersion, mcVersions.toArray())
            );

            String modName = prompter.prompt("Mod name:", s -> s.isEmpty() ? "You must input a name" : null);
            String modId = prompter.prompt("Mod id (must be unique):",
                    s -> s.isEmpty() ? "You must input a mod id" : null);
            String modDescription = prompter.prompt("Mod description:",
                    s -> s.isEmpty() ? "You must input a description" : null);
            String modVersion = prompter.promptSemVer("Mod version:");
            String author = prompter.prompt("Author:", s -> s.isEmpty() ? "You must input an author" : null);

            String homepage = prompter.prompt("Homepage (not required):", s -> {
                if (s.isEmpty())
                    return null;
                if (!isValidUrl(s))
                    return "Please enter a valid URL";
                return null;
            });
            String sources = prompter.prompt("Source code URL (not required):", s -> {
                if (s.isEmpty())
                    return null;
                if (!isValidUrl(s))
                    return "Please enter a valid URL";
                return null;
            });

            int license = prompter.promptList("License:", true, 1, dataProvider.getSupportedLicenses());
            String licenseName = author;
            if (licenses[license].requiresName)
                licenseName = prompter.prompt("Name on license:", author,
                        s -> s.isEmpty() ? "You must enter a name for the license" : null);

            String packageName = prompter.prompt("Main package:", s -> {
                if (s.isEmpty())
                    return "You must enter a package";
                if (PACKAGE_REGEX.matcher(s).matches()) {
                    String[] idents = s.split("\\.");
                    for (String ident : idents) {
                        if (contains(KEYWORDS, ident))
                            return ident + " is a Java keyword";
                    }
                    return null;
                } else {
                    return "Please enter a valid Java package name";
                }
            });

            String mainClass = prompter.prompt("Mod initialiser class:", s -> {
                if (s.isEmpty())
                    return "You must enter an initialiser class";
                if (IDENT_REGEX.matcher(s).matches()) {
                    if (contains(KEYWORDS, s))
                        return s + " is a Java keyword";
                    return null;
                } else {
                    return "Please enter a valid Java class name";
                }
            });

            boolean mixins = prompter.yesOrNo("Use mixins?", true);

            boolean useApi = prompter.yesOrNo("Use Fabric API?", true);
            FabricApiVersion apiVersion = null;
            if (useApi) {
                int defaultApi = dataProvider.getDefaultFabricApiVersion(mcVersion);
                apiVersion = apiVersions.get(prompter.promptList("Select Fabric API version:", true, defaultApi, apiVersions.toArray()));
            }

            Object[] yarnOptions = dataProvider.getFilteredYarnVersions(mcVersion).toArray();
            int yarnVersionIndex = prompter.promptList("Select Yarn mappings:", true, 0, yarnOptions);
            YarnVersion yarnVersion = (YarnVersion) yarnOptions[yarnVersionIndex];

            int defaultLoom = dataProvider.getDefaultLoomVersion(yarnVersion).index;
            LoomVersion loomVersion = loomVersions
                    .get(prompter.promptList("Select Loom version:", true, defaultLoom, loomVersions.toArray()));

            Object[] loaderOptions = dataProvider.getFilteredLoaderVersions(loomVersion).toArray();
            int loaderVersionIndex = prompter.promptList("Select Fabric Loader version:", true, 0, loaderOptions);
            LoaderVersion loaderVersion = (LoaderVersion) loaderOptions[loaderVersionIndex];

            String mavenGroup = prompter.prompt("Maven group:", packageName, s -> {
                if (s.isEmpty())
                    return "You must enter a maven group";
                if (PACKAGE_REGEX.matcher(s).matches()) {
                    String[] idents = s.split("\\.");
                    for (String ident : idents) {
                        if (contains(KEYWORDS, ident))
                            return ident + " is a Java keyword";
                    }
                    return null;
                } else {
                    return "Please enter a valid Java package name";
                }
            });
            String archiveName = prompter.prompt("Archive base name:", modId, s -> {
                if (s.isEmpty())
                    return "You must enter an archive base name";
                return null;
            });

            FabricModBuilder builder = new FabricModBuilder();
            builder.setMcVersion(mcVersion);
            builder.setModName(modName);
            builder.setModId(modId);
            builder.setModDescription(modDescription);
            builder.setModVersion(modVersion);
            builder.setAuthor(author);
            if (homepage != null && !homepage.equals(""))
                builder.setHomepage(new URL(homepage));
            if (sources != null && !sources.equals(""))
                builder.setSources(new URL(sources));
            builder.setLicense(licenses[license]);
            builder.setNameOnLicense(licenseName);
            builder.setMainPackage(packageName);
            builder.setMainClass(mainClass);
            builder.setMixin(mixins);
            builder.setFabricApi(useApi);
            if (useApi)
                builder.setApiVersion(apiVersion);
            builder.setYarnVersion(yarnVersion);
            builder.setLoomVersion(loomVersion);
            builder.setLoaderVersion(loaderVersion);
            builder.setMavenGroup(mavenGroup);
            builder.setArchiveName(archiveName);
            FabricMod mod = builder.build();

            templateMaker.outputMod(mod, Paths.get(""),
                    file -> prompter.startSpinner("Outputting " + file + " ", INTERVAL, SPINNER),
                    file -> prompter.finishSpinner("[done]"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean contains(String[] array, String s) {
        for (String item : array) {
            if (s.equals(item))
                return true;
        }
        return false;
    }
}
