/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.extracraftx.minecraft.generatorfabricmod;

import com.extracraftx.minecraft.generatorfabricmod.prompter.Prompter;

import org.jline.reader.UserInterruptException;

public class GeneratorFabricMod {

    public static void main(String[] args) {
        try{
            Prompter prompter = new Prompter();
            
            String name = prompter.prompt(
                "Please enter your name:",
                (input)-> input.isEmpty() ? "Your name cannot be empty" : null
            );
            System.out.println("Hello " + name + "!");
        }
        catch(UserInterruptException e){}
        catch(Exception e){
            System.err.println("An unknown error occured. The stack trace follows: ");
            e.printStackTrace();
        }
    }
}