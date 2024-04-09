package termproject;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Scanner{
    /* Variables */
    
    static int charClass;
    static char[] lexeme= new char[100];
    static char nextChar;
    static int lexLen;
    static int nextToken;
    static File in_fp;
    static FileInputStream fileInputStream;
    static InputStreamReader inputStreamReader;

    /* Function declarations 
    void addChar();
    void getChar();
    void getNonBlank();
    int lex();
    */
    
    /* Character classes */
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int UNKNOWN = 99;
    
    /* Token codes */
    static final int INT_LIT = 10; //integer literal
    static final int INT_ID = 10;
    static final int FLOAT_ID = 11; //integer literal
    static final int DOUBLE_ID = 12;
    static final int DEC_LIT = 13;
    static final int IDENT = 14;  //Identifier token code
    static final int ASSIGN_OP =  20;
    static final int COLON = 21;
    static final int EQUALS = 23;
    static final int COMMA = 24;
    static final int  ADD_OP = 25;
    static final int SUB_OP = 26;
    static final int MULT_OP = 27;
    static final int DIV_OP = 28;
    static final int LEFT_PAREN = 29;
    static final int RIGHT_PAREN = 30;
    static final int SEMI_COLON = 31;
    static final int LESS_THAN = 32;
    static final int GREATER_THAN = 33;
    static final int NOT_EQUALS = 34;
    /* Reserved Words */
    static final int PROGRAM = 35;
    static final int BEGIN = 36;
    static final int END = 37;
    static final int IF = 38;
    static final int THEN = 39;
    static final int ELSE = 40;
    static final int WHILE = 41;
    static final int LOOP = 41;
    static final int INPUT = 42;
    static final int OUTPUT = 43;

    static final int EOF = -1;
    static int lineNumber = 0;
    /* main driver */
    public static void main(String[] args){
        // Check if there are arguments
        try{
           //open the file for reading and process its contents
        in_fp = new File("C:\\Users\\kelli\\OneDrive\\Documents\\CSII\\TermProject\\test\\test7.txt");
        fileInputStream = new FileInputStream(in_fp);
        inputStreamReader = new InputStreamReader(fileInputStream);
        
        getChar();
        do{
            lexeme = new char[100]; //LEXEME ARRAY MUST BE EMPTIED OUT FOR NEW LEXEME, OTHERWISE SOME CHARACTERS MAY BE LEFT OVER FROM PREVIOUS 
            lex();
        }
        while(nextToken != EOF);
            inputStreamReader.close();
        }

        catch (IOException e){
            System.err.println("Error reading the file: " + e.getMessage());
        } 
    }
    /*****************************************************/

    /* lookupReserved -a function to lookup if lexeme is a reserved word */
    static int lookupReserved(String lexeme){
        switch (lexeme) {
        case "program":
            return PROGRAM;
        case "begin":
            return BEGIN;
        case "end":
            return END;
        case "if":
            return IF;
        case "then":
            return THEN;
        case "else":
            return ELSE;
        case "while":
            return WHILE;
        case "loop":
            return LOOP;
        case "input":
            return INPUT;
        case "output":
            return OUTPUT;
        case "int":
            return INT_ID;
        case "float":
            return FLOAT_ID;
        case "double":
            return DOUBLE_ID;
        default:
            return IDENT; // If not a reserved word, it's an identifier
    }
    }

    /* lookup - a function to lookup operators and parentheses
    and return the token */

    static int lookup(char ch) {
    switch (ch) {
        case '(':
            addChar();
            nextToken = LEFT_PAREN;
            break;
        case ')':
            addChar();
            nextToken = RIGHT_PAREN;
            break;
        case '+':
            addChar();
            nextToken = ADD_OP;
            break;
        case '-':
            addChar();
            nextToken = SUB_OP;
            break;
        case '*':
            addChar();
            nextToken = MULT_OP;
            break;
        case '/':
            addChar();
            nextToken = DIV_OP;
            break;
        case '<':
            addChar();
            nextToken = LESS_THAN;
            break;
        case '>':
            addChar();
            nextToken = GREATER_THAN;
            break;
        case ':':  // Place the colon case before the unknown case
            addChar();
            nextToken = COLON;
            break;
        case ',':
            addChar();
            nextToken = COMMA;
            break;
        case '=':
            addChar();
            nextToken = EQUALS;
            break;
        case ';':
            addChar();
            nextToken = SEMI_COLON;
            break;
        default:
            addChar();
            nextToken = UNKNOWN;
            break;
    }
    return nextToken;
}

    /*****************************************************/
    /* addChar - a function to add nextChar to lexeme */
    static void addChar() {
        if (lexLen <= 98) {
        lexeme[lexLen++] = nextChar;
        lexeme[lexLen] = 0;
        }else
            System.out.print("Error - lexeme is too long \n");
    }
    /*****************************************************/
    /* getChar - a function to get the next character of
    input and determine its character class */

    static void getChar() throws IOException {
        int nextCharValue;
        if ((nextCharValue = inputStreamReader.read()) != -1) {
            nextChar = (char) nextCharValue;
            if (Character.isAlphabetic(nextChar))
                charClass = LETTER;
            else if (Character.isDigit(nextChar))
                charClass = DIGIT;
            else 
                charClass = UNKNOWN;
        } else {
            charClass = EOF;
        }
        if (nextChar == '\n') {
            lineNumber++; // Increment the line number when a new line character is encountered
        }
    }
    /*****************************************************/
    /* getNonBlank - a function to call getChar until it
    returns a non-whitespace character */
    static void getNonBlank() throws IOException {
        while ((Character.isWhitespace(nextChar) || nextChar == 0) && charClass != EOF)
            getChar();
        lexeme = new char[100]; // Reset lexeme after skipping whitespace
    }
    /*****************************************************/
    /* lex - a simple lexical analyzer for arithmetic
    expressions */
static String lexemeToString() {
    String lexemeStr = String.valueOf(lexeme); // Trim the lexeme string
    return lexemeStr;
}
static int lex() throws IOException {
    lexLen = 0;
    getNonBlank();
    switch (charClass) {
        case LETTER:
            addChar();
            getChar();
            while (charClass == LETTER || charClass == DIGIT || nextChar == '_') {
                addChar();
                getChar();
            }
            String lexemeStr = String.valueOf(lexeme).trim(); // Trim the lexeme string
            nextToken = lookupReserved(lexemeStr); // check if it's a reserved word
            if (nextToken == -1) {
                nextToken = IDENT; // If not a reserved word, it's an identifier
            }
            break;
        /* Parse integers/decimals literals */
        case DIGIT:
            boolean isDecimal = false;
            int digitCount = 0; // Initialize the digit count
            addChar();
            getChar();
            while (charClass == DIGIT || (nextChar == '.' && !isDecimal)) {
                if (nextChar == '.') {
                    isDecimal = true;
                }
                if (digitCount < 10) {
                    addChar();
                } else {
                    // Error out if the digit count exceeds 10
                    System.err.println("Error: Number exceeds 10 digits");
                    System.exit(0); // Exit the program
                }
                digitCount++;
                getChar();
            }
            if (isDecimal) {
                nextToken = DEC_LIT; // Assign the token for float values
            } else {
                nextToken = INT_LIT; // Assign the token for integer literals
            }
            break;
        /* Parentheses and operators */
        case UNKNOWN:
            switch (nextChar) {
                case ':': //if its a : then check if it is assignment operator or if its just :
                    addChar();
                    getChar();
                    if (nextChar == '='){
                        addChar();
                        nextToken = ASSIGN_OP;
                        getChar();
                    } else {
                        nextToken = COLON;
                    }
                    break;
                case '<': //if its a < then check if it is a not_equals or if its just <
                    addChar();
                    getChar();
                    if (nextChar == '>') {
                        addChar();
                        nextToken = NOT_EQUALS;
                        getChar();
                    } else {
                        nextToken = LESS_THAN;
                    }
                    break;
                default:
                    nextToken = lookup(nextChar); // Call lookup for other unknown characters
                    getChar();
                    break;
            }
            break;
        /* EOF */
        case EOF:
            nextToken = EOF;
            lexeme[0] = 'E';
            lexeme[1] = 'O';
            lexeme[2] = 'F';
            lexeme[3] = 0;
            break;
    } /* End of switch */
    System.out.printf("Next token is: %d, Next lexeme is %s\n", nextToken, String.valueOf(lexeme));
    return nextToken;
}
}
