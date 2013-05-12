/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiny;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;


/**
 *
 * @author diego
 */
public class TinyLexico {
    
    int indice; //aka columna
    int linea;
    int indiceComentario;
    int lineaComentario;
    int index;
    
    File entrada;
    FileReader fileReader;
    BufferedReader bReader;
    
    private static enum Estado{
        INICIO, FIN, ERROR, ID, NUMERO, REAL_INICIA, REAL, MAS,
        MAS_MAS, MENOS, MENOS_MENOS, MENOR, MENOR_IGUAL, MAYOR, MAYOR_IGUAL,
        ASIGNACION, IGUAL_QUE, ADMIRACION, DIFERENTE, DIAGONAL,
        COMENTARIO, ASTERISCO_COM, COMENTARIO_LINEA
    }
    
    public TinyLexico(File entrada) throws FileNotFoundException{
        this.entrada = entrada;
        indice = linea= index= 0;
        fileReader = new FileReader (entrada);
        bReader = new BufferedReader(fileReader);
    }
    
    public Token next(){
        Estado estado;
        Token token = new Token();
        StringBuffer lexema = new StringBuffer(128);
        int caracter;
        estado = Estado.INICIO;
        try {
            while (estado != Estado.FIN) {
                switch (estado) {
                    case INICIO:
                        caracter = bReader.read();
                        incrementarIndice(caracter);
                        /*
                         * Omitiendo delimitadores
                         */
                        while(Character.isWhitespace(caracter)){
                            caracter = bReader.read();
                            incrementarIndice(caracter);
                        }
                        /*
                         * Se comienza a cambiar de estado según la entrada, o 
                         * se detectan tokens de un solo caracter.
                         */
                        if (Character.isAlphabetic(caracter)) {
                            estado = Estado.ID;
                            lexema.append(Character.toChars(caracter));
                            bReader.mark(1);
                        } else if (Character.isDigit(caracter)){
                            estado = Estado.NUMERO;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '+'){
                            estado = Estado.MAS;
                            lexema.append(Character.toChars(caracter));
                            bReader.mark(caracter);
                        } else if ((char) caracter == '-'){
                            estado = Estado.MENOS;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '<'){
                            estado = Estado.MENOR;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '>'){
                            estado = Estado.MAYOR;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '='){
                            estado = Estado.ASIGNACION;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '/'){
                            estado = Estado.DIAGONAL;
                            bReader.mark(1024);
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '!'){
                            estado = Estado.ADMIRACION;
                            lexema.append(Character.toChars(caracter));
                        } else if ((char) caracter == '*'){
                            estado = Estado.FIN;
                            token.setLexema("*");
                            token.setTipoToken(Token.TipoToken.ASTERISK);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == ';'){
                            estado = Estado.FIN;
                            token.setLexema(";");
                            token.setTipoToken(Token.TipoToken.SEMICOLON);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == ','){
                            estado = Estado.FIN;
                            token.setLexema(",");
                            token.setTipoToken(Token.TipoToken.COMMA);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == '('){
                            estado = Estado.FIN;
                            token.setLexema("(");
                            token.setTipoToken(Token.TipoToken.LEFT_PARENS);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == ')'){
                            estado = Estado.FIN;
                            token.setLexema(")");
                            token.setTipoToken(Token.TipoToken.RIGHT_PARENS);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == '{'){
                            estado = Estado.FIN;
                            token.setLexema("{");
                            token.setTipoToken(Token.TipoToken.LEFT_CURLY);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if ((char) caracter == '}'){
                            estado = Estado.FIN;
                            token.setLexema("}");
                            token.setTipoToken(Token.TipoToken.RIGHT_CURLY);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        } else if (caracter == -1){
                            estado = Estado.FIN;
                            token.setLexema(null);
                            token.setTipoToken(Token.TipoToken.EOF);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(new String(Character.toChars(caracter)));
                            token.setTipoToken(Token.TipoToken.ERROR);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        }
                        /*
                         * Se Define qué hacer en los diferentes estados según
                         * los autómatas que se proporcionan en la documentación
                         * 
                         */
                        break;
                    case ID:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if (Character.isAlphabetic(caracter) || Character.isDigit(caracter) || (char) caracter == '_'){
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.selectReservedWord();
                            token.setIndice(indice - token.getLexema().length());
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                        }
                        break;
                    case MAS:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if((char) caracter == '+') {
                            estado = Estado.MAS_MAS;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.PLUS);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case MAS_MAS:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.ITERATION);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case MENOS:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if((char) caracter == '-') {
                            estado = Estado.MENOS_MENOS;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.MINUS);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case MENOS_MENOS:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.DECREMENT);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case NUMERO:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if(Character.isDigit(caracter)){
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else if((char) caracter == '.') {
                            estado = Estado.REAL_INICIA;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.NUMBER);
                            token.setIndice(indice - token.getLexema().length());
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case REAL_INICIA:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if (Character.isDigit(caracter)) {
                            estado = Estado.REAL;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(".");
                            token.setTipoToken(Token.TipoToken.ERROR);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case REAL:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if (Character.isDigit(caracter)) {
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.REAL_NUMBER);
                            token.setIndice(indice - token.getLexema().length());
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case ADMIRACION:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if ((char) caracter == '=') {
                            estado = Estado.DIFERENTE;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema("!");
                            token.setTipoToken(Token.TipoToken.ERROR);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case DIFERENTE:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.NOT_EQUALS);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case MENOR:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if ((char) caracter == '=') {
                            estado = Estado.MENOR_IGUAL;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.LESS_THAN);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case MENOR_IGUAL:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.LESS_EQUAL);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case MAYOR:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if ((char) caracter == '=') {
                            estado = Estado.MAYOR_IGUAL;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.GREATER_THAN);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case MAYOR_IGUAL:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.GREATER_EQUAL);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case ASIGNACION:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if ((char) caracter == '=') {
                            estado = Estado.IGUAL_QUE;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.ASSIGN);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case IGUAL_QUE:
                        estado = Estado.FIN;
                        token.setLexema(lexema.toString());
                        token.setTipoToken(Token.TipoToken.EQUALS);
                        token.setIndice(indice-2);
                        token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                        break;
                    case DIAGONAL:
                        caracter = bReader.read();
                        bReader.mark(1000);
                        if ((char) caracter == '/') {
                            estado = Estado.COMENTARIO_LINEA;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else if((char) caracter == '*') {
                            estado = Estado.COMENTARIO;
                            indiceComentario = indice;
                            lineaComentario = linea;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else{
                            estado = Estado.FIN;
                            index += 1;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.SLASH);
                            token.setIndice(indice-1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                        }
                        break;
                    case COMENTARIO_LINEA:
                        bReader.mark(1);
                        caracter = bReader.read();
                        if ((char) caracter != '\n' &&
                                 caracter != -1) {
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString());
                            token.setTipoToken(Token.TipoToken.LINE_COMMENT);
                            token.setIndice(indice - token.getLexema().length() + 1);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            bReader.reset();
                            
                        }
                        break;
                    case COMENTARIO:
                        caracter = bReader.read();
                        if ((char) caracter != '*' && 
                                caracter != -1) {
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else if ((char)caracter == '*'){
                            estado = Estado.ASTERISCO_COM;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else {
                            estado = Estado.FIN;
                            indice = indiceComentario;
                            linea = lineaComentario;
                            token.setLexema("/*");
                            token.setIndice(indice);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            token.setTipoToken(Token.TipoToken.ERROR);
                            bReader.reset();
                        }
                        break;
                    case ASTERISCO_COM:
                        caracter = bReader.read();
                        if ((char) caracter != '/' && 
                                caracter != -1) {
                            estado = Estado.COMENTARIO;
                            lexema.append(Character.toChars(caracter));
                            incrementarIndice(caracter);
                        } else if ((char)caracter == '/'){
                            estado = Estado.FIN;
                            token.setLexema(lexema.toString() + "/");
                            token.setTipoToken(Token.TipoToken.COMMENT);
                            token.setIndice(indiceComentario-1);
                            token.setLinea(lineaComentario);
                            token.setIndex(index-token.getLexema().length());
                            index++;
                        } else {
                            estado = Estado.FIN;
                            token.setLexema("/*");
                            indice = indiceComentario;
                            linea = lineaComentario;
                            token.setIndice(indice);
                            token.setLinea(linea); token.setIndex(index-token.getLexema().length());
                            token.setTipoToken(Token.TipoToken.ERROR);
                            bReader.reset();
                        }
                        break;
                }
                
            }
        } catch (IOException ioe) {
            System.err.println("No se puede hacer parsing sin cargar un archivo primero: "
                    + ioe.getLocalizedMessage());
            token = null;
        }
        return token;
    }
    
    
    public boolean cerrarArchivo(){
        try {
            bReader.close();
            fileReader.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    private void incrementarIndice(int caracter){
        index++;
        if ((char)caracter == '\n'){
            linea++;
            indice = 0;
        } else {
            indice++;
        }
    }
}
