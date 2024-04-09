package termproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Parser {
    /* Token types */
    static int lookahead;
    static HashMap<String, String> symbolTable = new HashMap<>();
    static final int PROGRAM = Scanner.PROGRAM;
    static final int IDENT = Scanner.IDENT;
    static final int BEGIN = Scanner.BEGIN;
    static final int UNKNOWN = Scanner.UNKNOWN;
    static final int INT_ID = Scanner.INT_ID;
    static final int FLOAT_ID = Scanner.FLOAT_ID;
    static final int DOUBLE_ID = Scanner.DOUBLE_ID;
    static final int COLON = Scanner.COLON;
    static final int SEMI_COLON = Scanner.SEMI_COLON;
    static final int COMMA = Scanner.COMMA;
    static final int END = Scanner.END;
    static final int IF = Scanner.IF;
    static final int WHILE = Scanner.WHILE;
    static final int INPUT = Scanner.INPUT;
    static final int OUTPUT = Scanner.OUTPUT;
    static final int ASSIGN_OP = Scanner.ASSIGN_OP;
    static final int THEN = Scanner.THEN;
    static final int ELSE = Scanner.ELSE;
    static final int EQUALS = Scanner.EQUALS;
    static final int NOT_EQUALS = Scanner.NOT_EQUALS;
    static final int LESS_THAN = Scanner.LESS_THAN;
    static final int GREATER_THAN = Scanner.GREATER_THAN;
    static final int LEFT_PAREN = Scanner.LEFT_PAREN;
    static final int RIGHT_PAREN = Scanner.RIGHT_PAREN;
    static final int INT_LIT = Scanner.INT_LIT;
    static final int DEC_LIT = Scanner.DEC_LIT;
    static final int ADD_OP = Scanner.ADD_OP;
    static final int SUB_OP = Scanner.SUB_OP;
    static final int MULT_OP = Scanner.MULT_OP;
    static final int DIV_OP = Scanner.DIV_OP;
    static final int LOOP = Scanner.LOOP;
    
    // Rule 01: PROGRAM
    static void program() throws IOException {
        switch (lookahead) {
            case PROGRAM: //if the token is program, print program, get the next token
                System.out.println("PROGRAM");
                lookahead = Scanner.lex();
                if (lookahead == IDENT) { //if it is an identifier, then we are in a decl sec
                    decl_sec();
                }

                if (lookahead == BEGIN) { //if it is begin, then we are in the stmt section
                    lookahead = Scanner.lex(); //get the first token after begin
                    stmt_sec(); 
                } else { //otherwise, check if it is an unknown
                    if (lookahead != UNKNOWN){ //if it isnt, then we are missing begin
                        handleBeginError();
                    } else {
                        handleUnknownTokenError(); //if it is unknown, then error
                    }
                }
                
                break;
            case UNKNOWN: //if the first token is an unknown, then error
                handleUnknownTokenError();
                break;
            default: //otherwise, we are missing "program"
                handleMissingProgramError();
                break;
        }
    }
    //Rule 02: DECL_SEC
    static void decl_sec() throws IOException {
    while (lookahead == IDENT) { //while we have an identifier, we are in a decl section, then call decl
        System.out.println("DECL_SEC");
        decl();
    }
    }
    // Rule 03: DECL
    static void decl() throws IOException {
        System.out.println("DECL"); 
        if (lookahead == IDENT) { //if it is an identifier, then save the name of the identifier
            String identifier = Scanner.lexemeToString().trim(); 
            if (symbolTable.containsKey(identifier)) { //if this identifier is in the symbol table, then error
                throw new RuntimeException("Error - Redeclaration of variable: " + identifier);
            } else {   //otherwise we add the identifier to the symbol table with null type
                symbolTable.put(identifier, null); 
            }
            id_list();//call id_list
            if (lookahead == COLON) { //if next token is a colon
                lookahead = Scanner.lex(); // then get the next token, which should be a type
                String type = type(); 
                for (String key : symbolTable.keySet()) { //get the type from the type function, and save to symbol table
                    if (symbolTable.get(key) == null) {
                        symbolTable.put(key, type); 
                    }
                }
                if (lookahead != SEMI_COLON) { //if next token is not a semi colon, then error
                    handleStmtTerminatorError();
                }
                lookahead = Scanner.lex(); //otherwise, get the next token
            }
        } else if (lookahead == UNKNOWN) { //if token is unknown, then error
            handleUnknownTokenError();
        }
    }
    // Rule 04: ID_LIST
    static void id_list() throws IOException {
        System.out.println("ID_LIST");
        switch (lookahead) { 
            case IDENT: //if the next token is an identifier, then get the identifier 
                String identifier = Scanner.lexemeToString().trim(); 
                symbolTable.put(identifier, null); // Add the identifier to the symbol table with a null type initially 
                lookahead = Scanner.lex();
                if (lookahead == COMMA) {
                    lookahead = Scanner.lex();
                    id_list();
                }
                break;
            case UNKNOWN:
                handleUnknownTokenError();
            default:
                handleInvalidID();
        }
    }
    // RULE 06: STMT_SEC
    static void stmt_sec() throws IOException {
        while (lookahead != END && lookahead != ELSE){ //check if token is end or else first, then call stmt if its not
            System.out.println("STMT_SEC");
            stmt();
        }
        if (lookahead == ELSE || lookahead == END){ //return if end or else is found
            return;
        }

        if (lookahead != IF && lookahead != SEMI_COLON){ //if we return and it is not If or ;, then its an error
            handleStmtTerminatorError();
        }
    }
    // RULE 07: STMT
    static void stmt() throws IOException {
        switch (lookahead) {
            case IDENT: //if its an identifier, then we are in an assignment
                System.out.println("STMT");
                assign();
                break;
            case IF: //if it is If then we are in an if stmt
                System.out.println("STMT");
                ifstmt();
                break;
            case WHILE: //if it is While then we are in while stmt
                System.out.println("STMT");
                whilestmt();
                break;
            case INPUT: //if it is Input then we are in an input
                System.out.println("STMT");
                input();
                break;
            case OUTPUT: //if it is Output, then we are in output
                System.out.println("STMT");
                output();
                break;
            case UNKNOWN: //if it is unknown, then error
                handleUnknownTokenError();
                break;
            default: //otherwise, it is an invalid statement
                handleInvalidStmt();
                break;
        }
        if (lookahead == SEMI_COLON) { //if we return and it is a ;, then get the next token and check for end
            lookahead = Scanner.lex();
            if (lookahead == END){
                return;
            }
            stmt_sec();
        } else if (lookahead == UNKNOWN){
             handleUnknownTokenError();
        } else{
            handleStmtTerminatorError();
        }
        
    }
    // RULE 08: ASSIGN
    static void assign() throws IOException {
        System.out.println("ASSIGN");
        if (lookahead == IDENT){ //if its an identifier, then get the next one
            lookahead = Scanner.lex();
            if (lookahead == UNKNOWN) { //check if it is unknown
                handleUnknownTokenError();
            }
            if (lookahead == ASSIGN_OP){ //check if it is an assignment operator
                lookahead = Scanner.lex(); //get next token
                expr(); //call expr
            }else { //otherwise, we are missing the assignment operator
                throw new RuntimeException("Error at line " + Scanner.lineNumber + ": Missing assignment operator");
            }
        } else { //otherwise we are missing an identifier
            throw new RuntimeException("Error at line " + Scanner.lineNumber + ": missing an identifer in assignment");
        }
    }
    // RULE 09: IFSTMT
    static void ifstmt() throws IOException {
        System.out.println("IF_STMT"); 
        lookahead = Scanner.lex(); //get token after if
        if (lookahead == UNKNOWN) { //check if unknown
            handleUnknownTokenError();
        }
        if (lookahead == LEFT_PAREN) { //check if (, then call comp
            comp(); 
            if (lookahead == UNKNOWN) { //if it returns an unknown, then error
                handleUnknownTokenError();
            }
            if (lookahead == THEN) { //check if THEN, then call stmt_sec
                lookahead = Scanner.lex();
                stmt_sec();
                if (lookahead == ELSE) {
                    lookahead = Scanner.lex();
                    stmt_sec();
                } //check if end
                if (lookahead != END) {
                    throw new RuntimeException("Error at line " + Scanner.lineNumber + ": Missing keyword 'end'");
                }
                lookahead = Scanner.lex(); // Move to the next token after encountering 'end'
                if (lookahead != IF){ //check if if
                    throw new RuntimeException("Error at line " + Scanner.lineNumber + ": Missing keyword 'if'");
                }
                lookahead = Scanner.lex();
                if (lookahead != SEMI_COLON) { //check if semi_colon is missing
                    handleStmtTerminatorError();
                }
            } else { //othewise we are missing keyword then
                
                throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing keyword 'then");
            }
        } else { //otherwise we are missing a comparison
            throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing comparison");
        }
    }
    // RULE 10: WHILESTMT
    static void whilestmt() throws IOException {
        System.out.println("WHILE_STMT");
        lookahead = Scanner.lex(); //get token after While
        if (lookahead == LEFT_PAREN){ //check if (
            comp(); //call comp
            if (lookahead == LOOP){ //check if comp returns "loop"
                lookahead = Scanner.lex(); //get token after "loop"
                stmt_sec(); 
                if (lookahead != END){ //if stmt_sec does not return end, then error
                     throw new RuntimeException("Error at line " + Scanner.lineNumber + ": Missing keyword 'end");
                }
                lookahead = Scanner.lex(); // Move to the next token after encountering 'end'
                if (lookahead != LOOP){// if next token is not "loop", then error
                    if (lookahead == UNKNOWN){
                         handleUnknownTokenError();
                    } else{
                        throw new RuntimeException("Error at line " + Scanner.lineNumber + ": Missing keyword 'loop");
                    }
                }
                lookahead = Scanner.lex(); //get next token
                if (lookahead != SEMI_COLON) { //check for semi_colon
                    handleStmtTerminatorError();
                }
            } else {
                if (lookahead == UNKNOWN){
                     handleUnknownTokenError();
                } else{
                    throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing keyword 'loop");
                }
            }
        } else {
            throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing comparison");
        }
    }
    // RULE 11: INPUT
    static void input() throws IOException {
        System.out.println("INPUT"); 
        lookahead = Scanner.lex(); 
        id_list(); //get next token, and call id list
    }
    // RULE 12: OUTPUT
    static void output() throws IOException {
        System.out.println("OUTPUT");
        lookahead = Scanner.lex(); //get next token
        if (lookahead == IDENT){ //check if its an id
            id_list(); //call id_list
        }
        else if (lookahead == INT_LIT || lookahead == DEC_LIT){ //otherwise, check if its a valid digit
            lookahead = Scanner.lex(); //get next token
        } else {
            throw new RuntimeException("Error at line " + Scanner.lineNumber +": invalid output");
        }
    }
    // Rule 13: EXPR -> FACTOR | FACTOR + EXPR | FACTOR - EXPR
    static void expr() throws IOException {
        System.out.println("EXPR"); 
        factor(); //call factor
        if (lookahead == ADD_OP || lookahead == SUB_OP) { //if factor returns a valid operator, then call expr
            lookahead = Scanner.lex();
            expr();
        }
    }

    // Rule 14: FACTOR -> OPERAND | OPERAND * FACTOR | OPERAND / FACTOR
    static void factor() throws IOException {
        System.out.println("FACTOR");
        operand(); //call operand
        if (lookahead == MULT_OP || lookahead == DIV_OP) { //check if operand returns a valid operator, then call factor
            lookahead = Scanner.lex();
            factor();
        }
    }

    // Rule 15: OPERAND -> NUM | ID | ( EXPR )
    static void operand() throws IOException {
        System.out.println("OPERAND");
        if (lookahead == INT_LIT || lookahead == DEC_LIT || lookahead == IDENT) { //check if its a valid digit or id
            lookahead = Scanner.lex();
        } else if (lookahead == LEFT_PAREN) { //otherwise check if its an expr
            lookahead = Scanner.lex();
            expr();
            if (lookahead == RIGHT_PAREN) { // check if it is valid expr with )
                lookahead = Scanner.lex();
            } else {
                throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing ')'");
            }
        } else { //check if it is unknown
            if (lookahead == UNKNOWN) {
                handleUnknownTokenError();
            }
            throw new RuntimeException("Error at line " + Scanner.lineNumber +": Invalid operand");
        }
    }
    // Rule 17: COMP -> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )
    static void comp() throws IOException {
        System.out.println("COMP");
        if (lookahead == LEFT_PAREN) { //check if its (
            lookahead = Scanner.lex();
            operand(); //call operand with next token
            if (lookahead == EQUALS || lookahead == NOT_EQUALS || lookahead == LESS_THAN || lookahead == GREATER_THAN) { //check if valid comp
                lookahead = Scanner.lex();
                operand(); //call operand with next token
                if (lookahead == RIGHT_PAREN) { //check if valid comp with )
                    lookahead = Scanner.lex();
                } else {
                    throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing ')'");
                }
            } else {
                if (lookahead == UNKNOWN){
                     handleUnknownTokenError();
                }else {
                    throw new RuntimeException("Error at line " + Scanner.lineNumber +": Not a valid comparison operator");
                }
            }
        } else {
            throw new RuntimeException("Error at line " + Scanner.lineNumber +": Missing '('");
        }
    }
    // RULE 18: TYPE
    static String type() throws IOException { //check if valid type, otherwise error
        String type = null;
        if (lookahead == INT_ID || lookahead == FLOAT_ID || lookahead == DOUBLE_ID) {
            type = Scanner.lexemeToString().trim();
            lookahead = Scanner.lex(); // move to the next token
        } else {
           handleInvalidType();
        }
        return type;
    }
    
    static void handleStmtTerminatorError() { //error for missing ;
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Keyword ';' is missing");
    }
    static void handleBeginError() { //error for missing 'begin'
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Keyword 'begin' is missing");
    }
    static void handleUnknownTokenError() { //error for unknown token
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Unknown token");
    }
    static void handleMissingProgramError() { //error for missing 'program'
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Keyword 'program' is missing");
    }
    static void handleInvalidID() { //error for an invalid id 
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Invalid identifier");
    }
    static void handleInvalidType() { //error for invalid type
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Invalid type");
    }
    static void handleInvalidStmt() { //error for inavlid statement
        throw new RuntimeException("Error at line " + Scanner.lineNumber +": Invalid statement");
    }
    public static void main(String[] args) {
        String errorMessage = null; //initialize as null
        try {
            Scanner.in_fp = new File("C:\\Users\\kelli\\OneDrive\\Documents\\CSII\\TermProject\\test\\test6.txt"); // input file
            Scanner.fileInputStream = new FileInputStream(Scanner.in_fp); //read from file
            Scanner.inputStreamReader = new InputStreamReader(Scanner.fileInputStream); //read file using Scanner
            lookahead = Scanner.lex(); //lookahead is our next token
            program(); //call program
    
        } catch (IOException e) { //if it fails wtih IOException, then there is a file error
            errorMessage = "Error - File not found";
        } catch (RuntimeException e) { //if it fails with a runtimeexception then get the message
            errorMessage = e.getMessage();
        } finally {
            if (errorMessage != null) {
                System.err.println(errorMessage); //print the error message
            }
        }
    }
}
