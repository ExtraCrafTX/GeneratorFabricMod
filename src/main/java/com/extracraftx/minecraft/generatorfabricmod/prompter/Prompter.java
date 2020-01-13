package com.extracraftx.minecraft.generatorfabricmod.prompter;

import java.io.IOException;
import java.io.Reader;
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
    private Reader nbReader;
    private BindingReader raw;
    private LineReader reader;
    private KeyMap<Integer> map;

    public Prompter() throws IOException{
        terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler((sig)->System.exit(0))
                .build();
        terminal.enterRawMode();
        nbReader = terminal.reader();
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

    private void clearLine(){
        CSI("2K");
    }

    private void red(){
        CSI("31m");
    }

    private void reset(){
        CSI("0m");
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