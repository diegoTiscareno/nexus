/*
 * 
 * Analizador sintáctico descendente recursivo para el lenguaje de Tiny++
 * 
 */
package tiny;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author diego
 */
public class TinySintaxis {
    
    private List tokens; //ésta es la lista de tokens lexicamente correctos
    private Token tokenAct; //aquí se guarda el token que se analiza actualmente
    private int index; //éste es el índice en la lista de tokens del token actual
    private DefaultMutableTreeNode raiz; //Raíz del árbol sintáctico. Sirve para referenciarlo desde fuera
    private DefaultMutableTreeNode nodoActual; //nodo actual del análisis, solo para uso interno del análisis
    private List listaErrores;
    //El contructor requiere de la lista del análisis lexico que se debe
    //realizar previamete
    public TinySintaxis(List listaAnalisis) {
        this.tokens = listaAnalisis;
        index = 0;
        tokenAct = (Token)tokens.get(index);
        raiz = new DefaultMutableTreeNode();
        nodoActual = raiz;
        listaErrores = new ArrayList();
    }
    
    //programa -> "main" "{" lista-declaración lista-sentencias "}"
    private void programa() {
        
        if(match (Token.TipoToken.MAIN)) {
            nodoActual.setUserObject(tokenAct);
            next();
        } else {
            nodoActual.setUserObject("Error");
            listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": Función principal esperada.");
        }
        
        if (!match (Token.TipoToken.LEFT_CURLY)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": { esperado.");
        } else {
            next();
        }
        
        listaDeclaracion();
        listaSentencias(nodoActual);
        
        if (match (Token.TipoToken.EOF)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": se llegó al fin de archivo sin encontrar \"}\"");
        } else {
            next();
            if (!match (Token.TipoToken.EOF)) {
                listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": Sentencias después del fin de la función principal");
            }
        }
        
    }
    
    //Lista-declaración -> declaracion ; lista-declaración | vacio
    //Lista-declaración -> {declaración ;}
    private void listaDeclaracion() {
        while (declaracion()) {
            if (!match(Token.TipoToken.SEMICOLON)) {
                listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": ; esperado.");
            } else {
                next();
            }
        }
    }
    
    //declaración-> tipo lista-variables    
    private boolean declaracion() {
        boolean tipo;
        tipo = tipo();
        if (tipo) {
            listaVariables();
        } else {
            return false;
        }
        return true;
    }
    
    //tipo -> int | real | boolean | void
    private boolean tipo() {
        DefaultMutableTreeNode nuevoHijo;
        if (match(Token.TipoToken.INT) ||match(Token.TipoToken.REAL) ||
                match(Token.TipoToken.BOOLEAN) || match(Token.TipoToken.VOID)) {
            nuevoHijo = new DefaultMutableTreeNode(tokenAct);
            nodoActual.add(nuevoHijo);
            nodoActual = nuevoHijo;
            next();
            return true;
        } else {
            return false;
        }
    }
    
    //lista-variables -> lista-variables, identificador | identificador
    //lista-variables -> identificador {,identificador}
    private void listaVariables() {
        DefaultMutableTreeNode nuevoHijo;
        if (match(Token.TipoToken.IDENTIFIER)) {
            nuevoHijo = new DefaultMutableTreeNode(tokenAct);
            nodoActual.add(nuevoHijo);
            next();
            while (match(Token.TipoToken.COMMA)) {
                next();
                if (match(Token.TipoToken.IDENTIFIER)){
                    nuevoHijo = new DefaultMutableTreeNode(tokenAct);
                    nodoActual.add(nuevoHijo);
                    next();
                }  else {
                    listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": \"" + tokenAct.getLexema() + "\" no es un identificador.");
                }
            }
        } else {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": Identificador esperado");
        }
        nodoActual = (DefaultMutableTreeNode) nodoActual.getParent();
    }
    
    //lista-sentencias -> sentencia; lista-sentencias | vacio
    //lista-sentencias -> {sentencia;}
    private DefaultMutableTreeNode listaSentencias(DefaultMutableTreeNode padre) {
        DefaultMutableTreeNode nuevoHijo;
        
        while (!(match (Token.TipoToken.RIGHT_CURLY) || match (Token.TipoToken.END)
                || match (Token.TipoToken.UNTIL) || match (Token.TipoToken.ELSE)
                || match (Token.TipoToken.EOF))) {
            nuevoHijo = sentencia();
            if (nuevoHijo != null) {
                padre.add(nuevoHijo);
            } else {
                System.out.println("Te encontré!" + tokenAct);
            }
            if (!match (Token.TipoToken.SEMICOLON)) {
                listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": \";\" esperado.");
            } else {
                next();
            }
        }
        if (match (Token.TipoToken.EOF)) {
            listaErrores.add("Error: Se llegó al fin de archivo haciendo Parsing.");
        }
        return padre;
        
    }
    
    //sentencia -> selección | iteración | repetición | sent-in | sent-out | asignación
    private DefaultMutableTreeNode sentencia() {
        DefaultMutableTreeNode nuevoHijo = null;
        System.out.println("entrando en sentencia con " + tokenAct);
        switch (tokenAct.getTipoToken ()) {
            case IF:
                nuevoHijo = seleccion();
                break;
            case WHILE:
                nuevoHijo = iteracion();
                break;
            case DO:
                nuevoHijo = repeticion();
                break;
            case CIN:
                nuevoHijo = sentIn();
                break;
            case COUT:
                nuevoHijo = sentOut();
                break;
            case IDENTIFIER:
                System.out.println("Identificador encontrado");
                nuevoHijo = asignacion();
                break;
            default:
                listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": No es una sentencia.");
                next();
        }
        return nuevoHijo;
    }
    
    //selección -> if "(" expresión ")" lista-sentencias end | 
    //             if "(" expresión ")" lista-sentencias else lista-sentencias end
    //             
    private DefaultMutableTreeNode seleccion() {
        DefaultMutableTreeNode nuevoHijo, hijoVerdadero, hijoFalso, exp;
        nuevoHijo = new DefaultMutableTreeNode(tokenAct);
        next();
        if (!match (Token.TipoToken.LEFT_PARENS)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": \"(\" esperado.");
        } else {
            next();
        }
        exp = expresion();
        if (exp != null) {
            nuevoHijo.add(exp);
        } else {
            nuevoHijo.add(new DefaultMutableTreeNode("¿Expresion?"));
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": expresion esperada.");
        }
        if (!match (Token.TipoToken.RIGHT_PARENS)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": \")\" esperado.");
        } else {
            next();
        }
        hijoVerdadero = new DefaultMutableTreeNode(new Token(Token.TipoToken.THEN, 
                "then", tokenAct.getIndice(), tokenAct.getLinea(), tokenAct.getIndex()));
        
        listaSentencias(hijoVerdadero);
        nuevoHijo.add(hijoVerdadero);
        
        if (match (Token.TipoToken.ELSE)) {
            hijoFalso = new DefaultMutableTreeNode(tokenAct);
            next();
            listaSentencias(hijoFalso);
            nuevoHijo.add(hijoFalso);
        } 
        
        if (!match (Token.TipoToken.END)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": \"end\" esperado.");
        }
        else {
            next();
        }
        return nuevoHijo;
    }
    
    //iteracion -> while (expresion) "{" lista-sentencias "}"
    private DefaultMutableTreeNode iteracion(){
        DefaultMutableTreeNode nuevoHijo, sentencias, exp;
        System.out.println("Entrando en iteracion");
        nuevoHijo = new DefaultMutableTreeNode(tokenAct);
        next();
        
        if (!match (Token.TipoToken.LEFT_PARENS)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": \"(\" esperado.");
        } else {
            next();
            System.out.println("avanzando en iteración después de  ( a " + tokenAct);
        }
        System.out.println("Intentando formar expresión en iteracion");
        exp = expresion();
        if (exp != null) {
            nuevoHijo.add(exp);
        } else {
            nuevoHijo.add(new DefaultMutableTreeNode("¿Expresion?"));
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": expresion esperada.");
        }
        
        System.out.println("Expresión de iteracion terminada de formar");
        
        if (!match (Token.TipoToken.RIGHT_PARENS)) {
            System.out.println("Parentesis no encontrados");
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": \")\" esperado.");
        } else {
            next();
        }
        
        if (!match (Token.TipoToken.LEFT_CURLY)) {
            System.out.println("llaves no encontradas");
            listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": { esperado.");
        } else {
            next();
        }
        sentencias = new DefaultMutableTreeNode(new Token(Token.TipoToken.THEN, 
                "then", tokenAct.getIndice(), tokenAct.getLinea(), tokenAct.getIndex()));
        listaSentencias(sentencias);
        nuevoHijo.add(sentencias); 
        if (!match (Token.TipoToken.RIGHT_CURLY)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() + 
                    ": } esperado.");
        } else {
            System.out.println("encontrado } de while, avanzando");
            next();
            System.out.println(tokenAct);
        }
        System.out.println("Saliendo de iteracion");
        return nuevoHijo;
    }
    
    //repeticion -> do lista-sentencias until expresion
    private DefaultMutableTreeNode repeticion(){
        DefaultMutableTreeNode nuevoHijo, sentencias, exp;
        nuevoHijo = new DefaultMutableTreeNode(tokenAct);
        next();
        sentencias = new DefaultMutableTreeNode("sentencias");
        
        listaSentencias(sentencias);
        nuevoHijo.add(sentencias);
        
        if (match (Token.TipoToken.UNTIL)) {
            next();
            exp = expresion();
            if (exp != null) {
                nuevoHijo.add(exp);
            } else {
            nuevoHijo.add(new DefaultMutableTreeNode("¿Expresion?"));
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": expresion esperada.");
            }
        } 
        return nuevoHijo;
        
    }
    
    //sent-in -> cin identificador
    private DefaultMutableTreeNode sentIn() {
         DefaultMutableTreeNode nuevoHijo;
         nuevoHijo = new DefaultMutableTreeNode(tokenAct);
         next();
         
        if (!match (Token.TipoToken.IDENTIFIER)) {
            listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                ": identificador esperado.");
        } else {
            nuevoHijo.add(new DefaultMutableTreeNode(tokenAct));
            next();
        }
        return nuevoHijo;
    }
    
    //sent-out -> cout expresion
    private DefaultMutableTreeNode sentOut() {
        DefaultMutableTreeNode nuevoHijo, exp;
        nuevoHijo = new DefaultMutableTreeNode(tokenAct);
        next();
        exp = expresion();
        if (exp != null) {
            nuevoHijo.add(exp);
        } else {
        nuevoHijo.add(new DefaultMutableTreeNode("¿Expresion?"));
        listaErrores.add("Error en la línea " + tokenAct.getLinea() +
            ": expresion esperada.");
        }
        return nuevoHijo;
    }
    
    //asignación -> identificador = expresión | identificador ++ | identificador --
    private DefaultMutableTreeNode asignacion() {
        DefaultMutableTreeNode nuevoHijo, nuevoHijoR = null, nuevoHijoA, nuevoHijoB;
        nuevoHijo = new DefaultMutableTreeNode(tokenAct);
        next(); 
        switch (tokenAct.getTipoToken()) {
            case ASSIGN:
                nuevoHijoR = new DefaultMutableTreeNode(tokenAct);
                next();
                nuevoHijoA = expresion();
                nuevoHijoR.add(nuevoHijo);
                if (nuevoHijoA != null) {
                    nuevoHijoR.add(nuevoHijoA);
                }
                else {
                nuevoHijoR.add(new DefaultMutableTreeNode("¿Expresion?"));
                listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": expresion esperada.");
                }
                break;
            case ITERATION:
                next();
                nuevoHijoA = new DefaultMutableTreeNode(new Token(Token.TipoToken.NUMBER,
                        "1", -1, -1, -1));
                nuevoHijoB = new DefaultMutableTreeNode(new Token(Token.TipoToken.MINUS,
                        "+", -1, -1, -1));
                nuevoHijoR = new DefaultMutableTreeNode(new Token(Token.TipoToken.ASSIGN,
                        "=", -1, -1, -1));
                nuevoHijoB.add(nuevoHijo);
                nuevoHijoB.add(nuevoHijoA);
                nuevoHijoR.add(new DefaultMutableTreeNode(nuevoHijo.getUserObject()));
                nuevoHijoR.add(nuevoHijoB);
                break;
            case DECREMENT:
                next();
                nuevoHijoA = new DefaultMutableTreeNode(new Token(Token.TipoToken.NUMBER,
                        "1", -1, -1, -1));
                nuevoHijoB = new DefaultMutableTreeNode(new Token(Token.TipoToken.MINUS,
                        "-", -1, -1, -1));
                nuevoHijoR = new DefaultMutableTreeNode(new Token(Token.TipoToken.ASSIGN,
                        "=", -1, -1, -1));
                nuevoHijoB.add(nuevoHijo);
                nuevoHijoB.add(nuevoHijoA);
                nuevoHijoR.add(new DefaultMutableTreeNode(nuevoHijo.getUserObject()));
                nuevoHijoR.add(nuevoHijoB);
                break;
        }
        return nuevoHijoR;
        
    }
    
    //expresión -> expresión relación-op expresión-simple | expresión-simple
    //expresión -> expresión-simple {relación-op expresión-simple}
    private DefaultMutableTreeNode expresion() {
        DefaultMutableTreeNode nuevoHijo;
        nuevoHijo = expresionSimple();
        while (relacionOp()) {
            DefaultMutableTreeNode nuevoHijoA = new DefaultMutableTreeNode(tokenAct);
            nuevoHijoA.add(nuevoHijo);
            nuevoHijo = nuevoHijoA;
            next();
            nuevoHijoA.add(expresionSimple());
        }
        return nuevoHijo;
    }
    
    //relacionOp -> <= | <= | < | > | == | !=
    private boolean relacionOp() {
        if (match (Token.TipoToken.LESS_THAN) || match (Token.TipoToken.LESS_EQUAL)
                || match (Token.TipoToken.GREATER_THAN) || match(Token.TipoToken.GREATER_EQUAL)
                || match (Token.TipoToken.EQUALS) || match (Token.TipoToken.NOT_EQUALS)) {
            return true;
        } else {
            return false;
        }
    }
    
    //expresion-simple -> expresión-simple suma-op termino | termino
    //expresión.simple -> término{suma-op termino}
    private DefaultMutableTreeNode expresionSimple() {
        DefaultMutableTreeNode nuevoHijo;
        System.out.println("ExpresionSimple! " + tokenAct);
        nuevoHijo = termino();
        while (sumaOp()) {
            System.out.println("Ciclo exp simp");
            DefaultMutableTreeNode nuevoHijoA = new DefaultMutableTreeNode(tokenAct);
            nuevoHijoA.add(nuevoHijo);
            nuevoHijo = nuevoHijoA;
            next();
            nuevoHijoA.add(termino());
        }
        return nuevoHijo;
    }
    
    //suma-op -> + | - 
    private boolean sumaOp() {
        if (match (Token.TipoToken.PLUS) || match (Token.TipoToken.MINUS)) {
            return true;
        } else {
            return false;
        }
    }
    
    //termino -> termino mult-op signed-Expresion | signed-Expresion
    //termino -> factor {mult.op factor}
    private DefaultMutableTreeNode termino(){
        DefaultMutableTreeNode nuevoHijo, fact;
        System.out.println("Termino! " + tokenAct);
        nuevoHijo = signedExpresion();
        while (multOp()) {
            System.out.println("ciclo Term");
            DefaultMutableTreeNode nuevoHijoA = new DefaultMutableTreeNode(tokenAct);
            nuevoHijoA.add(nuevoHijo);
            nuevoHijo = nuevoHijoA;
            next();
            fact = signedExpresion();
            if (fact != null) {
                nuevoHijoA.add(fact);
            }
        }
        return nuevoHijo;
    }
    
    //mult-op -> * | /
    private boolean multOp() {
        if (match (Token.TipoToken.ASTERISK) || match (Token.TipoToken.SLASH)) {
            return true;
        } else {
            return false;
        }
    }

    //signed-expresion-> suma-op factor | factor
    private DefaultMutableTreeNode signedExpresion(){
        DefaultMutableTreeNode nuevoHijo, fact;
        if (sumaOp()){
            nuevoHijo = new DefaultMutableTreeNode(tokenAct);
            next();
            fact = factor();
            if (fact != null) {
                nuevoHijo.add(fact);
            } else {
                nuevoHijo.add(new DefaultMutableTreeNode("¿Expresión?"));
                listaErrores.add("Error en la línea " + tokenAct.getLinea() + ": expresión esperada");
            }
            
        } else {
            nuevoHijo = factor();
        }
        return nuevoHijo;
    }
    
    //factor -> (expresión) | número | identificador
    private DefaultMutableTreeNode factor() {
        DefaultMutableTreeNode nuevoHijo = null, exp;
        System.out.println("Factor! ");
        switch (tokenAct.getTipoToken()) {
            case LEFT_PARENS:
                next();
                exp = expresion();
                if (exp != null) {
                    nuevoHijo = exp;
                } else {
                    nuevoHijo.add(new DefaultMutableTreeNode("¿Expresion?"));
                    listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                        ": expresion esperada.");
                }
                if (!match (Token.TipoToken.RIGHT_PARENS)) {
                listaErrores.add("Error en la línea " + tokenAct.getLinea() +
                    ": \")\" esperado.");
                } else {
                    next();
                }
                break;
            case NUMBER:
                System.out.println("Encontrado número en factor, creando nodo");
                nuevoHijo = new DefaultMutableTreeNode(tokenAct);
                next();
                break;
            case REAL_NUMBER:
                nuevoHijo = new DefaultMutableTreeNode(tokenAct);
                next();
                break;
            case IDENTIFIER:
                System.out.println("encontrado id en factor, creando nodo");
                nuevoHijo = new DefaultMutableTreeNode(tokenAct);
                next();
                break;
        }
        return nuevoHijo;
    }
    
    //match() es la función que compara tokens esperados
    private boolean match(Token.TipoToken tok){
        boolean valret;
        if (tok == tokenAct.getTipoToken()) {
           valret = true;
        } else {
           valret = false;
        }
        return valret;
    }

    //avanza el análisis
    private void next() {
        index++;
        tokenAct = (Token)tokens.get(index);
    }

    public DefaultMutableTreeNode getRaiz() {
        return raiz;
    }

    public List getListaErrores() {
        return listaErrores;
    }

    public void analize() {
        programa();
    }

}
