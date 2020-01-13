package com.extracraftx.minecraft.generatorfabricmod.prompter;

import java.io.IOException;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Prompter{
    public static final int LIST_SIZE = 7;
    public static final int SCROLL_PADDING = 1;

    private Terminal terminal;
    private BindingReader raw;
    private LineReader reader;
    private KeyMap<Integer> map;

    public Prompter() throws IOException{
        terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler((sig)->System.exit(0))
                .build();
        terminal.enterRawMode();
        raw = new BindingReader(terminal.reader());
        reader = LineReaderBuilder.builder().terminal(terminal).appName("Testing").build();

        map = new KeyMap<>();
        map.setAmbiguousTimeout(1);
        map.bind( 0, "\033OA");
        map.bind( 1, "\033OB");
        map.bind( 2, "\r\n", "\r", "\n");
        map.bind(-1, "\033");
    }

    public String prompt(String prompt, Function<String, String> validator){
        System.out.println(prompt);
        String line = null;
        String result;
        while((result = validator.apply(line = readLine(line))) != null){
            clearLine(); red();
            print(result);
            reset(); moveUp();
        }
        clearLine();
        return line;
    }

    public int promptList(String prompt, boolean required, String... options){
        clearLine(); hideCursor();
        println(prompt);
        
        int val = -1;
        if(options.length < LIST_SIZE)
            val = list(required ? "You must select an option" : null, options);
        else
            val = scrollingList(required ? "You must select an option" : null, options);
        
        clearLine(); showCursor();
        return val;
    }

    private int list(String requiredMessage, String... options){
        int current = 0;
        String error = "";
        while(true){
            for(int i = 0; i < options.length; i++){
                if(i == current){
                    yellow();
                    print("> "); println(options[i]);
                    reset();
                }else{
                    print("  "); println(options[i]);
                }
            }
            
            clearLine(); red();
            print(error);
            reset(); home();

            int input = raw.readBinding(map);
            if(input == 2){
                break;
            }
            if(input == -1){
                if(requiredMessage != null){
                    error = requiredMessage;
                }else{
                    current = -1;
                    break;
                }
            }else{
                // error = "";
                if(input == 0 && current > 0)
                    current --;
                if(input == 1 && current < options.length-1)
                    current ++;
            }
            moveUp(options.length);
        }
        return current;
    }

    public int scrollingList(String requiredMessage, String... options){
        int current = 0;
        int start = options.length-1;
        String error = "";
        while(true){
            for(int pos = 0; pos < LIST_SIZE; pos++){
                int i = (start + pos) % options.length;
                if(i == current){
                    yellow();
                    print("> "); println(options[i]);
                    reset();
                }else{
                    print("  "); println(options[i]);
                }
            }
            println("(Move to reveal additional choices)");
            
            clearLine(); red();
            print(error);
            reset(); home();

            int input = raw.readBinding(map);
            if(input == 2){
                break;
            }
            if(input == -1){
                if(requiredMessage != null){
                    error = requiredMessage;
                }else{
                    current = -1;
                    break;
                }
            }else{
                // error = "";
                if(input == 0){
                    if(current == 0)
                        current = options.length-1;
                    else
                        current --;
                    if((current - start + options.length)%options.length < SCROLL_PADDING)
                        if(start == 0)
                            start = options.length-1;
                        else
                            start --;
                }
                else if(input == 1){
                    current = (current+1)%options.length;
                    if((current - start + options.length)%options.length > LIST_SIZE-SCROLL_PADDING-1)
                        start = (start+1)%options.length;
                }
            }
            moveUp(LIST_SIZE+1);
        }
        return current;
    }

    private void moveUp(){
        moveUp(1);
    }

    private void moveUp(int lines){
        if(lines <= 0)
            throw new IllegalArgumentException("Number of lines must be > 0");
        CSI(lines + "F");
    }

    private void moveDown(){
        moveDown(1);
    }

    private void moveDown(int lines){
        if(lines <= 0)
            throw new IllegalArgumentException("Number of lines must be > 0");
        CSI(lines + "E");
    }

    private void home(){
        moveToColumn(1);
    }

    private void moveToColumn(int col){
        CSI(col + "G");
    }

    private void clearLine(){
        CSI("2K");
    }

    private void red(){
        CSI("31m");
    }

    private void yellow(){
        CSI("33m");
    }

    private void reset(){
        CSI("0m");
    }

    private void hideCursor(){
        CSI("?25l");
    }

    private void showCursor(){
        CSI("?25h");
    }

    private void CSI(String code){
        print("\033[");
        print(code);
    }

    private void print(String s){
        System.out.print(s);
    }

    private void println(String s){
        System.out.println(s);
    }

    private String readLine(String buffer){
        return reader.readLine(null, null, buffer);
    }
}