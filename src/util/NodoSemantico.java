package util;

import tiny.Token;

/**
 *
 * @author diego
 */
public class NodoSemantico {
    
    public static enum type {
        INT,REAL,BOOLEAN,VOID,UNDEFINED
    }
    
    private String valor;
    private type tipo = type.UNDEFINED;
    private Token token;
    
    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the tipo
     */
    public type getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(type tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the token
     */
    public Token getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(Token token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "Token: " + token.getLexema() + "|" + "Valor: " + valor + "| Tipo: " + tipo; 
    }
}
