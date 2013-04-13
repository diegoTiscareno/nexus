/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IDE;

import java.io.Serializable;

/**
 *
 * @author diego
 */
public class ColorToken implements Serializable{
    
    private static final long serialVersionUID = 696L;
    private String lexema = null;
    private TipoToken tipoToken;
    private int indice;
    private int linea;
    private int index;

    public static enum TipoToken{
        ERROR, EOF, IF, THEN, ELSE, END, DO, UNTIL, WHILE, COUT, CIN, INT,
        REAL, MAIN, PLUS, MINUS, ASTERISK, SLASH, LESS_THAN, GREATER_THAN, 
        LESS_EQUAL, GREATER_EQUAL, EQUALS, NOT_EQUALS, ASSIGN, SEMICOLON,
        COMMA, LEFT_PARENS, RIGHT_PARENS, LEFT_CURLY, RIGHT_CURLY, COMMENT,
        LINE_COMMENT, ITERATION, DECREMENT, IDENTIFIER, NUMBER
    }
    
    public static final String[] literalesToken={
        "error", "fin de archivo", "if", "then", "else", "end", "do", "until",
        "while", "cout", "cin", "int", "real", "main", "más", "menos",
        "asterisco", "diagonal", "menor que", "mayor que", "menor o igual", 
        "mayor o igual", "asignación", "punto y coma", "coma", 
        "parentesis izquierdo", "parentesis derecho", "llave izquierda", 
        "llave derecha", "comentario", "comentario de línea", "iteracion",
        "decremento", "identificador", "número"
    };
    
    
    /**
     * @return El lexema correspondiente a éste token.
     */
    public String getLexema() {
        return lexema;
    }

    /**
     * @return El tipo de token correspondiete a éste token.
     */
    public TipoToken getTipoToken() {
        return tipoToken;
    }
    
    /**
     * @param lexema El lexema a establecer para éste token
     */
    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    /**
     * @param tipoToken Establece el tipo de token al que este 
     * pertenece.
     */
    public void setTipoToken(TipoToken tipoToken) {
        this.tipoToken = tipoToken;
    }
    
    /**
     * @return El indice en el que éste caracter se encuentra 
     */
    public int getIndice(){
        return this.indice;
    }
    
    /**
     * @param indice Se establecerá como el nuevo índice donde se encuentra el
     * token presente
     */
    public void setIndice(int indice){
        this.indice = indice;
    }
    
    /**
     * @return La línea en el que éste caracter se encuentra 
     */
    public int getLinea(){
        return this.linea;
    }
    
    /**
     * @param linea Se establecerá como la nueva línea donde se encuentra el
     * token presente
     */
    public void setLinea(int linea){
        this.linea = linea;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColorToken) {
            ColorToken tok;
            tok = (ColorToken)obj;
            if (tok.getIndice() == indice && tok.getLexema().equals(lexema) &&
                    tok.getLinea() == linea && tok.getTipoToken() == tipoToken){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public boolean isEofToken(){
        if (this.tipoToken == TipoToken.EOF){
            return true;
        }
        return false;
    }
    
    public boolean selectReservedWord(){
        if (this.lexema != null){
            if(ColorToken.isReservedWord(lexema)){
                switch (lexema) {
                    case "if":
                        this.tipoToken = TipoToken.IF;
                        break;
                    
                    case "then":
                        this.tipoToken = TipoToken.THEN;
                        break;
                    case "else":
                        this.tipoToken = TipoToken.ELSE;
                        break;
                    case "end":
                        this.tipoToken = TipoToken.END;
                        break;
                    case "do":
                        this.tipoToken = TipoToken.DO;
                        break;
                    case "while":
                        this.tipoToken = TipoToken.WHILE;
                        break;
                    case "until":
                        this.tipoToken = TipoToken.UNTIL;
                        break;
                    case "cout":
                        this.tipoToken = TipoToken.COUT;
                        break;
                    case "cin":
                        this.tipoToken = TipoToken.CIN;
                        break;
                    case "int":
                        this.tipoToken = TipoToken.INT;
                        break;
                    case "real":
                        this.tipoToken = TipoToken.REAL;
                        break;
                    case "main":
                        this.tipoToken = TipoToken.MAIN;
                        break;
                }
            } else {
                this.tipoToken = TipoToken.IDENTIFIER;
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isReservedWord(String word){
        final String[] literalesPalabrasReservadas ={
        "if", "then", "else", "end", "do", "until", "while", "cout", "cin", 
        "int", "real", "main"
        };
        if (word != null) {
            for (String string : literalesPalabrasReservadas) {
                if (word.equals(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Token: " + tipoToken.toString() + " en [" + linea + ", " + indice + "] Lexema: " + lexema + " Index: " + index; 
    }
    
    
}
