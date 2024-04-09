package termproject;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, String> symbolTable;

    public SymbolTable() {
        this.symbolTable = new HashMap<>();
    }

    public void insert(String identifier, String type) {
        symbolTable.put(identifier, type);
    }

    public void display() {
        System.out.println("Symbol Table:");
        for (String key : symbolTable.keySet()) {
            System.out.println("Identifier: " + key + ", Type: " + symbolTable.get(key));
        }
    }
}

