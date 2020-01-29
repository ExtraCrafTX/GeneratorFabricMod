package com.extracraftx.minecraft.generatorfabricmod.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Interface{
    public static final int LIST_SIZE = 7;
    public static final int SCROLL_PADDING = 1;

    private Terminal terminal;
    private BindingReader raw;
    private LineReader reader;
    private KeyMap<Integer> listMap;
    private KeyMap<Integer> booleanMap;
    private KeyMap<Integer> semVerMap;

    private Prompter prompter;
    private Spinner spinner;

    public Interface() throws IOException{
        terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler((sig)->{
                    if(sig == Terminal.Signal.QUIT || sig == Terminal.Signal.TSTP || sig == Terminal.Signal.INT){
                        System.out.println("\033[0m\033[?25h");
                        System.exit(0);
                    }
                })
                .build();
        terminal.enterRawMode();
        raw = new BindingReader(terminal.reader());
        reader = LineReaderBuilder.builder().terminal(terminal).appName("Testing").build();

        listMap = new KeyMap<>();
        listMap.setAmbiguousTimeout(10);
        listMap.bind( 0, "\033OA", "\033[A", "k");
        listMap.bind( 1, "\033OB", "\033[B", "j");
        listMap.bind( 2, "\012", "\015");
        listMap.bind(-1, "\033");

        booleanMap = new KeyMap<>();
        booleanMap.setAmbiguousTimeout(10);
        booleanMap.bind(0, "\033OC", "\033OD", "\033[C", "\033[D");
        booleanMap.bind(1, "y", "Y");
        booleanMap.bind(2, "n", "N");
        booleanMap.bind(3, "\012", "\015");

        semVerMap = new KeyMap<>();
        semVerMap.setAmbiguousTimeout(10);
        for(char i = '0'; i <= '9'; i++){
            semVerMap.bind((int)i, ""+i);
        }
        for(char i = 'A'; i <= 'Z'; i++){
            semVerMap.bind((int)i, ""+i);
            semVerMap.bind(i+32, ""+((char)(i+32)));
        }
        semVerMap.bind(0, "\033OD", "\033[D");
        semVerMap.bind(1, "\033OC", "\033[C");
        semVerMap.bind(2, "\033OA", "\033[A");
        semVerMap.bind(3, "\033OB", "\033[B");
        semVerMap.bind(4, "\012", "\015");
        semVerMap.bind(5, "+");
        semVerMap.bind(6, "-");
        semVerMap.bind(7, ".");
        semVerMap.bind(8, "\010", "\177", "\033[3~");

        prompter = new Prompter();

        reset();
    }

    public void listen(){
        while(true){
            int input = raw.readCharacter();
            System.out.print(input);
        }
    }

    public String prompt(String prompt){
        return prompter.prompt(prompt, null, s->null);
    }

    public String prompt(String prompt, Function<String, String> validator){
        return prompter.prompt(prompt, null, validator);
    }

    public String prompt(String prompt, String def, Function<String, String> validator){
        return prompter.prompt(prompt, def, validator);
    }

    public boolean yesOrNo(String prompt, boolean def){
        return prompter.yesOrNo(prompt, def);
    }

    public boolean yesOrNo(String prompt, boolean def, String yes, String no){
        return prompter.yesOrNo(prompt, def, yes, no);
    }

    public String promptSemVer(String prompt) throws Exception{
        return prompter.promptSemVer(prompt);
    }
    
    public int promptList(String prompt, boolean required, int def, String... options){
        return prompter.promptList(prompt, required, def, options);
    }

    public int promptList(String prompt, boolean required, int def, Object... options){
        String[] stringOptions = new String[options.length];
        for(int i = 0; i < options.length; i++){
            stringOptions[i] = options[i].toString();
        }
        return prompter.promptList(prompt, required, def, stringOptions);
    }

    public void startSpinner(String text, int interval, String... frames){
        spinner = new Spinner(text, interval, frames);
        spinner.show();
    }

    public void finishSpinner(String text){
        if(spinner == null)
            throw new IllegalStateException("No spinner running");
        spinner.finish(text);
        spinner = null;
    }

    public void errorSpinner(String text){
        if(spinner == null)
            throw new IllegalStateException("No spinner running");
        spinner.error(text);
        spinner = null;
    }

    private void moveUp(){
        moveUp(1);
    }

    private void moveUp(int lines){
        if(lines <= 0)
            throw new IllegalArgumentException("Number of lines must be > 0, was "+lines);
        CSI(lines + "F");
    }

    private void moveDown(){
        moveDown(1);
    }

    private void moveDown(int lines){
        if(lines <= 0)
            throw new IllegalArgumentException("Number of lines must be > 0, was "+lines);
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

    private void clearAfter(){
        CSI("J");
    }

    private void bold(){
        CSI("1m");
    }

    private void faint(){
        CSI("2m");
    }

    private void inverse(){
        CSI("7m");
    }

    private void normal(){
        CSI("22m");
    }

    private void inverseOff(){
        CSI("27m");
    }

    private void red(){
        CSI("31m");
    }

    private void green(){
        CSI("32m");
    }

    private void yellow(){
        CSI("33m");
    }

    private void light(){
        CSI("37m");
    }

    private void dark(){
        CSI("90m");
    }

    private void brightRed(){
        CSI("91m");
    }

    private void brightGreen(){
        CSI("92m");
    }

    private void brightYellow(){
        CSI("93m");
    }

    private void brightCyan(){
        CSI("96m");
    }

    private void white(){
        CSI("97m");
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

    private void print(int s){
        System.out.print(s);
    }

    private void println(int s){
        System.out.println(s);
    }

    private String readLine(String buffer){
        return reader.readLine(null, null, buffer);
    }
    
    private class Prompter{
        public String prompt(String prompt, String def, Function<String, String> validator){
            bold(); white();
            println(prompt);
            reset(); brightYellow();
            String line = def;
            String result;
            while((result = validator.apply(line = readLine(line))) != null){
                clearLine(); red();
                print(result);
                reset(); moveUp(); brightYellow();
            }
            clearLine(); moveUp();
            clearLine(); moveUp();
            bold(); white();
            print(prompt); print(" ");
            reset();
            if(line.isEmpty()){
                dark();
                println("Nothing entered");
            }else{
                brightCyan();
                println(line);
            }
            
            reset();
            return line;
        }

        public boolean yesOrNo(String prompt, boolean def){
            return yesOrNo(prompt, def, "Yes", "No");
        }

        public boolean yesOrNo(String prompt, boolean def, String yes, String no){
            hideCursor();
            boolean val = def;
            while(true){
                home(); clearLine(); bold(); white();
                print(prompt); print(" ");
                reset();

                reset(); dark();
                if(val){
                    brightGreen(); bold();
                }
                print(yes);
                reset(); dark();
                print("/");
                if(!val){
                    brightRed(); bold();
                }
                print(no);

                int input = raw.readBinding(booleanMap);
                if(input == 0){
                    val = !val;
                }else if(input == 1){
                    val = true;
                    break;
                }else if(input == 2){
                    val = false;
                    break;
                }else if(input == 3){
                    break;
                }
            }
            home(); clearLine(); bold(); white();
            print(prompt); print(" ");
            reset();
            brightCyan();
            println(val ? yes : no);
            reset();
            showCursor();

            return val;
        }

        public String promptSemVer(String prompt) throws Exception{
            hideCursor();
            bold(); white();
            println(prompt);
            reset();
            int major = 0;
            int minor = 0;
            int patch = 0;
            ArrayList<String> pre = null;
            ArrayList<String> build = null;
            int currentSection = 0;
            while(true){
                home();
                printSection(major, currentSection == 0);
                print(".");
                printSection(minor, currentSection == 1);
                print(".");
                printSection(patch, currentSection == 2);
                int buildThresh = 4;
                int drawing = 3;
                int end = 4;
                if(pre == null || pre.size() == 0){
                    dark();
                    print("-");
                    printSection("...", currentSection == drawing++);
                    reset();
                }else{
                    print("-");
                    for(int i = 0; i < pre.size(); i++){
                        printSection(pre.get(i), currentSection == drawing++);
                        if(i != pre.size() - 1)
                            print(".");
                    }
                    dark();
                    print(".");
                    printSection("...", currentSection == drawing++);
                    reset();
                    buildThresh += pre.size();
                }
                if(build == null || build.size() == 0){
                    dark();
                    print("+");
                    printSection("...", currentSection == drawing++);
                    reset();
                    end = buildThresh;
                }else{
                    print("+");
                    for(int i = 0; i < build.size(); i++){
                        printSection(build.get(i), currentSection == drawing++);
                        if(i != build.size() - 1)
                            print(".");
                    }
                    dark();
                    print(".");
                    printSection("...", currentSection == drawing++);
                    reset();
                    end = buildThresh + build.size();
                }
                clearAfter();

                int input = raw.readBinding(semVerMap);
                if(input == 0 && currentSection > 0){ // Left arrow
                    currentSection --;
                }else if(input == 1 && currentSection < end){ //Right arrow
                    currentSection ++;
                }else if(input == 2){ //Up arrow
                    switch(currentSection){
                        case 0: {major++; break;}
                        case 1: {minor++; break;}
                        case 2: {patch++; break;}
                    }
                }else if(input == 3){ //Down arrow
                    switch(currentSection){
                        case 0: {major = major > 0 ? major-1 : 0; break;}
                        case 1: {minor = minor > 0 ? minor-1 : 0; break;}
                        case 2: {patch = patch > 0 ? patch-1 : 0; break;}
                    }
                }else if(input == 4){ //Accept
                    break;
                }else if(input == 5){ //+
                    currentSection = buildThresh;
                }else if(input == 6){ //-
                    switch(currentSection){
                        case 0: case 1: case 2: {currentSection = 3; break;}
                        default: {
                            if(currentSection < buildThresh){
                                if(pre == null){
                                    pre = new ArrayList<>();
                                }
                                int index = currentSection - 3;
                                if(index == pre.size()){
                                    pre.add("");
                                }
                                pre.set(index, pre.get(index)+"-");
                            }else{
                                if(build == null){
                                    build = new ArrayList<>();
                                }
                                int index = currentSection - buildThresh;
                                if(index == build.size()){
                                    build.add("");
                                }
                                build.set(index, build.get(index)+"-");
                            }
                        }
                    }
                }else if(input == 7){ //.
                    if(currentSection < 2)
                        currentSection ++;
                    else if(currentSection < buildThresh - 1){
                        currentSection ++;
                    }else if(currentSection >= buildThresh && currentSection < end){
                        currentSection ++;
                    }
                }else if(input == 8){ //Bksp
                    switch(currentSection){
                        case 0: {major /= 10; break;}
                        case 1: {minor /= 10; break;}
                        case 2: {patch /= 10; break;}
                        default: {
                            if(currentSection < buildThresh-1){
                                if(pre != null && pre.size() > 0){
                                    int index = currentSection - 3;
                                    pre.set(index, delete(pre.get(index)));
                                    if(pre.get(index).length() == 0){
                                        pre.remove(index);
                                        if(currentSection < buildThresh - 2)
                                            currentSection --;
                                    }
                                }
                            }else if(currentSection >= buildThresh && currentSection < end){
                                if(build != null && build.size() > 0){
                                    int index = currentSection - buildThresh;
                                    build.set(index, delete(build.get(index)));
                                    if(build.get(index).length() == 0){
                                        build.remove(index);
                                        if(currentSection < end - 1)
                                            currentSection --;
                                    }
                                }
                            }
                        }
                    }
                }else if(input >= '0' && input <= '9'){
                    switch(currentSection){
                        case 0: {major = major * 10 + (input-48); break;}
                        case 1: {minor = minor * 10 + (input-48); break;}
                        case 2: {patch = patch * 10 + (input-48); break;}
                        default: {
                            if(currentSection < buildThresh){
                                if(pre == null){
                                    pre = new ArrayList<>();
                                }
                                int index = currentSection - 3;
                                if(index == pre.size()){
                                    pre.add("");
                                }
                                pre.set(index, pre.get(index)+((char)input));
                            }else{
                                if(build == null){
                                    build = new ArrayList<>();
                                }
                                int index = currentSection - buildThresh;
                                if(index == build.size()){
                                    build.add("");
                                }
                                build.set(index, build.get(index)+((char)input));
                            }
                        }
                    }
                }else if((input >= 'A' && input <= 'Z') || (input >= 'a' && input <= 'z')){
                    if(currentSection < 3)
                        continue;
                    if(currentSection < buildThresh){
                        if(pre == null){
                            pre = new ArrayList<>();
                        }
                        int index = currentSection - 3;
                        if(index == pre.size()){
                            pre.add("");
                        }
                        pre.set(index, pre.get(index)+((char)input));
                    }else{
                        if(build == null){
                            build = new ArrayList<>();
                        }
                        int index = currentSection - buildThresh;
                        if(index == build.size()){
                            build.add("");
                        }
                        build.set(index, build.get(index)+((char)input));
                    }
                }
            }
            StringBuilder result = new StringBuilder();
            result.append(major);
            result.append(".");
            result.append(minor);
            result.append(".");
            result.append(patch);
            if(pre != null && pre.size() > 0){
                result.append('-');
                result.append(String.join(".", pre));
            }
            if(build != null && build.size() > 0){
                result.append('+');
                result.append(String.join(".", build));
            }
            moveUp(); clearAfter();
            bold(); white();
            print(prompt); print(" ");
            normal(); brightCyan();
            println(result.toString());
            reset(); showCursor();
            return result.toString();
        }

        private void printSection(int val, boolean current){
            if(current){
                inverse();
                print(val);
                inverseOff();
            }else{
                print(val);
            }
        }

        private void printSection(String val, boolean current){
            if(current){
                inverse();
                print(val);
                inverseOff();
            }else{
                print(val);
            }
        }

        private String delete(String s){
            return s.substring(0, s.length()-1);
        }
    
        public int promptList(String prompt, boolean required, int def, String... options){
            clearLine(); hideCursor(); bold(); white();
            println(prompt);
            reset();
            
            int val = -1;
            if(options.length < LIST_SIZE)
                val = list(required ? "You must select an option" : null, def, options);
            else
                val = scrollingList(required ? "You must select an option" : null, def, options);
            
            moveUp(); clearLine();
            bold(); white();
            print(prompt); print(" ");
            reset();
            brightCyan();
            if(val == -1)
                println("Nothing selected");
            else
                println(options[val]);
            
            reset(); showCursor();
            return val;
        }

        private int list(String requiredMessage, int def, String... options){
            int current = def;
            String error = "";
            while(true){
                for(int i = 0; i < options.length; i++){
                    printListItem(options[i], i==current);
                }
                
                clearLine(); red();
                print(error);
                reset(); home();
    
                int input = raw.readBinding(listMap);
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
            moveUp(options.length);
            clearAfter();
            return current;
        }
    
        private int scrollingList(String requiredMessage, int def, String... options){
            int current = def;
            int start = (options.length+def-1)%options.length;
            String error = "";
            while(true){
                for(int pos = 0; pos < LIST_SIZE; pos++){
                    int i = (start + pos) % options.length;
                    printListItem(options[i], i==current);
                }
                dark();
                println("(Move to reveal additional choices)");
                
                clearLine(); red();
                print(error);
                reset(); home();
    
                int input = raw.readBinding(listMap);
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
            moveUp(LIST_SIZE+1);
            clearAfter();
            return current;
        }
    
        private void printListItem(String item, boolean current){
            clearLine();
            if(current){
                brightYellow();
                print("> "); println(item);
                reset();
            }else{
                light();
                print("  "); println(item);
                reset();
            }
        }
    }

    private class Spinner extends Thread{
        private String message;
        private int interval;
        private String[] frames;
    
        private boolean running = false;
        private int frame = 0;

        private long startTime = 0;
    
        public Spinner(String message, int interval, String... frames){
            this.message = message;
            this.interval = interval;
            this.frames = frames;
        }
    
        public void show(){
            running = true;
            startTime = System.currentTimeMillis();
            this.start();
        }
    
        public void error(String error){
            running = false;
            long time = System.currentTimeMillis() - startTime;
            try{
                this.join();
            }catch(InterruptedException e){}
            
            home(); clearLine();
            print(message);
            red(); bold();
            print(error); print(" ");
            normal(); dark();
            print((int)time); println("ms");
            reset();
        }
    
        public void finish(String success){
            running = false;
            long time = System.currentTimeMillis() - startTime;
            try{
                this.join();
            }catch(InterruptedException e){}
            
            home(); clearLine();
            print(message);
            green(); bold();
            print(success); print(" ");
            normal(); dark();
            print((int)time); println("ms");
            reset();
        }
    
        @Override
        public void run() {
            hideCursor();
            while(running){
                home(); clearLine();
                print(message); print(frames[frame]);
                frame = (frame+1) % frames.length;

                try{
                    Thread.sleep(interval);
                }catch(InterruptedException e){}
            }
            showCursor();
        }
    }
}